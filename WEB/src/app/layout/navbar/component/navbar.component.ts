import { Component, computed, effect, inject, input, model } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarRenderer } from '../renderer/navbar-renderer';
import { NavbarButton, NavbarSettings } from '../models/navbar-models';
import { AuthService } from 'src/app/core/services/auth.service';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';
import { Router } from '@angular/router';
import { NotificationService } from '@shared/services/notifications/notifications.service';

@Component({
	selector: 'app-navbar',
	imports: [NavbarRenderer, CommonModule],
	templateUrl: './navbar.component.html',
	styleUrl: './navbar.component.css',
})
export class NavbarComponent {
	private authService = inject(AuthService);
	private router = inject(Router);
	private notificationsService = inject(NotificationService);

	buttons = computed(() => {
		const user = this.authService.user();
		const imgSrc = user?.imgSrc; // Track imgSrc changes

		if (user == null) {
			return this.unregisteredButtons;
		} else if (user.role === LoggedInUserRole.CUSTOMER) {
			return this.customerButtons;
		} else if (user.role === LoggedInUserRole.DRIVER) {
			return this.driverButtons;
		} else if (user.role === LoggedInUserRole.ADMIN) {
			return this.adminButtons;
		}
		return [];
	});

	navbarConfig = computed(
		() =>
			({
				isSvgLogo: false,
				logoUrl: 'assets/taxi.svg',
				logoText: 'Taxi Taxi',
				backgroundColor: '#2d7a4f',
				textColor: '#ffffff',
				logoRoute: '/',
				buttons: this.buttons(),
			}) as NavbarSettings,
	);
	notificationClick = input.required<() => void>();

	private get unregisteredButtons(): NavbarButton[] {
		return [
			{
				id: 'login',
				type: 'text',
				label: 'Login',
				position: 'right',
				route: '/login',
			},
			{
				id: 'register',
				type: 'text',
				label: 'Register',
				position: 'right',
				route: '/register',
			},
		];
	}

	private get loggedInUserButtons(): NavbarButton[] {
		return [
			{
				id: 'history',
				type: 'text',
				label: 'Ride History',
				position: 'right',
				route: '/history',
			},
			{
				id: 'reports',
				type: 'text',
				label: 'Reports',
				position: 'right',
				route: '/reports',
			},
			{
				id: 'logout',
				type: 'text',
				label: 'Logout',
				position: 'right',
				onClick: () => {
					this.authService.logout();
					this.router.navigate(['/']).then();
				},
			},
			{
				id: 'notifications',
				type: 'notification',
				position: 'right',
				notificationCount: this.notificationsService.unreadCount(),
				onClick: () => {
					this.notificationClick()();
				},
				icon: 'assets/notification-icon.png',
			},
			{
				id: 'profile',
				type: 'icon',
				icon: this.authService.userProfileImage(),
				label: 'Profile',
				position: 'right',
				route: '/profile',
			},
		];
	}

	private get customerButtons(): NavbarButton[] {
		return [
			{
				id: 'rides',
				type: 'text',
				label: 'Booked Rides',
				position: 'right',
				route: '/rides/booked',
			},
			...this.loggedInUserButtons,
		];
	}
	private get driverButtons(): NavbarButton[] {
		return [
			...this.loggedInUserButtons,
		];
	}
	private get adminButtons(): NavbarButton[] {
		return [
			{
				id: 'users',
				type: 'text',
				label: 'Users',
				position: 'right',
				route: '/users',
			},
			{
				id: 'chats',
				type: 'text',
				label: 'Chats',
				position: 'right',
				route: '/chat',
			},
			{
				id: 'settings',
				type: 'text',
				label: 'Settings',
				position: 'right',
				route: '/settings',
			},
			...this.loggedInUserButtons,
		];
	}
	onNavbarButtonClick(buttonId: string): void {
	}
}
