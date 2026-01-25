// src/app/components/profile/profile.component.ts

import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService } from './services/profile.service';
import { UserProfile, VehicleInfo, PasswordChange } from './models/user-profile.model';
import {TabItem, TabNavigationComponent} from '@shared/components/tab-navigation/tab-navigation.component';
import {TabContentComponent} from '@shared/components/tab-content/tab-content.component';

@Component({
	selector: 'app-profile',
	standalone: true,
	imports: [CommonModule, FormsModule, TabNavigationComponent, TabContentComponent],
	templateUrl: './profile.component.html',
	styleUrls: ['./profile.component.css']
})
export class ProfileComponent {
	private profileService = inject(ProfileService);

	// Active tab state
	activeTab = signal<string>('personal');

	// Define tabs with icons
	tabs: TabItem[] = [
		{
			id: 'personal',
			label: 'Personal data',
			icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" stroke="currentColor" stroke-width="2"/></svg>'
		},
		{
			id: 'vehicle',
			label: 'Vehicle data',
			icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none"><path d="M5 17h14M5 17a2 2 0 1 1 0-4M5 17a2 2 0 1 0 0-4m14 4a2 2 0 1 0 0-4m0 4a2 2 0 1 1 0-4M3 11l2-7h14l2 7M5 13h14" stroke="currentColor" stroke-width="2"/></svg>'
		}
	];

	// Handle tab change
	onTabChange(tabId: string): void {
		this.activeTab.set(tabId);
	}
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
