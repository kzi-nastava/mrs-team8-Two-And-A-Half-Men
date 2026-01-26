import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TabNavigationComponent, TabItem } from '@shared/components/tabs/tab-navigation/tab-navigation.component';
import { TabIconDirective } from '@shared/directives/tab-icon/tab-icon.directive';
import { TabContentComponent } from '@shared/components/tabs/tab-content/tab-content.component';
import { PersonalInfoFormComponent } from '@shared/components/forms/personal-info-form/personal-info-form.component';
import { VehicleInfoFormComponent } from '@shared/components/forms/vehicle-info-form/vehicle-info-form.component';
import { VehicleInfo, VehicleType, AdditionalService } from '@shared/models/vehicles.model';
import { PersonalInfo } from '@shared/models/personal-info.model';
import {PasswordChange, UserProfile} from './models/user-profile.model';
import { BoxDirective} from '@shared/directives/box/box.directive';
import {
	RequestChangePreviewComponent
} from '@features/profile/components/request-change-preview/request-change-preview.component';
import {PendingChangeRequest} from '@shared/models/profile-change-request.model';

@Component({
	selector: 'app-profile',
	standalone: true,
	imports: [
		CommonModule,
		FormsModule,
		TabNavigationComponent,
		TabIconDirective,
		TabContentComponent,
		PersonalInfoFormComponent,
		VehicleInfoFormComponent,
		BoxDirective,
		RequestChangePreviewComponent
	],
	templateUrl: './profile.component.html',
	styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
	// Active tab
	activeTab = signal<string>('personal');

	// Form data
	personalInfo = signal<PersonalInfo>({
		firstName: '',
		lastName: '',
		phoneNumber: '',
		address: '',
		email: '',
		imgSrc: null
	});

	vehicleInfo = signal<VehicleInfo>({
		type: '',
		model: '',
		licensePlate: '',
		numberOfSeats: 4,
		additionalServices: []
	});
	passwordForm = signal<PasswordChange>({
		oldPassword: '',
		newPassword: '',
		confirmPassword: ''
	});

	// Options from backend
	vehicleTypes = signal<VehicleType[]>([]);
	availableServices = signal<AdditionalService[]>([]);
	changeRequest = signal<PendingChangeRequest | null>({
		id: 1,
		firstName: 'hello',
		lastName: 'world',
		phoneNumber: '+38160000010',
		address: 'Some other street 5',
		additionalServices: ['aaaa', 'bbbb', 'ccccccc'],
		imgSrc: 'https://media.istockphoto.com/id/814423752/photo/eye-of-model-with-colorful-art-make-up-close-up.jpg?s=612x612&w=0&k=20&c=l15OdMWjgCKycMMShP8UK94ELVlEGvt7GmB_esHWPYE='
	});

	// UI state
	saveMessage = signal<string>('');
	isSaving = signal<boolean>(false);

	// Tab configuration
	tabs: TabItem[] = [
		{ id: 'personal', label: 'Personal data', position: 'left' },
		{ id: 'vehicle', label: 'Vehicle data', position: 'left' }
	];

	ngOnInit() {
		this.loadProfileData();
		this.loadVehicleOptions();
	}

	loadProfileData() {
		// Simulate API call - replace with actual service
		const mockData: UserProfile = {
			personalInfo: {
				id: 101,
				firstName: 'Mike',
				lastName: 'Driver',
				phoneNumber: '+38160000003',
				address: 'Driver Street 10',
				email: 'driver@test.com',
				imgSrc: null,
				role: 'DRIVER'
			},
			vehicleInfo: {
				id: 1,
				type: 'Standard',
				model: 'Toyota Prius',
				licensePlate: 'NS-123-AB',
				numberOfSeats: 4,
				additionalServices: ['Baby seat', 'WiFi', 'Pet friendly']
			}
		};

		this.personalInfo.set(mockData.personalInfo);
		this.vehicleInfo.set(mockData.vehicleInfo!);
	}

	loadVehicleOptions() {
		// Simulate API call - replace with actual service
		const mockOptions = {
			vehicleTypes: [
				{ id: 1, typeName: 'Standard', description: 'Standard passenger vehicle for everyday rides.', price: 1 },
				{ id: 51, typeName: 'Comfort', description: 'More spacious and comfortable vehicle with extra legroom.', price: 1.3 },
				{ id: 101, typeName: 'Premium', description: 'High-end vehicle with luxury interior and superior comfort.', price: 1.7 },
				{ id: 151, typeName: 'Van', description: 'Larger vehicle suitable for groups or extra luggage.', price: 1.5 },
				{ id: 201, typeName: 'Electric', description: 'Eco-friendly electric vehicle with zero emissions.', price: 1.4 }
			],
			additionalServices: [
				{ id: 1, name: 'Pet friendly', description: 'Pets are allowed during the ride as long as they are properly secured.' },
				{ id: 51, name: 'Baby seat', description: 'Vehicle is equipped with a certified child safety seat.' },
				{ id: 101, name: 'Smoking allowed', description: 'Smoking is permitted inside the vehicle during the ride.' },
				{ id: 151, name: 'Wheelchair accessible', description: 'Vehicle supports wheelchair access with ramp or lift.' },
				{ id: 201, name: 'WiFi', description: 'Free onboard WiFi available for passengers.' }
			]
		};

		this.vehicleTypes.set(mockOptions.vehicleTypes);
		this.availableServices.set(mockOptions.additionalServices);
	}

	onTabChange(tabId: string): void {
		this.activeTab.set(tabId);
	}

	onPhotoChange(file: File): void {
		const reader = new FileReader();

		reader.onload = (e) => {
			const photoUrl = e.target?.result as string;
			this.personalInfo.update(current => ({ ...current, photoUrl }));
		};

		reader.readAsDataURL(file);
	}

	saveProfile(): void {
		this.isSaving.set(true);

		// Simulate API call
		setTimeout(() => {
			console.log('Saving personal info:', this.personalInfo());
			this.saveMessage.set('Personal information saved successfully!');
			this.isSaving.set(false);

			// Clear message after 3 seconds
			setTimeout(() => this.saveMessage.set(''), 3000);
		}, 1000);
	}


	// Show save message
	private showSaveMessage(message: string, isError: boolean = false): void {
		this.saveMessage.set(message);
		setTimeout(() => this.saveMessage.set(''), 3000);
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
		const success = true; //await this.profileService.changePassword(newPassword, confirmPassword);
		this.isSaving.set(false);

		if (success) {
			this.showSaveMessage('Password changed successfully');
			this.passwordForm.set({ oldPassword: '', newPassword: '', confirmPassword: '' });
		} else {
			this.showSaveMessage('Failed to change password', true);
		}
	}
}
