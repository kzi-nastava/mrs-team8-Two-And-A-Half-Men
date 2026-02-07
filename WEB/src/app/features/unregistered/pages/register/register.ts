import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { UnregisteredAuthService } from '@features/unregistered/services/unregistered-auth.service';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-register',
	imports: [ReactiveFormsModule, CommonModule, RouterModule],
	templateUrl: './register.html',
	standalone: true,
	styleUrls: ['./register.css'],
})
export class Register {
	private unregisteredAuthService = inject(UnregisteredAuthService);
	private router = inject(Router);
	private popupsService = inject(PopupsService);

	errorMessage = signal<string | null>(null);
	isSubmitButtonDisabled = signal(false);

	step = 1;
	registerForm = new FormGroup({
		firstName: new FormControl('', [Validators.required]),
		lastName: new FormControl('', [Validators.required]),
		phoneNumber: new FormControl('', [
			Validators.required,
			Validators.pattern('^\\+?[1-9]\\d{1,14}$'),
		]),
		address: new FormControl('', [Validators.required]),
		email: new FormControl('', [Validators.required, Validators.email]),
		password: new FormControl('', [Validators.required, Validators.minLength(6)]),
		confirmPassword: new FormControl('', [Validators.required]),
	});

	onSubmit() {
		if (!this.registerForm.valid) {
			alert('Please fill in the form correctly.');
			return;
		}

		const firstName = this.registerForm.get('firstName')?.value ?? '';
		const lastName = this.registerForm.get('lastName')?.value ?? '';
		const phoneNumber = this.registerForm.get('phoneNumber')?.value ?? '';
		const address = this.registerForm.get('address')?.value ?? '';
		const email = this.registerForm.get('email')?.value ?? '';
		const password = this.registerForm.get('password')?.value ?? '';
		const confirmPassword = this.registerForm.get('confirmPassword')?.value ?? '';

		this.errorMessage.set(null);
		if (password !== confirmPassword) {
			this.errorMessage.set('Passwords do not match');
			return;
		}
		const user = {
			firstName,
			lastName,
			phone: phoneNumber,
			address,
			email,
			password,
		};
		const result = this.unregisteredAuthService.register(user);
		this.isSubmitButtonDisabled.set(true);
		result.subscribe({
			next: () => {
				this.popupsService.success(
					'Success',
					'Registered successfully! Please check your email to activate your account.',
					{
						onConfirm: () => this.router.navigate(['/login']).then(),
					},
				);
				this.isSubmitButtonDisabled.set(false);
			},
			error: (err) => {
				this.errorMessage.set(err.error?.error || 'An error occurred during registration');
				this.isSubmitButtonDisabled.set(false);
			},
		});
	}
	previous() {
		this.step = this.step - 1;
	}
	next() {
		if (this.isValid()) {
			this.step = this.step + 1;
		}
	}
	isValid(): boolean {
		if (this.step === 1) {
			if (this.registerForm.get('firstName')?.invalid) {
				this.registerForm.get('firstName')?.markAsTouched();
			}
			if (this.registerForm.get('lastName')?.invalid) {
				this.registerForm.get('lastName')?.markAsTouched();
			}
			if (this.registerForm.get('phoneNumber')?.invalid) {
				this.registerForm.get('phoneNumber')?.markAsTouched();
			}
			if (this.registerForm.get('address')?.invalid) {
				this.registerForm.get('address')?.markAsTouched();
			}
			return (
				!!this.registerForm.get('firstName')?.valid &&
				!!this.registerForm.get('lastName')?.valid &&
				!!this.registerForm.get('phoneNumber')?.valid &&
				!!this.registerForm.get('address')?.valid
			);
		}
		return false;
	}
}
