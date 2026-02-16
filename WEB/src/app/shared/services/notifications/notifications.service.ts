import { effect, inject, Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { WebSocketService } from '@core/services/web-socket.service';
import { AuthService } from '@core/services/auth.service';
import { environment } from '@environments/environment';
import { firstValueFrom } from 'rxjs';
import { Router } from '@angular/router';

export interface Notification {
	id: number;
	title: string;
	message: string;
	timestamp: string;
	read: boolean;
	additionalData?: string; // URL to navigate to
}

@Injectable({
	providedIn: 'root',
})
export class NotificationService {
	private http = inject(HttpClient);
	private webSocketService = inject(WebSocketService);
	private authService = inject(AuthService);
	private router = inject(Router);

	private unsubscribeFn: (() => void) | null = null;

	// Signals for notifications
	private _notifications = signal<Notification[]>([]);
	readonly notifications = this._notifications.asReadonly();

	private _loading = signal<boolean>(false);
	readonly loading = this._loading.asReadonly();

	private _error = signal<string | null>(null);
	readonly error = this._error.asReadonly();

	// Computed values
	readonly unreadCount = computed(() => this._notifications().filter((n) => !n.read).length);

	readonly unreadNotifications = computed(() => this._notifications().filter((n) => !n.read));

	readonly readNotifications = computed(() => this._notifications().filter((n) => n.read));

	private hasInitialFetch = false;

	private subscribedUserId: number | null = null;

	constructor() {
		this.setupNotificationSubscription();
	}

	/**
	 * Setup effect to subscribe/unsubscribe based on user login
	 */
	private setupNotificationSubscription(): void {
		effect(() => {
			const user = this.authService.user();

			// logout case
			if (!user) {
				if (this.subscribedUserId !== null) {
					this.unsubscribeFromNotifications();
					this.clearNotifications();
					this.hasInitialFetch = false;
					this.subscribedUserId = null;
				}
				return;
			}

			// 🔥 prevent resubscribe loop
			if (this.subscribedUserId === user.id) {
				return;
			}

			this.subscribedUserId = user.id;

			this.subscribeToNotifications(user.id).then();

			if (!this.hasInitialFetch) {
				this.fetchAllNotifications().then();
			}
		});

	}

	/**
	 * Fetch all notifications from the server
	 */
	async fetchAllNotifications(): Promise<void> {
		const user = this.authService.user();
		if (!user) return;

		this._loading.set(true);
		this._error.set(null);

		try {
			const notifications = await firstValueFrom(
				this.http.get<Notification[]>(`/api/${environment.apiVersion}/notifications`),
			);

			// Sort by timestamp (newest first)
			const sortedNotifications = notifications.sort(
				(a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime(),
			);

			this._notifications.set(sortedNotifications);
			this.hasInitialFetch = true;

			console.log(`[NotificationService] Fetched ${notifications.length} notifications`);
		} catch (error) {
			console.error('[NotificationService] Error fetching notifications:', error);
			this._error.set('Failed to load notifications');
		} finally {
			this._loading.set(false);
		}
	}

	/**
	 * Subscribe to user-specific notifications via WebSocket
	 */
	private async subscribeToNotifications(userId: number): Promise<void> {
		// Unsubscribe from previous subscription if exists
		this.unsubscribeFromNotifications();
		this.requestNotificationPermission().then()
		console.log(`[NotificationService] Subscribing to notifications for user ${userId}`);

		try {
			this.unsubscribeFn = await this.webSocketService.subscribe<Notification>(
				`/topic/notifications/${userId}`,
				(notificationDTO) => this.handleNewNotification(notificationDTO),
			);
		} catch (error) {
			console.error('[NotificationService] Error subscribing to notifications:', error);
		}
	}

	/**
	 * Unsubscribe from notifications
	 */
	private unsubscribeFromNotifications(): void {
		if (this.unsubscribeFn) {
			console.log('[NotificationService] Unsubscribing from notifications');
			this.unsubscribeFn();
			this.unsubscribeFn = null;
		}
	}

	/**
	 * Handle incoming notification from WebSocket
	 */
	private handleNewNotification(notification: Notification): void {
		console.log('[NotificationService] New notification received:', notification);

		// Add to notifications list (prepend for newest first)
		this._notifications.update((notifications) => [notification, ...notifications]);

		// Show browser notification if supported and permitted
		this.showBrowserNotification(notification);

		// Play notification sound (optional)
		this.playNotificationSound();
	}

	/**
	 * Mark notification as read
	 */
	async markAsRead(notificationId: number): Promise<void> {
		// Optimistically update UI
		this._notifications.update((notifications) =>
			notifications.map((n) => (n.id === notificationId ? { ...n, read: true } : n)),
		);

		// Update on server
		try {
			await firstValueFrom(
				this.http.patch(
					`/api/${environment.apiVersion}/notifications/${notificationId}/read`,
					{},
				),
			);
			console.log(`[NotificationService] Notification ${notificationId} marked as read`);
		} catch (error) {
			console.error('[NotificationService] Error marking notification as read:', error);

			// Revert on error
			this._notifications.update((notifications) =>
				notifications.map((n) => (n.id === notificationId ? { ...n, read: false } : n)),
			);
		}
	}

	/**
	 * Mark all notifications as read
	 */
	async markAllAsRead(): Promise<void> {
		// Get all unread notification IDs
		const unreadIds = this._notifications()
			.filter((n) => !n.read)
			.map((n) => n.id);

		if (unreadIds.length === 0) return;

		// Optimistically update UI
		this._notifications.update((notifications) =>
			notifications.map((n) => ({ ...n, read: true })),
		);

		// Update on server
		try {
			await firstValueFrom(
				this.http.patch(`/api/${environment.apiVersion}/notifications/read-all`, {}),
			);
			console.log('[NotificationService] All notifications marked as read');
		} catch (error) {
			console.error('[NotificationService] Error marking all as read:', error);

			// Revert on error
			this._notifications.update((notifications) =>
				notifications.map((n) => ({
					...n,
					read: unreadIds.includes(n.id) ? false : n.read,
				})),
			);
		}
	}

	/**
	 * Delete a notification
	 */
	async deleteNotification(notificationId: number): Promise<void> {
		// Store for potential rollback
		const notificationToDelete = this._notifications().find((n) => n.id === notificationId);

		// Optimistically remove from UI
		this._notifications.update((notifications) =>
			notifications.filter((n) => n.id !== notificationId),
		);

		// Delete on server
		try {
			await firstValueFrom(
				this.http.delete(`/api/${environment.apiVersion}/notifications/${notificationId}`),
			);
			console.log(`[NotificationService] Notification ${notificationId} deleted`);
		} catch (error) {
			console.error('[NotificationService] Error deleting notification:', error);

			// Revert on error
			if (notificationToDelete) {
				this._notifications.update((notifications) =>
					[notificationToDelete, ...notifications].sort(
						(a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime(),
					),
				);
			}
		}
	}

	/**
	 * Clear all read notifications
	 */
	async clearReadNotifications(): Promise<void> {
		const readNotifications = this._notifications().filter((n) => n.read);

		if (readNotifications.length === 0) return;

		// Optimistically update UI
		this._notifications.update((notifications) => notifications.filter((n) => !n.read));

		// Delete on server
		try {
			await firstValueFrom(
				this.http.delete(`/api/${environment.apiVersion}/notifications/read`),
			);
			console.log('[NotificationService] Read notifications cleared');
		} catch (error) {
			console.error('[NotificationService] Error clearing read notifications:', error);

			// Revert on error
			this._notifications.update((notifications) =>
				[...notifications, ...readNotifications].sort(
					(a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime(),
				),
			);
		}
	}

	/**
	 * Show browser notification
	 */
	private showBrowserNotification(notification: Notification): void {
		if ('Notification' in window && Notification.permission === 'granted') {
			const browserNotification = new Notification(notification.title, {
				body: notification.message,
				icon: 'assets/notification-icon.png',
				badge: 'assets/notification-badge.png',
				tag: notification.id.toString(),
				requireInteraction: false,
			});
			browserNotification.onclick = () => {
				if (notification.additionalData) {
					this.router.navigate([notification.additionalData]).then();
				}
			}
			// Auto-close after 5 seconds
			setTimeout(() => browserNotification.close(), 5000);
		}
	}

	/**
	 * Play notification sound
	 */
	private playNotificationSound(): void {
		try {
			const audio = new Audio('assets/notification-sound.mp3');
			audio.volume = 0.5;
			audio.play().catch(() => {
				// Ignore errors (e.g., user hasn't interacted with page yet)
			});
		} catch (error) {
			// Ignore errors
		}
	}

	/**
	 * Clear all notifications
	 */
	clearNotifications(): void {
		this._notifications.set([]);
	}

	/**
	 * Request browser notification permission
	 */
	async requestNotificationPermission(): Promise<NotificationPermission> {
		if (!('Notification' in window)) {
			return 'denied';
		}
		if (Notification.permission === 'granted') {
			return 'granted';
		}
		if (Notification.permission === 'denied') {
			return 'denied';
		}
		return await Notification.requestPermission();
	}

	/**
	 * Refresh notifications from server
	 */
	async refresh(): Promise<void> {
		await this.fetchAllNotifications();
	}

	/**
	 * Manual cleanup (called on logout)
	 */
	cleanup(): void {
		this.unsubscribeFromNotifications();
		this.clearNotifications();
		this.hasInitialFetch = false;
	}
}
