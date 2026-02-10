import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { UnregisteredAuthService } from '@features/unregistered/services/unregistered-auth.service';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-login',
	standalone: true,
	imports: [ReactiveFormsModule, CommonModule, RouterModule],
	templateUrl: './login.html',
	styleUrls: ['./login.css'],
})
export class Login {
	private unregisteredAuthService = inject(UnregisteredAuthService);
	private router = inject(Router);
	private popupsService = inject(PopupsService);

	loginFailed = false;
	loginForm = new FormGroup({
		username: new FormControl('', [Validators.required, Validators.email]),
		password: new FormControl('', [Validators.required, Validators.minLength(6)]),
		rememberMe: new FormControl(false),
	});

	onSubmit() {
		if (this.loginForm.valid) {
			const username = this.loginForm.get('username')?.value ?? '';
			const password = this.loginForm.get('password')?.value ?? '';
			const rememberMe = !!this.loginForm.get('rememberMe')?.value;
			const loginData = { username, password };
			this.unregisteredAuthService.login(loginData, rememberMe).subscribe({
				next: () => {
					this.router.navigate(['']).then();
				},
				error: (err) => {
					this.loginFailed = true;
					this.popupsService.error('Login Failed', err.error?.message || 'An error occurred during login. Please try again.');
				},
			});
		} else {
			this.popupsService.error('Error', 'Please fill in the form correctly.');
		}
	}
}
