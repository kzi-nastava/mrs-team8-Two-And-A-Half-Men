import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { TabContentComponent } from '@shared/components/tabs/tab-content/tab-content.component';
import { TabItem, TabNavigationComponent } from '@shared/components/tabs/tab-navigation/tab-navigation.component';
import { TabIconDirective } from '@shared/directives/tab-icon/tab-icon.directive';
import { Ride } from '@shared/models/ride.model';
import { PopupsService } from '@shared/services/popups/popups.service';
import { RideConfig } from '@features/history/models/ride-config';
import { AdminHomepageService } from '@features/admin/home/services/admin-homepage.service';
import { RidesListComponent } from '@shared/components/rides/ride-list/ride-list.component';
import { BoxDirective } from '@shared/directives/box/box.directive';
import { PanicNotification } from '@shared/services/panics/panics.service';
import { WebSocketService } from '@core/services/web-socket.service';

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
	public panics = signal<Ride[]>([]);
	public rides = signal<Ride[]>([]);
	public driverNameFilter = signal<string>('');
	public loading = signal<boolean>(false);
	public adminHomepageService = inject(AdminHomepageService);
	private popupsService = inject(PopupsService);
	public router = inject(Router);
	private webSocketService = inject(WebSocketService);
	private unsubscribeFn: (() => void) | null = null;


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
		this.subscribeToRideUpdates();
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
		this.adminHomepageService.loadPanics().subscribe({
			next: (panics) => {
				this.panics.set(panics);
			},
			error: (err) => {
				this.popupsService.error(
					'Error loading panics',
					err?.error?.message ||
						'There was an error loading panics. Please try again later.',
				);
				this.panics.set([]);
			},
		});
	}
	onRideClick(ride: Ride) {
		this.router.navigate(['rides', ride.id]).then();
	}
	async subscribeToRideUpdates() {
		try {
			this.unsubscribeFn = await this.webSocketService.subscribe<any>(
				`/topic/panic`,
				(panic: any) => {
					console.log('Received panic notification:', panic);
						this.loadRides();
					},
			);
		} catch (error) {
			console.error('[PanicService] Error subscribing to panic:', error);
				}
	}
	unsubscribeFromRideUpdates() {
		try {
			if(this.unsubscribeFn) {
				this.unsubscribeFn();
				this.unsubscribeFn = null;
			}
		} catch (error) {
			console.error('[PanicService] Error unsubscribing from panic:', error);
		}
	}

	ngOnDestroy(): void {
		this.unsubscribeFromRideUpdates();
	}
}
