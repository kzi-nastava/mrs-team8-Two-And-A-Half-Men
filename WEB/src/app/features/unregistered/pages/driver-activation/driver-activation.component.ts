import { Component, inject, OnInit, signal } from '@angular/core';
import { BoxDirective } from '@shared/directives/box/box.directive';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PopupsService } from '@shared/services/popups/popups.service';
import { UnregisteredAuthService } from '@features/unregistered/services/unregistered-auth.service';

@Component({
	selector: 'app-driver-activation',
	imports: [BoxDirective, FormsModule],
	templateUrl: './driver-activation.component.html',
	styleUrl: './driver-activation.component.css',
})
export class DriverActivationComponent implements OnInit {
	private activatedRoute = inject(ActivatedRoute);
	private popupsService = inject(PopupsService);
	private router = inject(Router);
	private unregisteredAuthService = inject(UnregisteredAuthService);

	passwordForm = signal({
		newPassword: '',
		confirmPassword: '',
	});
	private accessToken: string = '';

	isSaving = signal<boolean>(false);

	ngOnInit() {
		this.activatedRoute.queryParams.subscribe((params) => {
			const token = params['token'];
			if (!token) {
				this.popupsService.error('Error', 'Invalid activation link. No token provided.', {
					onConfirm: () => {
						this.router.navigate(['/']).then();
					},
				});
				return;
			}
			this.accessToken = token;
		});
	}

	activateAccount() {
		if (!this.accessToken) {
			return;
		}
		const password = this.passwordForm().newPassword;
		const confirmPassword = this.passwordForm().confirmPassword;

		if (password !== confirmPassword) {
			this.popupsService.error('Error', 'Passwords do not match.');
			return;
		}

		if (password.length < 8) {
			this.popupsService.error('Error', 'Password must be at least 8 characters long.');
			return;
		}

		this.isSaving.set(true);
		this.unregisteredAuthService.activateDriverAccount(this.accessToken, password).subscribe({
			next: (response) => {
				this.popupsService.success('Success', response.message, {
					onConfirm: () => {
						this.router.navigate(['/login']).then();
					},
				});
			},
			error: (error) => {
				this.popupsService.error(
					'Error',
					error?.error?.message || 'An error occurred during activation.',
				);
			},
			complete: () => {
				this.isSaving.set(false);
			},
		});
	}
}
