import { Component, inject, input } from '@angular/core';
import { RideService } from '@features/rides/services/ride.service';
import { ButtonDirective } from '@shared/directives/button/button.directive';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-end-ride-button',
	providers: [RideService],
	imports: [ButtonDirective],
	templateUrl: './end-ride-button.component.html',
	styleUrl: './end-ride-button.component.css',
})
export class EndRideButtonComponent {
	private rideService = inject(RideService);
	private popupsService = inject(PopupsService);

	rideId = input.required<number>();

	endRide() {
		this.rideService.endRide(this.rideId()).subscribe({
			next: (response) => {
				this.popupsService.success(
					'Ride Ended',
					'Ride ended successfully! Cost: ' +
						response.cost +
						', Time: ' +
						response.time +
						' minutes.',
				);
			},
			error: (error) => {
				this.popupsService.error(
					'Error!',
					'There was an error ending the ride. ' + error.message,
				);
			},
		});
	}
}
