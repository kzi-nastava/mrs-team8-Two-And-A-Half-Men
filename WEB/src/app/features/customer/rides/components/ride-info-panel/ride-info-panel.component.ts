import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RideService } from '@features/customer/rides/services/ride-tracking.service';
import { RideTracking } from '@features/customer/rides/models/ride.model';
import { PanicButtonComponent } from '@shared/components/panic-button/panic-button.component';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-ride-info-panel',
	standalone: true,
	imports: [FormsModule, PanicButtonComponent],
	templateUrl: './ride-info-panel.component.html',
	styleUrls: ['./ride-info-panel.component.css'],
})
export class RideInfoPanelComponent {
	private rideService = inject(RideService);
	private popupsService = inject(PopupsService);

	rideData = signal<RideTracking | null>(null);

	////////////
	remainingDistance: number = 7.2;
	remainingTime: number = 50;

	note: string = '';
	accessToken: string | null = null;
	//////////////

	ngOnInit(): void {
		this.loadActiveRide();
	}

	loadActiveRide(): void {
		this.rideService.getActiveRide(this.accessToken).subscribe({
			next: (data) => this.rideData.set(data),
			error: (err) =>
				this.popupsService.error(
					'Error',
					'Failed to load ride data. Please try again later. ' + err.message,
				),
		});
	}

	saveNote(): void {
		const currentRide = this.rideData();

		if (!currentRide || currentRide.id === undefined || !this.note.trim()) {
			return;
		}

		this.rideService.saveNote(currentRide.id, this.note, this.accessToken).subscribe({
			next: (response) => {
				this.popupsService.success('Note saved', 'Your note has been successfully saved!');
			},
			error: (err) => {
				this.popupsService.error(
					'Error',
					'Failed to save note. Please try again later. ' + err.message,
				);
			},
		});
	}
}
