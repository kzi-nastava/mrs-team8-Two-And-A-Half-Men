// history-ride-details-page.component.ts
import { Component, inject, computed, effect } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { HistoryService } from '@features/history/services/history.service';
import { Ride, RideStatus } from '@features/history/models/ride.model';
import { map } from 'rxjs/operators';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-history-ride-details-page',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './ride-details.component.html',
	styleUrl: './ride-details.component.css',
})
export class RideDetailsComponent {
	private route = inject(ActivatedRoute);
	private router = inject(Router);

	historyService = inject(HistoryService);
	popupService = inject(PopupsService)

	ride = this.historyService.selectedRide;

	private rideId = toSignal(this.route.paramMap.pipe(map((params) => params.get('id'))));

	private shouldLoadDetails = computed(() => {
		const id = this.rideId();
		const currentRide = this.ride();

		return id && (!currentRide || currentRide.id !== +id);
	});

	protected readonly RideStatus = RideStatus;

	constructor() {
		effect(() => {
			if (this.shouldLoadDetails()) {
				const id = this.rideId();
				if (id) {
					this.loadRideDetails(+id);
				}
			}
		});

		// Debug effect
		effect(() => {
			console.log('Current ride:', this.ride());
			console.log('Route ID:', this.rideId());
		});
	}

	private loadRideDetails(rideId: number) {
		console.log('Loading ride details for ID:', rideId);

		this.historyService.selectedRide.set({ id: rideId } as Ride);

		this.historyService.getRideDetails().subscribe({
			next: (data) => {
				this.historyService.selectedRide.set(data);
				console.log('Ride details loaded:', data);
			},
			error: (err) => {
				console.error('Error loading ride details:', err);
				this.popupService.error(
					'Error',
					'Failed to load ride details. Please try again later.',
				);
				this.goBack();
			},
		});
	}

	goBack() {
		this.router.navigate(['..'], { relativeTo: this.route });
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
}
