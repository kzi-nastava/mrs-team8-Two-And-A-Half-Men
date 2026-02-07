import { Component, inject, input, output } from '@angular/core';
import { ButtonDirective } from '@shared/directives/button/button.directive';
import { DriverRideService } from '@features/driver/rides/services/driver-ride.service';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-start-ride-button',
	imports: [ButtonDirective],
	providers: [DriverRideService],
	templateUrl: './start-ride-button.component.html',
	styleUrl: './start-ride-button.component.css',
})
export class StartRideButtonComponent {
	private driverRideService = inject(DriverRideService);
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
