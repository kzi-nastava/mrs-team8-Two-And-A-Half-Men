import { effect, inject, Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { WebSocketService } from '@core/services/web-socket.service';
import { AuthService } from '@core/services/auth.service';
import { environment } from '@environments/environment';
import { firstValueFrom } from 'rxjs';
import { Router } from '@angular/router';
import { UserRole } from '@shared/models/user-role';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export interface PanicNotification {
	passengerName?: string;
	driverName: string;
	location: string;
	rideId: string;
}

@Injectable({
	providedIn: 'root',
})
export class PanicsService {
	private http = inject(HttpClient);
	private webSocketService = inject(WebSocketService);
	private authService = inject(AuthService);
	private router = inject(Router);

	private unsubscribeFn: (() => void) | null = null;

	private subscribedUserId: number | null = null;

	constructor() {
		console.log('[PanicService] Initializing PanicService');
		this.setupPanicSubscription();
	}


	private setupPanicSubscription(): void {
		effect(() => {
			const user = this.authService.user();

			if (!user || (user.role && user.role !== LoggedInUserRole.ADMIN)) {
				if (this.subscribedUserId !== null) {
					this.unsubscribeFromPanic();
					this.subscribedUserId = null;
				}
				return;
			}

			// 🔥 prevent resubscribe loop
			if (this.subscribedUserId === user.id) {
				return;
			}

			this.subscribedUserId = user.id;

			this.subscribeToPanic().then();
		});

	}

	private async subscribeToPanic(): Promise<void> {
		this.unsubscribeFromPanic();
		this.requestNotificationPermission().then()
		console.log(`[PanicService] Subscribing to panic`);

		try {
			this.unsubscribeFn = await this.webSocketService.subscribe<PanicNotification>(
				`/topic/panic`,
				(PanicNotification) => this.handleNewNotification(PanicNotification),
			);
		} catch (error) {
			console.error('[PanicService] Error subscribing to panic:', error);
		}
	}

	/**
	 * Unsubscribe from panic
	 */
	private unsubscribeFromPanic(): void {
		if (this.unsubscribeFn) {
			console.log('[PanicService] Unsubscribing from panic');
			this.unsubscribeFn();
			this.unsubscribeFn = null;
		}
	}

	/**
	 * Handle incoming notification from WebSocket
	 */
	private handleNewNotification(notification: PanicNotification): void {
		console.log('[PanicService] New notification received:', notification);
		this.showBrowserNotification(notification);
		//this.playPanicSound();
	}

	private showBrowserNotification(notification: PanicNotification): void {
		console.log('[PanicService] Attempting to show browser notification');
		if ('Notification' in window && Notification.permission === 'granted') {
			console.log('[PanicService] Showing browser notification');
			const browserNotification = new Notification("Panic Alert", {
				body: `Driver:`,
				icon: '/assets/panic-icon.png',
				tag: notification.rideId,
				requireInteraction: false,
			});

			browserNotification.onclick = () => {
				window.focus();
				this.router.navigate(['rides', notification.rideId]).then();
			}
			// Auto-close after 5 seconds
			setTimeout(() => browserNotification.close(), 5000);
		}
		else {
			console.warn('[PanicService] Browser notifications not supported or permission not granted');
		}
	}

	private playPanicSound(): void {
		try {
			const audio = new Audio('/assets/panic-sound.mp3');
			audio.volume = 0.5;
			audio.play().catch(() => {
				// Ignore errors (e.g., user hasn't interacted with page yet)
			});
		} catch (error) {
			// Ignore errors
		}
	}

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

	cleanup(): void {
		this.unsubscribeFromPanic();
	}
}
