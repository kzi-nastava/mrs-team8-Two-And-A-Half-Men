import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Ride } from '@shared/models/ride.model';
import { PopupsService } from '@shared/services/popups/popups.service';
import { RideConfig } from '@features/history/models/ride-config';
import { RidesListComponent } from '@shared/components/rides/ride-list/ride-list.component';
import { DriverHomepageService } from '@features/driver/home/services/driver-homepage.service';

@Component({
	selector: 'app-driver-home-page',
	imports: [RidesListComponent],
	templateUrl: './driver-home-page.component.html',
	styleUrl: './driver-home-page.component.css',
})
export class DriverHomePageComponent implements OnInit {
	public rides = signal<Ride[]>([]);
	public loading = signal<boolean>(false);
	public driverHomepageService = inject(DriverHomepageService);
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
		this.driverHomepageService.loadActiveRides().subscribe({
			next: (rides) => {
				this.rides.set(rides);
				this.loading.set(false);
			},
			error: (err) => {
				this.popupsService.error(
					'Error loading active rides',
					err?.error?.message ||
						'There was an error loading active rides. Please try again later.',
				);
				this.rides.set([]);
			},
		});
	}
	onRideClick(ride: Ride) {
		this.router.navigate(['rides', ride.id]).then();
	}
}
