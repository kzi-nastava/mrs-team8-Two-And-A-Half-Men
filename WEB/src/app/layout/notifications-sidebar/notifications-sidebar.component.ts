import { Component, computed, inject, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationService } from '@shared/services/notifications/notifications.service';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
	selector: 'app-notification-sidebar',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './notifications-sidebar.component.html',
	styleUrls: ['./notifications-sidebar.component.css'],
	animations: [
		trigger('slideIn', [
			transition(':enter', [
				style({ transform: 'translateX(100%)' }),
				animate('300ms ease-out', style({ transform: 'translateX(0)' })),
			]),
			transition(':leave', [
				animate('300ms ease-in', style({ transform: 'translateX(100%)' })),
			]),
		]),
		trigger('fadeIn', [
			transition(':enter', [
				style({ opacity: 0 }),
				animate('200ms ease-out', style({ opacity: 1 })),
			]),
			transition(':leave', [animate('200ms ease-in', style({ opacity: 0 }))]),
		]),
	],
})
export class NotificationSidebarComponent {
	private router = inject(Router);
	notificationService = inject(NotificationService);

	// Output event to close sidebar
	close = output<void>();

	// Computed values
	notifications = this.notificationService.notifications;
	unreadNotifications = this.notificationService.unreadNotifications;
	readNotifications = this.notificationService.readNotifications;
	unreadCount = this.notificationService.unreadCount;
	loading = this.notificationService.loading;
	error = this.notificationService.error;

	hasNotifications = computed(() => this.notifications().length > 0);
	hasUnreadNotifications = computed(() => this.unreadNotifications().length > 0);

	/**
	 * Handle notification click
	 */
	async onNotificationClick(notification: any): Promise<void> {
		// Mark as read
		if (!notification.read) {
			await this.notificationService.markAsRead(notification.id);
		}

		// Navigate if additionalData (URL) is present
		if (notification.additionalData) {
			this.router.navigate([notification.additionalData]);
			this.closeSidebar();
		}
	}

	/**
	 * Mark all as read
	 */
	async markAllAsRead(): Promise<void> {
		await this.notificationService.markAllAsRead();
	}

	/**
	 * Delete notification
	 */
	async deleteNotification(event: Event, notificationId: number): Promise<void> {
		event.stopPropagation();
		await this.notificationService.deleteNotification(notificationId);
	}

	/**
	 * Clear all read notifications
	 */
	async clearRead(): Promise<void> {
		await this.notificationService.clearReadNotifications();
	}

	/**
	 * Refresh notifications
	 */
	async refresh(): Promise<void> {
		await this.notificationService.refresh();
	}

	/**
	 * Close sidebar
	 */
	closeSidebar(): void {
		this.close.emit();
	}

	/**
	 * Handle backdrop click
	 */
	onBackdropClick(event: Event): void {
		// Only close if clicking the backdrop itself, not children
		if (event.target === event.currentTarget) {
			this.closeSidebar();
		}
	}

	/**
	 * Format timestamp to relative time
	 */
	formatTimestamp(timestamp: string): string {
		const now = new Date();
		const notificationDate = new Date(timestamp);
		const diffInSeconds = Math.floor((now.getTime() - notificationDate.getTime()) / 1000);

		if (diffInSeconds < 60) {
			return 'Just now';
		} else if (diffInSeconds < 3600) {
			const minutes = Math.floor(diffInSeconds / 60);
			return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
		} else if (diffInSeconds < 86400) {
			const hours = Math.floor(diffInSeconds / 3600);
			return `${hours} hour${hours > 1 ? 's' : ''} ago`;
		} else if (diffInSeconds < 604800) {
			const days = Math.floor(diffInSeconds / 86400);
			return `${days} day${days > 1 ? 's' : ''} ago`;
		} else {
			return notificationDate.toLocaleDateString();
		}
	}

	/**
	 * Get notification icon based on title or type
	 */
	getNotificationIcon(notification: any): string {
		const title = notification.title.toLowerCase();

		if (title.includes('ride')) return '🚗';
		if (title.includes('payment')) return '💳';
		if (title.includes('driver')) return '👨‍✈️';
		if (title.includes('cancel')) return '❌';
		if (title.includes('complete')) return '✅';
		if (title.includes('panic')) return '🚨';
		if (title.includes('message')) return '💬';

		return '🔔'; // Default
	}
}
