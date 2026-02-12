import { Component, inject, Input, Output, EventEmitter, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { RatingService } from '@shared/components/forms/rating-form/services/rating.service';
import { PopupsService } from '@shared/services/popups/popups.service';

export interface RatingFormData {
	driverRating: number;
	vehicleRating: number;
	comment: string;
}

@Component({
	selector: 'app-rating-form',
	standalone: true,
	imports: [CommonModule, ReactiveFormsModule],
	templateUrl: './rating-form.component.html',
	styleUrls: ['./rating-form.component.css'],
})
export class RatingFormComponent {

	@Output() ratingSubmitted = new EventEmitter<RatingFormData>();
	@Output() closePopup = new EventEmitter<void>();

	ratingForm = new FormGroup({
		driver: new FormControl(0, [Validators.required, Validators.min(1)]),
		vehicle: new FormControl(0, [Validators.required, Validators.min(1)]),
		comment: new FormControl(''),
	});

	stars = [1, 2, 3, 4, 5];

	setRating(field: 'driver' | 'vehicle', val: number) {
		this.ratingForm.patchValue({ [field]: val });
	}

	close() {
		this.closePopup.emit();
	}

	submit() {
		if (this.ratingForm.valid) {
			const data = {
				driverRating: this.ratingForm.value.driver!,
				vehicleRating: this.ratingForm.value.vehicle!,
				comment: this.ratingForm.value.comment || '',
			};
			this.ratingSubmitted.emit(data);
		}
	}
}
