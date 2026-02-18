import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UnregisteredAuthService } from '@features/unregistered/services/unregistered-auth.service';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-restart-password',
	imports: [ReactiveFormsModule],
	templateUrl: './restart-password.html',
	styleUrl: './restart-password.css',
})
export class RestartPassword {
	private unregisteredAuthService = inject(UnregisteredAuthService);
	private router = inject(Router);
	private popupsService = inject(PopupsService);

	restartPasswordForm = new FormGroup({
		password: new FormControl('', [Validators.required, Validators.minLength(6)]),
		confirmPassword: new FormControl('', [Validators.required]),
	});

	onSubmit() {
		if (!this.restartPasswordForm.valid) {
			return;
		}
		const password = (this.restartPasswordForm.get('password')?.value ?? '') as string;
		const confirmPassword = (this.restartPasswordForm.get('confirmPassword')?.value ??
			'') as string;
		if (password !== confirmPassword) {
			this.restartPasswordForm.get('confirmPassword')?.setErrors({ mismatch: true });
			return;
		}
		const token = new URLSearchParams(window.location.search).get('token') || '';
		if (!token) {
			return;
		}
		// Call the Auth service to reset the password
		this.unregisteredAuthService.restartPassword(token, password).subscribe({
			next: () => {
				this.popupsService.success('Success', 'Password has been reset successfully.', {
					onConfirm: () => this.router.navigate(['/login']),
				});
			},
			error: () => {
				this.popupsService.error('Error', 'Failed to reset password. Please try again.');
			},
		});
	}
}
