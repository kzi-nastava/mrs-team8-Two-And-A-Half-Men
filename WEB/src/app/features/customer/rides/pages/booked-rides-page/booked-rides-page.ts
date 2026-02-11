import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerRideService } from '../../services/customer-ride-service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { RidesListComponent } from '@shared/components/rides/ride-list/ride-list.component';
import { RideConfig } from '@features/history/models/ride-config';
import { Ride } from '@shared/models/ride.model';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-booked-rides',
	imports: [CommonModule, ReactiveFormsModule, RidesListComponent],
	templateUrl: './booked-rides-page.html',
	styleUrls: ['./booked-rides-page.css'],
})
export class BookedRides implements OnInit {
	public bookedRides = signal<Ride[]>([]);
	public loading = signal<boolean>(false);
	public BookedRidesService = inject(CustomerRideService);
	private popupsService = inject(PopupsService);
	public router = inject(Router);

	rideConfig = {
		showDriverInfo: true,
		showPassengerInfo: true,
		showPanicButton: true,
		showInconsistencyReports: true,
		showRatings: true,
		showReorderOption: true,
		canViewDetails: true,
		showUserFilters: true,
		showRateButton: false,
	} as RideConfig;

	ngOnInit(): void {
		this.loadRides();
	}
	loadRides(): void {
		this.loading.set(true);
		this.BookedRidesService.loadBookedRides().subscribe({
			next: (rides) => {
				this.bookedRides.set(rides);
				this.loading.set(false);
			},
			error: (err) => {
				this.popupsService.error(
					'Error loading bookedRides',
					err?.error?.message ||
						'There was an error loading booked rides. Please try again later.',
				);
				this.bookedRides.set([]);
			},
		});
	}
	onRideClick(ride: Ride) {
		this.router.navigate(['rides', ride.id]).then();
	}
}
