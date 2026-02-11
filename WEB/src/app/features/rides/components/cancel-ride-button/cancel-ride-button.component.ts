import { Component, inject, input } from '@angular/core';
import Swal from 'sweetalert2';
import { RideService } from '@features/rides/services/ride.service';
import { ButtonDirective } from '@shared/directives/button/button.directive';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-cancel-ride-button',
	providers: [RideService],
	imports: [ButtonDirective],
	templateUrl: './cancel-ride-button.component.html',
	styleUrl: './cancel-ride-button.component.css',
})
export class CancelRideButtonComponent {
	private rideService = inject(RideService);
	private popupsService = inject(PopupsService);

	rideId = input.required<number>();

	cancel(): void {
		Swal.fire({
			title: 'Cancellation Reason',
			html: this.getCancellationFormHtml(),
			showCancelButton: true,
			confirmButtonText: 'Cancel Ride',
			cancelButtonText: 'Cancel',
			confirmButtonColor: '#dc3545',
			cancelButtonColor: '#6c757d',
			preConfirm: () => {
				const reason = (
					Swal.getPopup()?.querySelector('#cancellationReason') as HTMLTextAreaElement
				).value;
				const cancelledBy = (
					Swal.getPopup()?.querySelector('#cancelledBy') as HTMLSelectElement
				).value;
				if (!reason) {
					return null;
				}
				if (!cancelledBy) {
					return null;
				}
				return `${cancelledBy}: ${reason}`;
			},
		}).then((result) => {
			if (!result.isConfirmed) {
				return;
			}
			const cancelledBy = result.value.split(': ')[0];
			const reason = result.value.split(': ')[1];
			if (!reason) {
				return;
			}
			this.rideService.cancelRide(this.rideId(), reason, cancelledBy).subscribe({
				next: () => {
					this.popupsService.success('Cancelled!', 'The ride has been cancelled.');
				},
				error: (error) => {
					this.popupsService.error(
						'Error!',
						'There was an error cancelling the ride. ' + error.message,
					);
				},
			});
		});
	}

	private getCancellationFormHtml(): string {
		return `
			<div style="text-align: left; width: 90%;">
			  <label style="display: block; margin-bottom: 10px; font-weight: bold;">
				Cancelled by:
			  </label>
			  <select id="cancelledBy" class="swal2-select" style="width: 90%; margin-bottom: 15px;">
				<option value="">Select...</option>
				<option value="CUSTOMER">Customer issues</option>
				<option value="DRIVER">Driver issues</option>
			  </select>

			  <label style="display: block; margin-bottom: 10px; font-weight: bold;">
				Reason:
			  </label>
			  <textarea
				id="cancellationReason"
				class="swal2-textarea"
				placeholder="Enter your reason here..."
				style="width: 90%; height: 300px; resize: none;"
				aria-label="Enter your cancellation reason"></textarea>
			</div>
		`;
	}
}
