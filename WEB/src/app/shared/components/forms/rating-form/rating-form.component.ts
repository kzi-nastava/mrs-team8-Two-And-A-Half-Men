import { Component, inject, Input, Output, EventEmitter, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { RatingService } from '@shared/components/forms/rating-form/services/rating.service';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-rating-form',
	standalone: true,
	imports: [CommonModule, ReactiveFormsModule],
	templateUrl: './rating-form.component.html',
	styleUrls: ['./rating-form.component.css'],
})
export class RatingFormComponent {
	private ratingService = inject(RatingService);
	private popupsService = inject(PopupsService);

	@Input() rideId!: number;
	@Input() accessToken: string | null = null;
	@Output() ratingSubmitted = new EventEmitter<void>();
	@Output() closePopup = new EventEmitter<void>();

	ratingForm = new FormGroup({
		driver: new FormControl(0, [Validators.required, Validators.min(1)]),
		vehicle: new FormControl(0, [Validators.required, Validators.min(1)]),
		comment: new FormControl(''),
	});

	stars = [1, 2, 3, 4, 5];
	isLoading = signal<boolean>(false);

	setRating(field: 'driver' | 'vehicle', val: number) {
		this.ratingForm.patchValue({ [field]: val });
	}

	close() {
		this.closePopup.emit();
	}

	submit() {
		console.log(this.ratingForm.valid);
		console.log(this.rideId);
		if (this.ratingForm.valid && this.rideId) {
			this.isLoading.set(true);
			const data = {
				driverRating: this.ratingForm.value.driver!,
				vehicleRating: this.ratingForm.value.vehicle!,
				comment: this.ratingForm.value.comment || '',
			};

			this.ratingService.submitRating(this.rideId, data, this.accessToken).subscribe({
				next: () => {
					this.popupsService.success(
						'Success',
						'Your rating has been submitted successfully!',
					);
					this.isLoading.set(false);
					this.ratingForm.reset();
					this.ratingSubmitted.emit();
				},
				error: (err) => {
					this.popupsService.error(
						'Error',
						'Failed to submit rating. ' + err.error.message,
					);
					this.isLoading.set(false);
				},
			});
		}
	}
}
