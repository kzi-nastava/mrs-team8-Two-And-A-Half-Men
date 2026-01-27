import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { UnregisteredAuthService } from '@features/unregistered/services/unregistered-auth.service';
import { ButtonDirective } from '@shared/directives/button/button.directive';

@Component({
	selector: 'app-forgot-password',
	imports: [ReactiveFormsModule, ButtonDirective],
	templateUrl: './forgot-password.html',
	styleUrls: ['./forgot-password.css'],
})
export class ForgotPassword {
	private unregisteredAuthService = inject(UnregisteredAuthService);

	forgotPasswordForm = new FormGroup({
		username: new FormControl('', [Validators.required, Validators.email]),
	});

	onSubmit() {
		if (this.forgotPasswordForm.valid) {
			const username = (this.forgotPasswordForm.get('username')?.value ?? '') as string;
			this.unregisteredAuthService.forgotPassword(username).subscribe({
				next: () => {
					Swal.fire('Success', 'Link sent to your email', 'success').then();
				},
				error: () => {
					Swal.fire(
						'Error',
						'Failed to send password reset email. Please try again.',
						'error',
					).then();
				},
			});
		}
	}
}
