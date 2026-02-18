import { Component, inject, OnInit, signal } from '@angular/core';
import { UnregisteredAuthService } from '@features/unregistered/services/unregistered-auth.service';
import { PopupsService } from '@shared/services/popups/popups.service';
import { Router } from '@angular/router';

@Component({
	selector: 'app-activation-page',
	imports: [],
	templateUrl: './activation-page.component.html',
	styleUrls: ['./activation-page.component.css'],
})
export class ActivationComponent implements OnInit {
	private hasSendRequest: boolean = false;
	private popupsService = inject(PopupsService);
	private router = inject(Router);

	private unregisteredAuthService = inject(UnregisteredAuthService);

	ngOnInit(): void {
		const urlParams = new URLSearchParams(window.location.search);
		const token = urlParams.get('token');
		if (this.hasSendRequest) {
			return;
		}
		this.hasSendRequest = true;
		if (!token) {
			this.popupsService.error('Error', 'Invalid activation-page link.', {
				onConfirm: () => this.router.navigate(['/']),
			});
			return;
		}
		this.unregisteredAuthService.activateAccount(token).subscribe({
			next: (response) => {
				this.popupsService.success('Success', response.message, {
					onConfirm: () => this.router.navigate(['/login']),
				});
			},
			error: () => {
				this.popupsService.error('Error', 'Activation failed. Please try again.');
			},
		});
	}
}
