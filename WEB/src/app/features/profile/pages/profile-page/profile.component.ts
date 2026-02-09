import {Component, signal, OnInit, inject} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TabNavigationComponent, TabItem } from '@shared/components/tabs/tab-navigation/tab-navigation.component';
import { TabIconDirective } from '@shared/directives/tab-icon/tab-icon.directive';
import { TabContentComponent } from '@shared/components/tabs/tab-content/tab-content.component';
import { PersonalInfoFormComponent } from '@shared/components/forms/personal-info-form/personal-info-form.component';
import { VehicleInfoFormComponent } from '@shared/components/forms/vehicle-info-form/vehicle-info-form.component';
import { VehicleInfo, VehicleType, AdditionalService } from '@shared/models/vehicles.model';
import { PersonalInfo } from '@shared/models/personal-info.model';
import {PasswordChange, UserProfile} from '../../models/user-profile.model';
import { BoxDirective} from '@shared/directives/box/box.directive';
import {
	RequestChangePreviewComponent
} from '@features/profile/components/request-change-preview/request-change-preview.component';
import {PendingChangeRequest} from '@shared/models/profile-change-request.model';
import {ProfileService} from '@features/profile/services/profile.service';
import { AuthService } from '@core/services/auth.service';
import { UpdateProfileRequest } from '@features/profile/models/update-profile.model';
import { VehiclesService } from '@shared/services/vehicles/vehicles.service';
import { PopupsService } from '@shared/services/popups/popups.service';
import { Router } from '@angular/router';

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
		RequestChangePreviewComponent,
	],
	templateUrl: './profile.component.html',
	styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
	private profileService = inject(ProfileService);
	private authService = inject(AuthService);
	private vehicleService = inject(VehiclesService);
	private router = inject(Router);
	private popupsService = inject(PopupsService);

	// Active tab
	activeTab = signal<string>('personal');

	// Form data
	personalInfo = signal<PersonalInfo>({
		firstName: '',
		lastName: '',
		phoneNumber: '',
		address: '',
		email: '',
		imgSrc: null,
	});
	originalPersonalInfo = signal<PersonalInfo | null>(null);
	originalVehicleInfo = signal<VehicleInfo | null>(null);

	vehicleInfo = signal<VehicleInfo | null>(null);
	passwordForm = signal<PasswordChange>({
		oldPassword: '',
		newPassword: '',
		confirmPassword: '',
	});

	// Options from backend
	vehicleTypes = signal<VehicleType[]>([]);
	availableServices = signal<AdditionalService[]>([]);
	changeRequest = signal<PendingChangeRequest | null>(null);

	private selectedFile: File | null = null;

	// UI state
	isSaving = signal<boolean>(false);

	private personalTab: TabItem = { id: 'personal', label: 'Personal data', position: 'left' };
	private vehicleTab: TabItem = { id: 'vehicle', label: 'Vehicle data', position: 'left' };
	get tabs(): TabItem[] {
		const tabs: TabItem[] = [this.personalTab];
		if (this.vehicleInfo()) {
			tabs.push(this.vehicleTab);
		}
		return tabs;
	}

	ngOnInit() {
		this.loadProfileData();
	}

	private updateProfileDate(profile: UserProfile) {
		this.personalInfo.set(profile.personalInfo);
		this.originalPersonalInfo.set(JSON.parse(JSON.stringify(profile.personalInfo)));

		if (profile.vehicleInfo) {
			this.vehicleInfo.set(profile.vehicleInfo ?? null);
			this.originalVehicleInfo.set(JSON.parse(JSON.stringify(profile.vehicleInfo)));
			this.loadVehicleOptions();
		}
		if (profile.pendingChangeRequest) {
			this.changeRequest.set(profile?.pendingChangeRequest ?? null);
		}
		this.authService.updateUserInfo(profile.personalInfo);
	}

	loadProfileData() {
		this.profileService.getUserProfile().subscribe({
			next: (profile) => this.updateProfileDate(profile),
			error: (err) => {
				this.popupsService.error(
					'Error',
					'Failed to load profile data. Please try again later. ' + err.message,
					{
						onConfirm: () => this.router.navigate(['/']).then()
					}
				);
			},
		});
	}

	loadVehicleOptions() {
		this.vehicleService.getVehicleOptions().subscribe({
			next: (options) => {
				this.vehicleTypes.set(options.vehicleTypes);
				this.availableServices.set(options.additionalServices);
			},
			error: (err) => {
				console.error('Failed to load vehicle options', err);
			},
		});
	}

	onTabChange(tabId: string): void {
		this.activeTab.set(tabId);
	}

	onPhotoChange(file: File): void {
		this.selectedFile = file;

		const reader = new FileReader();

		reader.onload = (e) => {
			const photoUrl = e.target?.result as string;
			this.personalInfo.update((current) => ({ ...current, photoUrl }));
		};

		reader.readAsDataURL(file);
	}

	saveProfile(): void {
		this.isSaving.set(true);

		const personalInfo = this.personalInfo();
		const vehicleInfo = this.vehicleInfo();
		const types = this.vehicleTypes();
		const services = this.availableServices();

		const updateRequest: UpdateProfileRequest = {
			firstName: personalInfo.firstName,
			lastName: personalInfo.lastName,
			email: personalInfo.email,
			address: personalInfo.address,
			phoneNumber: personalInfo.phoneNumber,
			imgSrc: personalInfo.imgSrc,

			// Vehicle info
			model: vehicleInfo?.model ?? null,
			licensePlate: vehicleInfo?.licensePlate ?? null,
			numberOfSeats: vehicleInfo?.numberOfSeats ?? null,
			vehicleTypeId: types?.find((type) => type.typeName === vehicleInfo?.type)?.id ?? null,
			additionalServiceIds: vehicleInfo?.additionalServices
				.map((serviceName) => {
					const service = services.find((s) => s.name === serviceName);
					return service ? service.id : null;
				})
				.filter((id) => id !== null) as number[] | null,
		};

		if (this.selectedFile !== null) {
			this.profileService.uploadPhoto(this.selectedFile).subscribe({
				next: (response) => {
					if (!response.ok) {
						this.isSaving.set(false);
						this.popupsService.error('Error', 'Failed to upload Photo');
						return;
					}
					updateRequest.imgSrc = response.filePath;
					console.log(response, updateRequest);
					this.updateProfile(updateRequest);
				},
				error: (err) => {
					console.error('Failed to upload profile picture', err);
					this.isSaving.set(false);
					this.popupsService.error('Error', 'Failed to upload profile picture. Please try again later. ' + err.message);
				},
			});
			return;
		}
		this.updateProfile(updateRequest);
	}

	private updateProfile(updateRequest: UpdateProfileRequest): void {
		this.profileService.updateUserProfile(updateRequest).subscribe({
			next: (response) => {
				if (response.accessToken) {
					this.authService.updateToken(response.accessToken);
				}
				this.updateProfileDate(response.profile);

				this.popupsService.success('Profile Updated', 'Your profile information has been updated successfully!');
				this.isSaving.set(false);
			},
			error: (err) => {
				console.error('Failed to update profile', err);
				this.popupsService.error('Error', 'Failed to update profile information. Please try again later. ' + err.message);
				this.isSaving.set(false);
			},
		});
	}

	// Change password
	async changePassword(): Promise<void> {
		const { newPassword, confirmPassword } = this.passwordForm();

		if (newPassword !== confirmPassword) {
			this.popupsService.error('Validation Error', 'Passwords do not match');
			return;
		}

		if (newPassword.length < 8) {
			this.popupsService.error('Validation Error', 'Password must be at least 8 characters');
			return;
		}

		this.isSaving.set(true);
		this.profileService
			.changePassword({
				oldPassword: this.passwordForm().oldPassword,
				newPassword: this.passwordForm().newPassword,
				confirmNewPassword: this.passwordForm().confirmPassword,
			})
			.subscribe({
				next: (response) => {
					if (response.accessToken) {
						this.authService.updateToken(response.accessToken);
					}
					this.popupsService.success('Password Changed', 'Your password has been changed successfully!');
					this.passwordForm.set({
						oldPassword: '',
						newPassword: '',
						confirmPassword: '',
					});
					this.isSaving.set(false);
				},
				error: (err) => {
					console.error('Failed to change password', err);
					this.passwordForm.set({
						oldPassword: '',
						newPassword: '',
						confirmPassword: '',
					});
					this.popupsService.error('Error', 'Failed to change password');
					this.isSaving.set(false);
				},
			});
	}
}
