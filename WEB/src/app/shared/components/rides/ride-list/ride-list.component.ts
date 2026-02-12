import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ride, RideStatus } from '@shared/models/ride.model';

export interface RidesListConfig {
	canViewDetails?: boolean;
	showDriverInfo?: boolean;
	showPassengerInfo?: boolean;
	showPanicButton?: boolean;
	showReorderOption?: boolean;
}

@Component({
	selector: 'app-rides-list',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './ride-list.component.html',
	styleUrl: './ride-list.component.css',
})
export class RidesListComponent {
	// Inputs
	rides = input.required<Ride[]>();
	config = input.required<RidesListConfig>();
	loading = input<boolean>(false);

	// Outputs
	rideClick = output<Ride>();
	reorderClick = output<Ride>();

	protected readonly RideStatus = RideStatus;

	onRideClick(ride: Ride) {
		if (this.config().canViewDetails) {
			this.rideClick.emit(ride);
		}
	}

	onReorderClick(event: Event, ride: Ride) {
		event.stopPropagation();
		this.reorderClick.emit(ride);
	}

	getStatusClass(status: RideStatus): string {
		const statusClasses = {
			[RideStatus.PENDING]: 'status-pending',
			[RideStatus.ACCEPTED]: 'status-accepted',
			[RideStatus.ACTIVE]: 'status-active',
			[RideStatus.FINISHED]: 'status-finished',
			[RideStatus.INTERRUPTED]: 'status-interrupted',
			[RideStatus.CANCELLED]: 'status-cancelled',
			[RideStatus.PANICKED]: 'status-panicked',
		};
		return statusClasses[status];
	}

	getStatusText(status: RideStatus): string {
		const statusTexts = {
			[RideStatus.PENDING]: 'Pending',
			[RideStatus.ACCEPTED]: 'Accepted',
			[RideStatus.ACTIVE]: 'Active',
			[RideStatus.FINISHED]: 'Finished',
			[RideStatus.INTERRUPTED]: 'Interrupted',
			[RideStatus.CANCELLED]: 'Cancelled',
			[RideStatus.PANICKED]: 'Panicked',
		};
		return statusTexts[status];
	}

	formatDate(date: Date | string): string {
		const d = new Date(date);
		return d.toLocaleString('sr-RS', {
			year: 'numeric',
			month: '2-digit',
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit',
		});
	}
}
