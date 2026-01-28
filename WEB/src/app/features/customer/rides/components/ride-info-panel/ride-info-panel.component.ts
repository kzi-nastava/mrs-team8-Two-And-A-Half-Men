import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RideService } from '@features/customer/rides/services/ride-tracking.service';
import { RideTracking } from '@features/customer/rides/models/ride.model';
import { PanicButtonComponent } from '@shared/components/panic-button/panic-button.component';

@Component({
	selector: 'app-ride-info-panel',
	standalone: true,
	imports: [FormsModule, PanicButtonComponent],
	templateUrl: './ride-info-panel.component.html',
	styleUrls: ['./ride-info-panel.component.css'],
})
export class RideInfoPanelComponent {
	private rideService = inject(RideService);

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
			error: (err) => console.error('Error:', err),
		});
	}

	saveNote(): void {
		const currentRide = this.rideData();

		if (!currentRide || currentRide.id === undefined || !this.note.trim()) {
			return;
		}

		this.rideService.saveNote(currentRide.id, this.note, this.accessToken).subscribe({
			next: (response) => {
				console.log('Note saved successfully:', response);
			},
			error: (err) => console.error('Error:', err),
		});
	}
}
