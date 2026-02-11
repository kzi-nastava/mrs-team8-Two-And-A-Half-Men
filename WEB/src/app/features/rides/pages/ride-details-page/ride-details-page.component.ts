import { Component, computed, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { CommonModule, Location } from '@angular/common';
import { Ride, RideStatus } from '@shared/models/ride.model';
import { map } from 'rxjs/operators';
import { PopupsService } from '@shared/services/popups/popups.service';
import { RIDE_HISTORY_CONFIGS } from '@shared/models/ride-config';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';
import { MapComponent } from '@shared/components/map/map.component';
import { MAP_CONFIGS } from '@shared/components/map/map.config';
import { RatingFormComponent } from '@shared/components/forms/rating-form/rating-form.component';
import { RideService } from '@features/rides/services/ride.service';
import { AuthService } from '@core/services/auth.service';

@Component({
	selector: 'app-history-ride-details-page',
	standalone: true,
	imports: [CommonModule, MapComponent, RatingFormComponent],
	templateUrl: './ride-details-page.component.html',
	styleUrl: './ride-details-page.component.css',
})
export class RideDetailsComponent {
	private route = inject(ActivatedRoute);
	private location = inject(Location);
	private router = inject(Router);

	rideService = inject(RideService);
	popupService = inject(PopupsService);
	private authService = inject(AuthService);


	userRole = computed(() => this.authService!.user()!.role);

	config = computed(() => RIDE_HISTORY_CONFIGS[this.userRole()]);
	mapConfig = MAP_CONFIGS.HISTORY_VIEW;

	ride = signal<Ride | null>(null);
	togglingFavorite = signal(false);
	showRatingPopup = signal(false);
	loadingDetails = signal<boolean>(true);

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

		effect(() => {
			console.log('Current ride:', this.ride());
			console.log('Route ID:', this.rideId());
		});
	}

	private loadRideDetails(rideId: number) {
		console.log('Loading ride details for ID:', rideId);

		if (isNaN(rideId)) {
			this.router
				.navigate(['errors', 'not-fountd'], {
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

		this.togglingFavorite.set(true);

		// this.rideService.toggleFavorite(ride.id);
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
}
