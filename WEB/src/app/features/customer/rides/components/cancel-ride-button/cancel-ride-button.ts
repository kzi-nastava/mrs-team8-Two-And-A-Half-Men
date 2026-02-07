import { Component, inject, input, output } from '@angular/core';
import { CustomerRideService } from '../../services/customer-ride-service';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-cancel-ride-button',
	imports: [],
	templateUrl: './cancel-ride-button.html',
	styleUrl: './cancel-ride-button.css',
})
export class CancelRideButton {
	rideId = input.required<number>();
	cancelled = output<void>();
	customerRideService = inject(CustomerRideService);
	private popupsService = inject(PopupsService);
	cancel() {
		this.popupsService.confirm(
			'Cancelation',
			'Are you sure you want to cancel the ride?',
			() => {
				const message = this.customerRideService.cancelRide(this.rideId());
				message.subscribe({
					next: () => {
						this.popupsService.success('Cancelled!', 'The ride has been cancelled.');
						this.cancelled.emit();
					},
					error: (error) => {
						this.popupsService.error(
							'Error!',
							'There was an error cancelling the ride. ' + error.message,
						);
					},
				});
			},
		);
	}
}
