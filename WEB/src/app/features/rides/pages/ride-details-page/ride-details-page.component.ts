import { Component, computed, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { CommonModule, Location } from '@angular/common';
import { Ride, RideStatus } from '@shared/models/ride.model';
import { map } from 'rxjs/operators';
import { PopupsService } from '@shared/services/popups/popups.service';
import { MapComponent } from '@shared/components/map/map.component';
import { MAP_CONFIGS } from '@shared/components/map/map.config';
import { RatingFormComponent } from '@shared/components/forms/rating-form/rating-form.component';
import { RideService } from '@features/rides/services/ride.service';
import { AuthService } from '@core/services/auth.service';
import { ConfigService } from '@features/rides/services/config.service';
import { FavouriteRoutesService } from '@shared/services/routes/favourite-routes.service';
import { ButtonDirective } from '@shared/directives/button/button.directive';
import { SharedLocationsService } from '@shared/services/locations/shared-locations.service';

@Component({
	selector: 'app-history-ride-details-page',
	standalone: true,
	imports: [CommonModule, MapComponent, RatingFormComponent, ButtonDirective],
	templateUrl: './ride-details-page.component.html',
	styleUrl: './ride-details-page.component.css',
})
export class RideDetailsComponent {
	private route = inject(ActivatedRoute);
	private location = inject(Location);
	private router = inject(Router);
	private actionsConfigService = inject(ConfigService);
	private favouriteRoutesService = inject(FavouriteRoutesService);
	private sharedLocationService = inject(SharedLocationsService);

	rideService = inject(RideService);
	popupService = inject(PopupsService);
	private authService = inject(AuthService);

	mapConfig = MAP_CONFIGS.HISTORY_VIEW;

	actionsConfig = computed(() =>
		this.actionsConfigService.getActions(this.authService.user(), this.ride()),
	);

	ride = signal<Ride | null>(null);
	showRatingPopup = signal(false);
	loadingDetails = signal<boolean>(true);

	private rideId = toSignal(this.route.paramMap.pipe(map((params) => params.get('id'))));

	private shouldLoadDetails = computed(() => {
		const id = this.rideId();
		const currentRide = this.ride();

		return id && (!currentRide || currentRide.id !== +id);
	});

	constructor() {
		effect(() => {
			if (this.shouldLoadDetails()) {
				const id = this.rideId();
				if (id) {
					this.loadRideDetails(+id);
				}
			}
		});

		effect(() => {
			console.log('Current ride:', this.ride());
			console.log('Route ID:', this.rideId());
		});
	}

	private loadRideDetails(rideId: number) {
		console.log('Loading ride details for ID:', rideId);

		if (isNaN(rideId)) {
			this.router
				.navigate(['errors', 'not-found'], {
					queryParams: { msg: `Ride with id: ${this.rideId()} does not exist` },
				})
				.then();
			return;
		}
		this.loadingDetails.set(true);
		this.rideService.getRide(rideId).subscribe({
			next: (data) => {
				console.log('Ride details loaded:', data);
				this.ride.set(data);
				this.loadingDetails.set(false);
			},
			error: (err) => {
				console.error('Error loading ride details:', err);
				this.popupService.error(
					'Error',
					'Failed to load ride details. Please try again later.',
				);
				this.loadingDetails.set(false);
				this.goBack();
			},
		});
	}

	toggleFavorite() {
		const ride = this.ride();
		if (!ride) return;
		if (ride.favourite) {
			this.favouriteRoutesService.removeFromFavourites(ride.routeId).subscribe({
				next: () => {
					this.ride.update((r) => (r ? { ...r, favourite: false } : r));
				},
				error: (err) => {
					this.popupService.error(
						'Error',
						'Failed to remove from favourites. Please try again later.',
					);
				},
			});
		} else {
			this.favouriteRoutesService.addToFavourites(ride.routeId).subscribe({
				next: () => {
					this.ride.update((r) => (r ? { ...r, favourite: true } : r));
				},
				error: (err) => {
					this.popupService.error(
						'Error',
						'Failed to add to favourites. Please try again later.',
					);
				},
			});
		}
	}

	openRatingPopup() {
		this.showRatingPopup.set(true);
	}

	closeRatingPopup() {
		this.showRatingPopup.set(false);
	}

	onRatingSubmitted() {
		this.showRatingPopup.set(false);

		const id = this.rideId();
		if (id) {
			this.loadRideDetails(+id);
		}
	}

	canGoBack(): boolean {
		// history.length is a very general indicator and might not be reliable
		// for complex Angular app history.
		return window.history.length > 2;
	}

	goBack() {
		this.location.back();
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

	protected readonly RideStatus = RideStatus;

	protected rebookRide() {
		if (!this.ride()) return;
		let newLocations = this.ride()!.locations.map((loc) => ({
			lon: `${loc.longitude}`,
			lat: `${loc.latitude}`,
			display_name: loc.address,
			place_id: 1,
		})) as NominatimResult[];
		this.sharedLocationService.locations.set(newLocations);
		this.router.navigate(['/home']).then();
	}

	protected panic() {
		alert('PANIC');
	}

	protected leaveANote() {
		alert('LEAVE A NOTE');
	}

	protected cancelRide() {
		alert('CANCEL');
	}

	protected startRide() {
		alert('STARTING');
	}

	protected endRide() {
		alert('ENDING');
	}
}
