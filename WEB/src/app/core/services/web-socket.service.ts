import { Injectable, signal } from '@angular/core';
import SockJS from 'sockjs-client';
import { CompatClient, Stomp, StompSubscription } from '@stomp/stompjs';

export interface WebSocketMessage<T = any> {
	body: T;
	headers?: Record<string, string>;
}

@Injectable({
	providedIn: 'root',
})
export class WebSocketService {
	private stompClient: CompatClient | null = null;
	private subscriptions = new Map<string, StompSubscription>();
	private subscriptionRefCounts = new Map<string, number>();
	private topicCallbacks = new Map<string, Set<(msg: any) => void>>();

	// Signal to track connection state
	private _connected = signal<boolean>(false);
	readonly connected = this._connected.asReadonly();

	private connectionPromise: Promise<void> | null = null;

	/**
	 * Subscribe to a WebSocket topic
	 * Automatically connects if not already connected
	 * Uses reference counting - multiple subscriptions to the same topic are allowed
	 *
	 * @param topic - The topic to subscribe to (e.g., '/topic/panic', '/topic/driver-locations')
	 * @param callback - Function to call when a message is received
	 * @returns Unsubscribe function
	 */
	async subscribe<T = any>(topic: string, callback: (message: T) => void): Promise<() => void> {
		await this.ensureConnected();

		const callbacks = this.topicCallbacks.get(topic) ?? new Set();
		callbacks.add(callback);
		this.topicCallbacks.set(topic, callbacks);

		const currentRefCount = this.subscriptionRefCounts.get(topic) || 0;
		this.subscriptionRefCounts.set(topic, currentRefCount + 1);

		if (!this.subscriptions.has(topic)) {
			console.log(`[WebSocket] Subscribing to topic: ${topic}`);

			const stompSub = this.stompClient!.subscribe(topic, (message: any) => {
				let parsedBody: any;
				try {
					parsedBody = message.body ? JSON.parse(message.body) : null;
				} catch {
					parsedBody = message.body;
				}

				// 🔥 call ALL callbacks
				this.topicCallbacks.get(topic)?.forEach((cb) => cb(parsedBody));
			});

			this.subscriptions.set(topic, stompSub);
		}

		return () => this.unsubscribe(topic, callback);
	}

	/**
	 * Unsubscribe from a topic
	 * Uses reference counting - only actually unsubscribes when all references are gone
	 * Disconnects when no subscriptions remain
	 *
	 * @param topic - The topic to unsubscribe from
	 * @param callback
	 */
	private unsubscribe(topic: string, callback: (msg: any) => void): void {
		const callbacks = this.topicCallbacks.get(topic);
		callbacks?.delete(callback);

		if (callbacks && callbacks.size === 0) {
			this.topicCallbacks.delete(topic);
		}

		const currentRefCount = this.subscriptionRefCounts.get(topic) || 0;

		if (currentRefCount <= 1) {
			console.log(`[WebSocket] Unsubscribing from topic: ${topic}`);

			const sub = this.subscriptions.get(topic);
			sub?.unsubscribe();

			this.subscriptions.delete(topic);
			this.subscriptionRefCounts.delete(topic);

			if (this.subscriptions.size === 0) {
				this.disconnect();
			}
		} else {
			this.subscriptionRefCounts.set(topic, currentRefCount - 1);
		}
	}

	/**
	 * Send a message to the backend
	 * Automatically connects if not already connected
	 * Server-side destination should use /app prefix
	 *
	 * @param destination - The destination to send to (e.g., '/app/driver/location')
	 * @param body - The message body (will be JSON stringified)
	 * @param headers - Optional headers
	 */
	async send<T = any>(
		destination: string,
		body: T,
		headers: Record<string, string> = {},
	): Promise<void> {
		// Ensure connection is established
		await this.ensureConnected();

		console.log(`[WebSocket] Sending message to: ${destination}`);

		try {
			this.stompClient!.send(destination, headers, JSON.stringify(body));
		} catch (error) {
			console.error(`[WebSocket] Error sending message to ${destination}:`, error);
			throw error;
		}
	}

	/**
	 * Ensure WebSocket connection is established
	 * Returns a promise that resolves when connected
	 */
	private async ensureConnected(): Promise<void> {
		if (this._connected()) {
			return Promise.resolve();
		}

		// If connection is in progress, wait for it
		if (this.connectionPromise) {
			return this.connectionPromise;
		}

		// Start new connection
		this.connectionPromise = this.connect();
		return this.connectionPromise;
	}

	/**
	 * Establish WebSocket connection
	 */
	private connect(): Promise<void> {
		console.log('[WebSocket] Connecting...');

		return new Promise((resolve, reject) => {
			const ws = new SockJS('/socket', null, {});
			this.stompClient = Stomp.over(ws);

			// Configure debug logging (disable in production)
			this.stompClient.debug = (str: string) => {
				console.log('[WebSocket Debug]', str);
			};

			this.stompClient.connect(
				{},
				// Success callback
				() => {
					console.log('[WebSocket] Connected successfully');
					this._connected.set(true);
					this.connectionPromise = null;
					resolve();
				},
				// Error callback
				(error: any) => {
					console.error('[WebSocket] Connection error:', error);
					this._connected.set(false);
					this.connectionPromise = null;
					this.stompClient = null;
					reject(error);
				},
			);
		});
	}

	/**
	 * Disconnect from WebSocket
	 * Called automatically when no subscriptions remain
	 */
	private disconnect(): void {
		if (!this.stompClient) return;

		console.log('[WebSocket] Disconnecting...');

		try {
			this.stompClient.disconnect(() => {
				console.log('[WebSocket] Disconnected successfully');
			});
		} catch (error) {
			console.error('[WebSocket] Error during disconnect:', error);
		}

		this.stompClient = null;
		this._connected.set(false);
		this.connectionPromise = null;
	}

	/**
	 * Force disconnect (for logout, etc.)
	 * Clears all subscriptions
	 */
	forceDisconnect(): void {
		console.log('[WebSocket] Force disconnect - clearing all subscriptions');

		// Clear all subscriptions
		this.subscriptions.forEach((sub) => sub.unsubscribe());
		this.subscriptions.clear();
		this.subscriptionRefCounts.clear();

		this.disconnect();
	}

	/**
	 * Get current connection status
	 */
	isConnected(): boolean {
		return this._connected();
	}

	/**
	 * Get active subscription count
	 */
	getSubscriptionCount(): number {
		return this.subscriptions.size;
	}

	/**
	 * Get all active topics
	 */
	getActiveTopics(): string[] {
		return Array.from(this.subscriptions.keys());
	}
}
