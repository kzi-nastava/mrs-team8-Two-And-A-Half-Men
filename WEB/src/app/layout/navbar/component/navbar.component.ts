import { Component, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarRenderer } from '../renderer/navbar-renderer';
import { NavbarButton, NavbarSettings } from '../models/navbar-models';
import { AuthService } from 'src/app/core/services/auth.service';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';
import { Router } from '@angular/router';

@Component({
	selector: 'app-navbar',
	imports: [NavbarRenderer, CommonModule],
	templateUrl: './navbar.component.html',
	styleUrl: './navbar.component.css',
})
export class NavbarComponent {
	private authService = inject(AuthService);
	private router = inject(Router);
	navbarConfig: NavbarSettings = {
		isSvgLogo: true,
		logoUrl: `  <svg width="32" height="32" viewBox="0 0 24 24" fill="none">
        <path d="M18 16.5L20 14M20 14L22 11.5M20 14L18 11.5M20 14L22 16.5"
              stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <rect x="2" y="6" width="16" height="12" rx="2" stroke="white" stroke-width="2"/>
        <path d="M2 10H18M6 14H6.01M10 14H10.01"
              stroke="white" stroke-width="2" stroke-linecap="round"/>
      </svg>`,
		logoText: 'Taxi Taxi',
		backgroundColor: '#2d7a4f',
		textColor: '#ffffff',
		logoRoute: '/',
		buttons: [],
	};

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
				notificationCount: 5,
				icon: 'assets/notification-icon.png',
			},
			{
				id: 'profile',
				type: 'icon',
				icon: this.authService.user()?.imgSrc || 'assets/default-profile.png',
				label: 'Profile',
				position: 'right',
				route: '/profile',
			},
		];
	}

	private get customerButtons(): NavbarButton[] {
		return [
			{
				id: 'history',
				type: 'text',
				label: 'Ride History',
				position: 'right',
				route: '/history',
			},
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
			{
				id: 'history',
				type: 'text',
				label: 'Ride History',
				position: 'right',
				route: '/driver/history',
			},
			...this.loggedInUserButtons,
		];
	}
	private get adminButtons(): NavbarButton[] {
		return [
			{
				id: 'history',
				type: 'text',
				label: 'Ride History',
				position: 'right',
				route: '/admin/history',
			},
			{
				id: 'active-rides',
				type: 'text',
				label: 'Active Rides',
				position: 'right',
				route: '/admin/active-rides',
			},
			{
				id: 'users',
				type: 'text',
				label: 'Users',
				position: 'right',
				route: '/admin/users',
			},
			{
				id: 'settings',
				type: 'text',
				label: 'Settings',
				position: 'right',
				route: '/admin/settings',
			},
			...this.loggedInUserButtons,
		];
	}
	constructor() {
		effect(() => {
			if (this.authService.user() == null) {
				this.navbarConfig.buttons = this.unregisteredButtons;
			} else if (this.authService.user()?.role === LoggedInUserRole.CUSTOMER) {
				this.navbarConfig.buttons = this.customerButtons;
			} else if (this.authService.user()?.role === LoggedInUserRole.DRIVER) {
				this.navbarConfig.buttons = this.driverButtons;
			} else if (this.authService.user()?.role === LoggedInUserRole.ADMIN) {
				this.navbarConfig.buttons = this.adminButtons;
			}
		});
	}
	onNavbarButtonClick(buttonId: string): void {
		console.log(`Navbar button clicked: ${buttonId}`);
	}
}
