import { Component, computed, effect, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { CommonModule, Location } from '@angular/common';
import { Ride, RideStatus } from '@shared/models/ride.model';
import { map } from 'rxjs/operators';
import { PopupsService } from '@shared/services/popups/popups.service';
import { MapComponent } from '@shared/components/map/map.component';
import { getMapConfigForRideStatus, MapConfig } from '@shared/components/map/map.config';
import {
	RatingFormComponent,
	RatingFormData,
} from '@shared/components/forms/rating-form/rating-form.component';
import { RideService } from '@features/rides/services/ride.service';
import { AuthService } from '@core/services/auth.service';
import { ConfigService } from '@features/rides/services/config.service';
import { FavouriteRoutesService } from '@shared/services/routes/favourite-routes.service';
import { ButtonDirective } from '@shared/directives/button/button.directive';
import { SharedLocationsService } from '@shared/services/locations/shared-locations.service';
import Swal from 'sweetalert2';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';
import { NominatimResult } from '@shared/models/nominatim-results.model';

@Component({
	selector: 'app-history-ride-details-page',
	standalone: true,
	imports: [CommonModule, MapComponent, RatingFormComponent, ButtonDirective],
	templateUrl: './ride-details-page.component.html',
	styleUrl: './ride-details-page.component.css',
})
export class RideDetailsComponent implements OnInit {
	private route = inject(ActivatedRoute);
	private location = inject(Location);
	private router = inject(Router);
	private actionsConfigService = inject(ConfigService);
	private favouriteRoutesService = inject(FavouriteRoutesService);
	private sharedLocationService = inject(SharedLocationsService);

	rideService = inject(RideService);
	popupService = inject(PopupsService);
	private authService = inject(AuthService);

	mapConfig = computed<MapConfig>(() => {
		const currentRide = this.ride();
		if (!currentRide) {
			return getMapConfigForRideStatus(RideStatus.PENDING);
		}
		return getMapConfigForRideStatus(currentRide.status);
	});

	actionsConfig = computed(() =>
		this.actionsConfigService.getActions(this.authService.user(), this.ride()),
	);

	ride = signal<Ride | null>(null);
	showRatingPopup = signal(false);
	loadingDetails = signal<boolean>(true);
	private shouldOpenRatingPopup = false;

	private rideId = toSignal(this.route.paramMap.pipe(map((params) => params.get('id'))));

	private shouldLoadDetails = computed(() => {
		const id = this.rideId();
		const currentRide = this.ride();

		return id && (!currentRide || currentRide.id !== +id);
	});
	private accessToken: string | null = null;

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
			console.log('Map config:', this.mapConfig());
		});
	}

	ngOnInit() {
		console.log('ON INIT');
		console.log(this.route.snapshot.queryParams);
		this.accessToken = this.route.snapshot.queryParams['accessToken'] || null;
		this.shouldOpenRatingPopup = this.route.snapshot.queryParams['view'] === 'rate';
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
				if (this.shouldOpenRatingPopup) {
					this.showRatingPopup.set(true);
				}
				this.shouldOpenRatingPopup = false;
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
				error: () => {
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
				error: () => {
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

	onRatingSubmitted(data: RatingFormData) {
		if (!this.ride()) {
			return;
		}
		this.showRatingPopup.set(false);
		this.rideService.submitRating(this.ride()!.id, data, this.accessToken).subscribe({
			next: () => {
				this.popupService.success('Thank you!', 'Your rating has been submitted.');
				this.loadRideDetails(this.ride()!.id);
			},
			error: (err) => {
				this.popupService.error(
					'Error',
					'Failed to submit rating. ' +
						(err?.error?.message || 'Please try again later.'),
				);
			},
		});
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
		this.rideService.triggerPanic(this.accessToken).subscribe({
			next: () => {
				this.popupService.success(
					'PANICKED!',
					'Panic alert has been triggered. Help is on the way!',
				);
				this.loadRideDetails(this.ride()!.id);
			},
			error: (error) => {
				this.popupService.error(
					'Error!',
					'There was a triggering your panic. Pray to God this works soon. ' +
						error?.error?.message,
				);
			},
		});
	}

	protected leaveANote() {
		Swal.fire({
			title: 'Leave Inconsistency Note',
			html: this.getLeaveNoteFormHtml(),
			showCancelButton: true,
			confirmButtonText: 'Save',
			cancelButtonText: 'Cancel',
			confirmButtonColor: '#dc3545',
			cancelButtonColor: '#6c757d',
			preConfirm: () => {
				const note = (
					Swal.getPopup()?.querySelector('#inconsistency-note') as HTMLTextAreaElement
				).value;
				if (!note) {
					return null;
				}
				return note;
			},
		}).then((result) => {
			if (!result.isConfirmed) {
				return;
			}
			const note = result.value;
			if (!note) {
				return;
			}
			this.rideService.leaveNote(this.ride()!.id, note, this.accessToken).subscribe({
				next: () => {
					this.popupService.success('Saved', 'Your note has been saved.');
					this.loadRideDetails(this.ride()!.id);
				},
				error: (error) => {
					this.popupService.error(
						'Error!',
						'There was an error saving your note. ' + error?.error?.message,
					);
				},
			});
		});
	}

	protected cancelRide() {
		if (this.authService.user()?.role === LoggedInUserRole.DRIVER) {
			this.cancelDriverRide();
		} else {
			this.cancelRideOwnerRide();
		}
	}

	protected cancelRideOwnerRide() {
		this.popupService.confirm(
			'Cancel Ride',
			'Are you sure you want to cancel this ride?',
			() => {
				this.rideService.cancelRide(this.ride()!.id).subscribe({
					next: () => {
						this.popupService.success('Cancelled!', 'The ride has been cancelled.');
						this.loadRideDetails(this.ride()!.id);
					},
					error: (error) => {
						this.popupService.error(
							'Error!',
							'There was an error cancelling the ride. ' +
								(error?.error?.message ?? ''),
						);
					},
				});
			},
		);
	}

	protected cancelDriverRide() {
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
			this.rideService.cancelRide(this.ride()!.id, reason, cancelledBy).subscribe({
				next: () => {
					this.popupService.success('Cancelled!', 'The ride has been cancelled.');
					this.loadRideDetails(this.ride()!.id);
				},
				error: (error) => {
					this.popupService.error(
						'Error!',
						'There was an error cancelling the ride. ' + (error?.error?.message ?? ''),
					);
				},
			});
		});
	}

	protected startRide() {
		this.popupService.confirm('Start Ride', 'Are you sure you want to start this ride?', () => {
			this.rideService.startRide(this.ride()!.id).subscribe({
				next: (response) => {
					this.popupService.success('Ride Started', response.message);
					this.loadRideDetails(this.ride()!.id);
				},
				error: (error) => {
					console.error('Error starting ride:', error);
					this.popupService.error(
						'Error',
						error?.error?.message ?? 'Error starting ride',
					);
				},
			});
		});
	}

	protected endRide() {
		this.popupService.confirm('End Ride', 'Are you sure you want to end this ride?', () => {
			this.rideService.endRide(this.ride()!.id).subscribe({
				next: (response) => {
					Swal.fire({
						title: 'Ride Ending',
						html: this.getFinishRideForm(
							response.cost.toString(),
							response.time.toString(),
						),
						showCancelButton: true,
						confirmButtonText: 'Finish Ride',
						confirmButtonColor: '#dc3545',
						preConfirm: () => {
							const isInterrupted = (
								Swal.getPopup()?.querySelector('#interrupted') as HTMLInputElement
							).checked;
							return `${isInterrupted}`;
						},
					}).then((result) => {
						if (!result.isConfirmed) {
							return;
						}
						const isInterrupted = result.value === 'true';
						this.rideService
							.finishRide(this.ride()!.id, isInterrupted, true)
							.subscribe({
								next: () => {
									this.popupService.success(
										'Ride Finished',
										'The ride has been successfully finished.',
									);
									this.loadRideDetails(this.ride()!.id);
								},
								error: (error) => {
									this.popupService.error(
										'Error!',
										'There was an error finishing the ride. ' +
											(error?.error?.message ?? ''),
									);
								},
							});
					});
				},
				error: (error) => {
					console.error('Error ending ride:', error);
					this.popupService.error('Error', error?.error?.message ?? 'Error ending ride');
				},
			});
		});
	}

	private getFinishRideForm(cost: string, time: string): string {
		return `
			<div style="text-align: left; width: 90%;">
			  <label style="display: block; margin-bottom: 10px; font-weight: bold;">
				Ride Cost:
			  </label>
			  <input
				id="rideCost"
				class="swal2-input"
				type="text"
				readonly
				value="${cost}"
				style="width: 90%; margin-bottom: 15px;"
			  />

			  <label style="display: block; margin-bottom: 10px; font-weight: bold;">
				Ride Duration:
			  </label>
			  <input
				id="rideDuration"
				class="swal2-input"
				type="text"
				readonly
				value="${time}"
				style="width: 90%; margin-bottom: 15px;"
			  />

			  <label style="display: flex; align-items: center; gap: 8px; font-weight: bold;">
				<input type="checkbox" id="interrupted" />
				Was ride interrupted
			  </label>
			</div>
		`;
	}

	private getLeaveNoteFormHtml(): string {
		return `
			<div style="text-align: left; width: 90%;">
			  <label style="display: block; margin-bottom: 10px; font-weight: bold;">
				Note:
			  </label>
			  <textarea
				id="inconsistency-note"
				class="swal2-textarea"
				placeholder="Enter your note here..."
				style="width: 90%; height: 300px; resize: none;"
				aria-label="Enter your note"></textarea>
			</div>
		`;
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
