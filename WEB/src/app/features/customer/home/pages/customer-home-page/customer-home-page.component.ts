import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouteForm } from '@shared/components/forms/route-form/route-form';
import { MapComponent } from '@shared/components/map/map.component';
import { ButtonDirective } from '@shared/directives/button/button.directive';
import { SharedLocationsService } from '@shared/services/locations/shared-locations.service';
import { BookRideRequest } from '@features/customer/home/models/ride.model';
import { RideService } from '@features/customer/home/services/ride.service';
import { PopupsService } from '@shared/services/popups/popups.service';
import { Router } from '@angular/router';
import { MAP_CONFIGS } from '@shared/components/map/map.config';
import {
	TabItem,
	TabNavigationComponent,
} from '@shared/components/tabs/tab-navigation/tab-navigation.component';
import { TabIconDirective } from '@shared/directives/tab-icon/tab-icon.directive';
import { TabContentComponent } from '@shared/components/tabs/tab-content/tab-content.component';
import { VehiclesService } from '@shared/services/vehicles/vehicles.service';
import { AdditionalService, VehicleType } from '@shared/models/vehicles.model';
import { FormsModule } from '@angular/forms';
import { FavouriteRoutesComponent } from '@features/customer/home/components/favourite-routes/favourite-routes.component';
import { FavouriteRoute } from '@shared/models/favourite-route.model';
import { FavouriteRoutesService } from '@shared/services/routes/favourite-routes.service';

@Component({
	selector: 'app-customer-home-page',
	standalone: true,
	imports: [
		CommonModule,
		RouteForm,
		MapComponent,
		ButtonDirective,
		TabNavigationComponent,
		TabIconDirective,
		TabContentComponent,
		FormsModule,
		FavouriteRoutesComponent,
	],
	templateUrl: './customer-home-page.component.html',
	styleUrl: './customer-home-page.component.css',
})
export class CustomerHomePageComponent implements OnInit {
	mapConfig = MAP_CONFIGS.BOOKING;

	private sharedLocationService = inject(SharedLocationsService);
	private rideService = inject(RideService);
	private popupsService = inject(PopupsService);
	private router = inject(Router);
	private vehicleService = inject(VehiclesService);
	private favouriteRoutesService = inject(FavouriteRoutesService);

	// Active tab
	optionsActiveTab = signal<string>('time');
	routeActiveTab = signal<string>('route');

	// Form options visibility
	showMoreOptions = signal<boolean>(false);

	// Form data
	scheduleOption = signal<'now' | 'specific'>('now');
	scheduledTime = signal<string>('');
	numberOfPassengers = signal<number>(1);
	passengerEmails = signal<string[]>([]);
	newPassengerEmail = signal<string>('');

	// Available vehicle types
	vehicleTypes = signal<VehicleType[]>([]);
	selectedVehicleTypeId = signal<number | null>(null);

	// Available additional services
	selectedServices = signal<Set<number>>(new Set());
	availableServices = signal<AdditionalService[]>([]);

	private selectedRoute: FavouriteRoute | null = null;

	// Tabs configuration
	optionsTabs: TabItem[] = [
		{ id: 'time', label: 'Time', position: 'left' },
		{ id: 'passengers', label: 'Passengers', position: 'left' },
		{ id: 'services', label: 'Additional Services', position: 'left' },
		{ id: 'vehicle', label: 'Vehicle Type', position: 'left' },
	];

	routeTabs: TabItem[] = [
		{ id: 'route', label: 'Route', position: 'left' },
		{ id: 'favorites', label: 'Favourites', position: 'right' },
	];
	protected favourites = signal<FavouriteRoute[]>([]);

	constructor() {
		effect(() => {
			let locations = this.sharedLocationService.locations();
			if (!this.selectedRoute) {
				return;
			}
			if (
				locations.length === this.selectedRoute.points.length &&
				locations.every((loc, index) => {
					const point = this.selectedRoute!.points[index];
					return (
						loc.lat === point.latitude.toString() &&
						loc.lon === point.longitude.toString() &&
						loc.display_name === point.address
					);
				})
			) {
				return; // Locations match the selected route, no update needed
			}
			this.selectedRoute = null; // Clear selected route if locations don't match
		});
	}

	ngOnInit() {
		this.loadVehicleOptions();
		this.loadFavouriteRoutes();
		this.initializeScheduledTime();
	}

	private initializeScheduledTime() {
		const now = new Date();
		const year = now.getFullYear();
		const month = String(now.getMonth() + 1).padStart(2, '0');
		const day = String(now.getDate()).padStart(2, '0');
		const hours = String(now.getHours()).padStart(2, '0');
		const minutes = String(now.getMinutes()).padStart(2, '0');
		this.scheduledTime.set(`${year}-${month}-${day}T${hours}:${minutes}`);
	}

	private loadVehicleOptions() {
		this.vehicleService.getVehicleOptions().subscribe({
			next: (options) => {
				this.vehicleTypes.set(options.vehicleTypes);
				this.availableServices.set(options.additionalServices);
			},
			error: (err) => {
				console.error('Failed to load vehicle options', err);
				this.popupsService.error('Error', 'Failed to load vehicle types');
			},
		});
	}

	onOptionsTabChange(tabId: string): void {
		this.optionsActiveTab.set(tabId);
	}

	onRouteTabChange(tabId: string): void {
		this.routeActiveTab.set(tabId);
	}

	toggleMoreOptions(): void {
		this.showMoreOptions.update((value) => !value);
		if (this.showMoreOptions()) {
			// Scroll to the form section
			setTimeout(() => {
				const formSection = document.getElementById('booking-form-section');
				if (formSection) {
					formSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
				}
			}, 100);
		}
	}

	setScheduleOption(option: 'now' | 'specific'): void {
		this.scheduleOption.set(option);
	}

	addPassenger(): void {
		const email = this.newPassengerEmail().trim();
		if (email && this.isValidEmail(email)) {
			this.passengerEmails.update((emails) => [...emails, email]);
			this.newPassengerEmail.set('');
		} else {
			this.popupsService.error('Invalid Email', 'Please enter a valid email address');
		}
	}

	removePassenger(index: number): void {
		this.passengerEmails.update((emails) => emails.filter((_, i) => i !== index));
	}

	private isValidEmail(email: string): boolean {
		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		return emailRegex.test(email);
	}

	toggleService(serviceId: number): void {
		this.selectedServices.update((services) => {
			const newServices = new Set(services);
			if (newServices.has(serviceId)) {
				newServices.delete(serviceId);
			} else {
				newServices.add(serviceId);
			}
			return newServices;
		});
	}

	isServiceSelected(serviceId: number): boolean {
		return this.selectedServices().has(serviceId);
	}

	selectVehicleType(typeId: number): void {
		this.selectedVehicleTypeId.set(typeId);
	}

	protected createRide() {
		const locations = this.sharedLocationService.locations();

		if (locations.length < 2) {
			this.popupsService.error(
				'Invalid Route',
				'Please select at least a start and end location',
			);
			return;
		}

		const request = this.getBookingRequest();

		const callBookAPI = () => {
			this.rideService.createRide(request).subscribe({
				next: (response) => {
					this.popupsService.success(
						'Ride booked',
						'Your ride has been successfully booked!',
						{
							onConfirm: () => this.router.navigate(['rides', response.id]).then(),
						},
					);
					this.sharedLocationService.clearLocations();
					this.resetForm();
				},
				error: (err) => {
					this.popupsService.error(
						'Booking failed',
						err.error?.message ||
							'An error occurred while booking your ride. Please try again.',
					);
				},
			});
		};

		if (request.scheduledTime) {
			// If the ride being booked for a specific time, we don't need to get time estimate
			callBookAPI();
			return;
		}
		this.rideService.estimateRideTime(request).subscribe({
			next: ({ time }) => {
				this.popupsService.confirm(
					'Confirm Ride',
					`The estimated time for your ride is ${Math.ceil(time)} minutes. Do you want to proceed with booking?`,
					callBookAPI
				);
			},
			error: (err) => {
				this.popupsService.error(
					'Estimation Failed',
					err.error?.error ||
						'An error occurred while estimating your ride time. Please try again.',
				);
			},
		});
	}

	private getBookingRequest(): BookRideRequest {
		const request: BookRideRequest = {};

		if (this.selectedRoute) {
			request.routeId = this.selectedRoute.id;
		} else {
			request.route = this.sharedLocationService.locations().map((location) => ({
				address: location.display_name,
				latitude: parseFloat(location.lat),
				longitude: parseFloat(location.lon),
			}));
		}
		if (this.scheduleOption() === 'specific') {
			request.scheduledTime = this.scheduledTime();
		}
		if (this.passengerEmails().length > 0) {
			request.passengers = this.passengerEmails();
		}
		if (this.selectedVehicleTypeId()) {
			request.vehicleTypeId = this.selectedVehicleTypeId();
		}
		if (this.selectedServices().size > 0) {
			request.additionalServicesIds = Array.from(this.selectedServices());
		}
		return request;
	}

	private resetForm(): void {
		this.scheduleOption.set('now');
		this.passengerEmails.set([]);
		this.newPassengerEmail.set('');
		this.selectedServices.set(new Set());
		this.showMoreOptions.set(false);
		this.optionsActiveTab.set('time');
	}

	getEstimate(): void {
		if (this.sharedLocationService.locations().length < 2) {
			this.popupsService.error(
				'Invalid Route',
				'Please select at least a start and end location to get an estimate',
			);
			return;
		}
		const request = this.getBookingRequest();
		this.rideService.estimateRideTime(request).subscribe({
			next: (response) => {
				this.popupsService.success(
					'Estimated Time',
					`The estimated time for your ride is ${Math.ceil(response.time)} minutes.`,
				);
			},
			error: (err) => {
				this.popupsService.error(
					'Estimation Failed',
					err.error?.error ||
						'An error occurred while estimating your ride time. Please try again.',
				);
			},
		});
	}

	protected selectFavouriteRoute(routeId: number) {
		let route = this.favourites().find((r) => r.id === routeId);
		if (!route) {
			return;
		}
		this.selectedRoute = route;
		let newLocations = route.points.map((r) => ({
			lon: `${r.longitude}`,
			lat: `${r.latitude}`,
			display_name: r.address,
			place_id: 1,
		})) as NominatimResult[];
		this.sharedLocationService.locations.set(newLocations);
		this.routeActiveTab.set('route');
	}

	protected removeFavouriteRoute(routeId: number) {
		this.popupsService.confirm(
			'Remove Favourite',
			'Are you sure you want to remove this favourite route?',
			() => {
				this.favouriteRoutesService.removeFromFavourites(routeId).subscribe({
					next: () => {
						this.popupsService.success(
							'Removed',
							'Favourite route removed successfully',
						);
						this.loadFavouriteRoutes();
					},
					error: (err) => {
						console.error('Failed to remove favourite route', err);
						this.popupsService.error('Error', 'Failed to remove favourite route');
					},
				});
			},
		);
	}

	private loadFavouriteRoutes() {
		this.favouriteRoutesService.getFavouriteRoutes().subscribe({
			next: (dto) => {
				this.favourites.set(dto.routes);
			},
			error: (err) => {
				console.error('Failed to load favourite routes', err);
				this.popupsService.error('Error', 'Failed to load favourite routes');
			},
		});
	}
}
