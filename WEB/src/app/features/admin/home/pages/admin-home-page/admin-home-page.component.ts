import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { TabContentComponent } from '@shared/components/tabs/tab-content/tab-content.component';
import { TabItem, TabNavigationComponent } from '@shared/components/tabs/tab-navigation/tab-navigation.component';
import { TabIconDirective } from '@shared/directives/tab-icon/tab-icon.directive';
import { Ride } from '@shared/models/ride.model';
import { PopupsService } from '@shared/services/popups/popups.service';
import { RideConfig } from '@shared/models/ride-config';
import { AdminHomepageService } from '@features/admin/home/services/admin-homepage.service';
import { RidesListComponent } from '@shared/components/rides/ride-list/ride-list.component';
import { BoxDirective } from '@shared/directives/box/box.directive';

@Component({
	selector: 'app-driver-home-page',
	imports: [
		TabContentComponent,
		TabNavigationComponent,
		TabIconDirective,
		RidesListComponent,
		BoxDirective,
	],
	templateUrl: './admin-home-page.component.html',
	styleUrl: './admin-home-page.component.css',
})
export class AdminHomePageComponent {
	tabs: TabItem[] = [
		{ id: 'active-rides', label: 'Active Rides' },
		{ id: 'panics', label: 'Panics' },
	];
	activeTab = signal<'active-rides' | 'panics'>('active-rides');

	setActiveTab(tabId: string) {
		this.activeTab.set(tabId as 'active-rides' | 'panics');
	}

	public rides = signal<Ride[]>([]);
	public driverNameFilter = signal<string>('');
	public loading = signal<boolean>(false);
	public adminHomepageService = inject(AdminHomepageService);
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
		this.adminHomepageService.loadActiveRides(this.driverNameFilter()).subscribe({
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
