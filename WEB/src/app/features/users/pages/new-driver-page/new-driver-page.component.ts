import { Component, inject, OnInit, signal } from '@angular/core';
import { BoxDirective } from '@shared/directives/box/box.directive';
import { PersonalInfoFormComponent } from '@shared/components/forms/personal-info-form/personal-info-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { TabContentComponent } from '@shared/components/tabs/tab-content/tab-content.component';
import { TabIconDirective } from '@shared/directives/tab-icon/tab-icon.directive';
import {
	TabItem,
	TabNavigationComponent,
} from '@shared/components/tabs/tab-navigation/tab-navigation.component';
import { VehicleInfoFormComponent } from '@shared/components/forms/vehicle-info-form/vehicle-info-form.component';
import { PersonalInfo } from '@shared/models/personal-info.model';
import { AdditionalService, VehicleInfo, VehicleType } from '@shared/models/vehicles.model';
import { AdminUserService } from '@features/users/services/admin-user.service';
import { VehiclesService } from '@shared/services/vehicles/vehicles.service';
import { DriverRegistrationRequest } from '@features/users/models/drivers.model';
import { Router } from '@angular/router';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-new-driver-page',
	imports: [
		BoxDirective,
		PersonalInfoFormComponent,
		ReactiveFormsModule,
		TabContentComponent,
		TabIconDirective,
		TabNavigationComponent,
		VehicleInfoFormComponent,
	],
	templateUrl: './new-driver-page.component.html',
	styleUrl: './new-driver-page.component.css',
})
export class NewDriverPageComponent implements OnInit {
	private usersService = inject(AdminUserService);
	private vehiclesService = inject(VehiclesService);
	private router = inject(Router);
	private popupsService = inject(PopupsService);

	// Form data
	personalInfo = signal<PersonalInfo>({
		firstName: '',
		lastName: '',
		phoneNumber: '',
		address: '',
		email: '',
		imgSrc: null,
	});

	vehicleInfo = signal<VehicleInfo>({
		type: '',
		additionalServices: [],
		model: '',
		numberOfSeats: 0,
		licensePlate: '',
	});

	// Options from backend
	vehicleTypes = signal<VehicleType[]>([]);
	availableServices = signal<AdditionalService[]>([]);

	// UI state
	saveMessage = signal<string>('');
	isSaving = signal<boolean>(false);

	// Active tab
	activeTab = signal<string>('personal');

	private personalTab: TabItem = { id: 'personal', label: 'Personal data', position: 'left' };
	private vehicleTab: TabItem = { id: 'vehicle', label: 'Vehicle data', position: 'left' };

	get tabs(): TabItem[] {
		return [this.personalTab, this.vehicleTab];
	}

	ngOnInit() {
		this.loadVehicleOptions();
	}

	loadVehicleOptions() {
		this.vehiclesService.getVehicleOptions().subscribe({
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

	saveChanges(): void {
		this.isSaving.set(true);

		const personalInfo = this.personalInfo();
		const vehicleInfo = this.vehicleInfo();
		const types = this.vehicleTypes();
		const services = this.availableServices();

		const registrationRequest: DriverRegistrationRequest = {
			personalInfo: {
				firstName: personalInfo.firstName,
				lastName: personalInfo.lastName,
				email: personalInfo.email,
				address: personalInfo.address,
				phoneNumber: personalInfo.phoneNumber,
			},
			vehicleInfo: {
				model: vehicleInfo.model,
				licensePlate: vehicleInfo.licensePlate,
				numberOfSeats: vehicleInfo.numberOfSeats,
				typeId: types?.find((type) => type.typeName === vehicleInfo.type)?.id ?? -1,
				additionalServicesIds: vehicleInfo.additionalServices
					.map((serviceName) => {
						const service = services.find((s) => s.name === serviceName);
						return service ? service.id : -1;
					})
					.filter((id) => id !== null) as number[],
			},
		};
		console.log(registrationRequest);
		this.usersService.registerDriver(registrationRequest).subscribe({
			next: () => {
				this.isSaving.set(false);
				this.popupsService.success(
					'Success',
					'Driver registered successfully!',
					{
						onConfirm: () => this.router.navigate(['users']).then()
					}
				);
			},
			error: (err) => {
				this.popupsService.error('Failed to register driver', err.error?.message || 'An error occurred while registering the driver.');
				this.isSaving.set(false);
				console.error(err);
			},
		});
	}
}
