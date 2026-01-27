import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
import { UnregisteredAuthService } from '@features/unregistered/services/unregistered-auth.service';

@Component({
	selector: 'app-restart-password',
	imports: [ReactiveFormsModule],
	templateUrl: './restart-password.html',
	styleUrl: './restart-password.css',
})
export class RestartPassword {
	private unregisteredAuthService = inject(UnregisteredAuthService);
	private router = inject(Router);

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
				Swal.fire('Success', 'Password has been reset successfully.', 'success').then();
				this.router.navigate(['/login']).then();
			},
			error: () => {
				Swal.fire('Error', 'Failed to reset password. Please try again.', 'error').then();
			},
		});
	}
}
