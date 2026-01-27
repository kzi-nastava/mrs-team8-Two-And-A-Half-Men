import { Component, inject, OnInit, signal } from '@angular/core';
import { UnregisteredAuthService } from '@features/unregistered/services/unregistered-auth.service';

@Component({
	selector: 'app-activation-page',
	imports: [],
	templateUrl: './activation-page.component.html',
	styleUrls: ['./activation-page.component.css'],
})
export class ActivationComponent implements OnInit {
	message = signal('');
	private hasSendRequest: boolean = false;

	private unregisteredAuthService = inject(UnregisteredAuthService);

	ngOnInit(): void {
		const urlParams = new URLSearchParams(window.location.search);
		const token = urlParams.get('token');
		if (this.hasSendRequest) {
			return;
		}
		this.hasSendRequest = true;
		if (!token) {
			this.message.set('Invalid activation-page link.');
			return;
		}
		this.unregisteredAuthService.activateAccount(token).subscribe({
			next: (response) => {
				this.message.set(response.message);
			},
			error: () => {
				this.message.set('Activation failed. Please try again.');
			},
		});
	}
}
