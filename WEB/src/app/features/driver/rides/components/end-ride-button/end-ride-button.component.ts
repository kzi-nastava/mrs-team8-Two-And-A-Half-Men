import { Component, inject, input } from '@angular/core';
import Swal from 'sweetalert2';
import { DriverRideService } from '@features/driver/rides/services/driver-ride.service';
import { ButtonDirective } from '@shared/directives/button/button.directive';

@Component({
	selector: 'app-end-ride-button',
	providers: [DriverRideService],
	imports: [ButtonDirective],
	templateUrl: './end-ride-button.component.html',
	styleUrl: './end-ride-button.component.css',
})
export class EndRideButtonComponent {
	private rideService = inject(DriverRideService);

	rideId = input.required<number>();

	endRide() {
		this.rideService.endRide(this.rideId()).subscribe({
			next: (response) => {
				Swal.fire({
					title: 'Success',
					text:
						'Ride ended successfully! Cost: ' +
						response.cost +
						', Time: ' +
						response.time +
						' minutes.',
					icon: 'success',
					confirmButtonText: 'Finnish a ride',
				}).then(() => {
					window.location.reload();
					// TODO logic for finishing a ride
				});
			},
			error: (error) => {
				console.error('Error ending ride:', error);
			},
		});
	}
}
