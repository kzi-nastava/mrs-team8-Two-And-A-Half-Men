import { Component, inject, input, output } from '@angular/core';
import { ButtonDirective } from '@shared/directives/button/button.directive';
import { DriverRideService } from '@features/driver/rides/services/driver-ride.service';
import Swal from 'sweetalert2';

@Component({
	selector: 'app-start-ride-button',
	imports: [ButtonDirective],
	providers: [DriverRideService],
	templateUrl: './start-ride-button.component.html',
	styleUrl: './start-ride-button.component.css',
})
export class StartRideButtonComponent {
	private driverRideService = inject(DriverRideService);

	rideId = input.required<number>();

	rideStarted = output<void>()

	startRide(): void {
		this.driverRideService.startRide(this.rideId()).subscribe({
			next: (response) => {
				this.rideStarted.emit();
				Swal.fire({
					title: 'Success',
					text: response.message,
					icon: 'success',
				}).then();
			},
			error: (error) => {
				console.error('Error starting ride:', error);
				Swal.fire({
					title: 'Error',
					text: error?.error?.message ?? 'Error starting ride',
					icon: 'error',
				}).then();
			},
		});
	}
}
