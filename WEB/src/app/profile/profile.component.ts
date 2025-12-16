// src/app/components/profile/profile.component.ts

import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService } from './services/profile.service';
import { UserProfile, VehicleInfo, PasswordChange } from './models/user-profile.model';

@Component({
	selector: 'app-profile',
	standalone: true,
	imports: [CommonModule, FormsModule],
	templateUrl: './profile.component.html',
	styleUrls: ['./profile.component.css']
})
export class ProfileComponent {
	private profileService = inject(ProfileService);

	// Tab state
	activeTab = signal<'personal' | 'vehicle'>('personal');

	// Form state signals
	userForm = signal<UserProfile>({ ...this.profileService.userProfile() });
	vehicleForm = signal<VehicleInfo>({ ...this.profileService.vehicleInfo() });
	passwordForm = signal<PasswordChange>({
		newPassword: '',
		confirmPassword: ''
	});

	// Service signals (readonly)
	userProfile = this.profileService.userProfile;
	vehicleInfo = this.profileService.vehicleInfo;
	vehicleTypes = this.profileService.vehicleTypes;
	availableServices = this.profileService.availableServices;

	// UI state
	isSaving = signal(false);
	saveMessage = signal<string>('');
	notificationCount = signal(3);

	constructor() {
		// Initialize forms with current data
		this.resetUserForm();
		this.resetVehicleForm();
	}

	// Tab navigation
	setActiveTab(tab: 'personal' | 'vehicle'): void {
		this.activeTab.set(tab);
	}

	// Reset forms to current profile data
	resetUserForm(): void {
		this.userForm.set({ ...this.profileService.userProfile() });
	}

	resetVehicleForm(): void {
		this.vehicleForm.set({ ...this.profileService.vehicleInfo() });
	}

	// Save user profile
	async saveUserProfile(): Promise<void> {
		this.isSaving.set(true);

		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 500));

		this.profileService.updateUserProfile(this.userForm());
		this.isSaving.set(false);
		this.showSaveMessage('Personal data saved successfully');
	}

	// Save vehicle info
	async saveVehicleInfo(): Promise<void> {
		this.isSaving.set(true);

		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 500));

		this.profileService.updateVehicleInfo(this.vehicleForm());
		this.isSaving.set(false);
		this.showSaveMessage('Vehicle data saved successfully');
	}

	// Change password
	async changePassword(): Promise<void> {
		const { newPassword, confirmPassword } = this.passwordForm();

		if (newPassword !== confirmPassword) {
			this.showSaveMessage('Passwords do not match', true);
			return;
		}

		if (newPassword.length < 8) {
			this.showSaveMessage('Password must be at least 8 characters', true);
			return;
		}

		this.isSaving.set(true);
		const success = await this.profileService.changePassword(newPassword, confirmPassword);
		this.isSaving.set(false);

		if (success) {
			this.showSaveMessage('Password changed successfully');
			this.passwordForm.set({ newPassword: '', confirmPassword: '' });
		} else {
			this.showSaveMessage('Failed to change password', true);
		}
	}

	// Toggle additional service
	toggleService(serviceId: string): void {
		this.vehicleForm.update(current => {
			const services = current.additionalServices.includes(serviceId)
				? current.additionalServices.filter(id => id !== serviceId)
				: [...current.additionalServices, serviceId];

			return { ...current, additionalServices: services };
		});
	}

	// Check if service is enabled
	isServiceEnabled(serviceId: string): boolean {
		return this.vehicleForm().additionalServices.includes(serviceId);
	}

	// Show save message
	private showSaveMessage(message: string, isError: boolean = false): void {
		this.saveMessage.set(message);
		setTimeout(() => this.saveMessage.set(''), 3000);
	}

	// Handle photo upload
	onPhotoUpload(event: Event): void {
		const input = event.target as HTMLInputElement;
		if (input.files && input.files[0]) {
			const file = input.files[0];
			const reader = new FileReader();

			reader.onload = (e) => {
				const photoUrl = e.target?.result as string;
				this.userForm.update(current => ({ ...current, photoUrl }));
			};

			reader.readAsDataURL(file);
		}
	}
}
