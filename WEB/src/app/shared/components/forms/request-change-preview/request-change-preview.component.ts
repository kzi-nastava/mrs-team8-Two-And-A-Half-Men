import { Component, inject, input, model } from '@angular/core';
import {PendingChangeRequest} from '@shared/models/profile-change-request.model';
import {PersonalInfo} from '@shared/models/personal-info.model';
import {VehicleInfo} from '@shared/models/vehicles.model';
import {ReactiveFormsModule} from '@angular/forms';
import { ProfileService } from '@features/profile/services/profile.service';
import { PopupsService } from '@shared/services/popups/popups.service';

interface ChangeItem {
	label: string;
	oldValue: string | null;
	newValue: string | null;
}

@Component({
	selector: 'app-request-change-preview',
	imports: [ReactiveFormsModule],
	templateUrl: './request-change-preview.component.html',
	styleUrl: './request-change-preview.component.css',
})
export class RequestChangePreviewComponent {

	request = model.required<PendingChangeRequest | null>();
	personalInfo = input.required<PersonalInfo>();
	vehicleInfo = input.required<VehicleInfo | null>();
	title = input.required<string>();


	get pendingChanges(): ChangeItem[] {
		const changes: ChangeItem[] = [];

		const addChange = (label: string, oldVal: any, newVal: any) => {
			if (newVal !== null && newVal !== undefined && newVal !== oldVal) {
				// Special handling for arrays
				if (Array.isArray(newVal)) {
					// Compare arrays
					const oldArray = Array.isArray(oldVal) ? oldVal : [];
					const newArray = newVal;
					const arraysEqual =
						oldArray.length === newArray.length &&
						oldArray.every((val) => newArray.includes(val));
					if (arraysEqual) {
						return;
					}
				}
				changes.push({
					label,
					oldValue: this.formatValue(oldVal),
					newValue: this.formatValue(newVal),
				});
			}
		};

		// Personal info
		addChange('First name', this.personalInfo().firstName, this.request()?.firstName);
		addChange('Last name', this.personalInfo().lastName, this.request()?.lastName);
		addChange('Phone number', this.personalInfo().phoneNumber, this.request()?.phoneNumber);
		addChange('Address', this.personalInfo().address, this.request()?.address);
		addChange('Email', this.personalInfo().email, this.request()?.email);

		// Vehicle info
		if (this.vehicleInfo() === null) {
			return changes;
		}
		addChange('Vehicle type', this.vehicleInfo()!.type, this.request()?.vehicleType);
		addChange('Vehicle model', this.vehicleInfo()!.model, this.request()?.model);
		addChange('License plate', this.vehicleInfo()!.licensePlate, this.request()?.licensePlate);
		addChange(
			'Number of seats',
			this.vehicleInfo()!.numberOfSeats,
			this.request()?.numberOfSeats,
		);
		addChange(
			'Additional driver-location',
			this.vehicleInfo()!.additionalServices,
			this.request()?.additionalServices,
		);

		return changes;
	}

	private formatValue(value: unknown): string {
		if (Array.isArray(value)) {
			return value.join(', ');
		}
		if (value === null || value === undefined) {
			return '—';
		}
		return String(value);
	}

	get imgChange(): ChangeItem | null {
		if (
			this.request()?.imgSrc === undefined ||
			this.request()?.imgSrc === this.personalInfo().imgSrc
		) {
			return null;
		}
		return {
			label: 'Profile Image',
			oldValue: this.personalInfo().imgSrc ?? null,
			newValue: this.request()?.imgSrc ?? null,
		};
	}
}
