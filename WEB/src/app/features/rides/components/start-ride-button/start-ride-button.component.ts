import { Component, inject, input, output } from '@angular/core';
import { ButtonDirective } from '@shared/directives/button/button.directive';
import { RideService } from '@features/rides/services/ride.service';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-start-ride-button',
	imports: [ButtonDirective],
	providers: [RideService],
	templateUrl: './start-ride-button.component.html',
	styleUrl: './start-ride-button.component.css',
})
export class StartRideButtonComponent {
	private driverRideService = inject(RideService);
	private popupsService = inject(PopupsService);

	rideId = input.required<number>();

	rideStarted = output<void>();

	startRide(): void {
		this.driverRideService.startRide(this.rideId()).subscribe({
			next: (response) => {
				this.rideStarted.emit();
				this.popupsService.success('Ride Started', response.message);
			},
			error: (error) => {
				console.error('Error starting ride:', error);
				this.popupsService.error('Error', error?.error?.message ?? 'Error starting ride');
			},
		});
	}
}
