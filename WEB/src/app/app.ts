import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { routeAnimations } from '@core/animations/route-animation';
import { NavbarComponent } from "./layout/navbar/component/navbar.component";
import { NotificationSidebarComponent } from './layout/notifications-sidebar/notifications-sidebar.component';
import { UserChatComponent } from './layout/user-chat/user-chat.component';

@Component({
	selector: 'app-root',
	imports: [RouterOutlet, NavbarComponent, NotificationSidebarComponent, UserChatComponent],
	templateUrl: './app.html',
	styleUrl: './app.css',
	animations: [routeAnimations],
})
export class App {
	protected readonly title = signal('WEB');
	// Signal to control notification sidebar visibility
	notificationSidebarOpen = signal<boolean>(false);
	/**
	 * Toggle notification sidebar
	 */
	toggleNotificationSidebar(): void {
		this.notificationSidebarOpen.update((open) => !open);
	}

	/**
	 * Open notification sidebar
	 */
	openNotificationSidebar(): void {
		this.notificationSidebarOpen.set(true);
	}

	/**
	 * Close notification sidebar
	 */
	closeNotificationSidebar(): void {
		this.notificationSidebarOpen.set(false);
	}
	prepareRoute(outlet: RouterOutlet) {
		return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
	}
}
