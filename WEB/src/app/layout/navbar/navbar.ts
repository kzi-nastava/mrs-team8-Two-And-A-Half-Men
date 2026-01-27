// navbar.component.ts
import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { WebSocketService } from '@core/services/web-socket.service';

@Component({
	selector: 'app-navbar',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './navbar.html',
	styleUrls: ['./navbar.css'],
})
export class NavbarComponent {
	constructor(
		private router: Router,
		private webSocket: WebSocketService,
	) {}
	private isOk = signal(true);
	ngOnInit() {
		if (this.isOk()) {
			this.webSocket.connect();
			this.isOk.set(false);
		}
	}
	onProfileClick() {
		this.router.navigate(['/profile']);
	}

	onDriversRidesClick() {
		this.router.navigate(['/drivers-history']);
	}

	onLoginClick() {
		this.router.navigate(['/login']);
	}

	onRegisterClick() {
		this.router.navigate(['/register']);
	}
}
