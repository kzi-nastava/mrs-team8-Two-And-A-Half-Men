// src/app/shared/renderer/vehicle-info-form/vehicle-info-form.component.ts

import { Component, input, model } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VehicleInfo, VehicleType, AdditionalService } from '@shared/models/vehicles.model';

@Component({
	selector: 'app-vehicle-info-form',
	standalone: true,
	imports: [CommonModule, FormsModule],
	templateUrl: './vehicle-info-form.component.html',
	styleUrls: ['./vehicle-info-form.component.css']
})
export class VehicleInfoFormComponent {
	// Two-way binding for vehicle data
	vehicleInfo = model.required<VehicleInfo>();

	// Vehicle options from backend
	vehicleTypes = input.required<VehicleType[]>();
	availableServices = input.required<AdditionalService[]>();

	// Configuration
	readonly = input<boolean>(false);

	isServiceEnabled(serviceName: string): boolean {
		return this.vehicleInfo().additionalServices.includes(serviceName);
	}

	toggleService(serviceName: string): void {
		if (this.readonly()) return;

		const services = this.vehicleInfo().additionalServices;
		const index = services.indexOf(serviceName);

		if (index > -1) {
			// Remove service
			this.vehicleInfo.update(info => ({
				...info,
				additionalServices: services.filter(s => s !== serviceName)
			}));
		} else {
			// Add service
			this.vehicleInfo.update(info => ({
				...info,
				additionalServices: [...services, serviceName]
			}));
		}
	}

	// Get vehicle type ID from type name for select binding
	getVehicleTypeId(): number | undefined {
		const vehicleType = this.vehicleTypes().find(
			vt => vt.typeName === this.vehicleInfo().type
		);
		return vehicleType?.id;
	}

	getVehicleDescription(): string | null {
		return this.vehicleTypes()
			.find(type => type.typeName === this.vehicleInfo().type)
			?.description ?? null;
	}

	// Update vehicle type when selection changes
	onVehicleTypeChange(typeId: string): void {
		const vehicleType = this.vehicleTypes().find(vt => vt.id === parseInt(typeId));
		if (vehicleType) {
			this.vehicleInfo.update(info => ({
				...info,
				type: vehicleType.typeName
			}));
		}
	}
}
