// import { Component, inject } from '@angular/core';
// import { RouteForm } from '@shared/components/forms/route-form/route-form';
// import { MapComponent } from '@shared/components/map/map.component';
// import { ButtonDirective } from '@shared/directives/button/button.directive';
// import { SharedLocationsService } from '@shared/services/locations/shared-locations.service';
// import { BookRideRequest } from '@features/customer/home/models/ride.model';
// import { RideService } from '@features/customer/home/services/ride.service';
// import { PopupsService } from '@shared/services/popups/popups.service';
// import { Router } from '@angular/router';
// import { MAP_CONFIGS } from '@shared/components/map/map.config';
//
// @Component({
// 	selector: 'app-customer-home-page',
// 	imports: [RouteForm, MapComponent, ButtonDirective],
// 	templateUrl: './customer-home-page.component.html',
// 	styleUrl: './customer-home-page.component.css',
// })
// export class CustomerHomePageComponent {
// 	mapConfig = MAP_CONFIGS.BOOKING;
// 	private sharedLocationService = inject(SharedLocationsService);
// 	private rideService = inject(RideService);
// 	private popupsService = inject(PopupsService);
// 	private router = inject(Router);
//
// 	protected createRide() {
// 		const request: BookRideRequest = {
// 			route: this.sharedLocationService.locations().map((location) => ({
// 				address: location.display_name,
// 				latitude: parseFloat(location.lat),
// 				longitude: parseFloat(location.lon),
// 			})),
// 		};
// 		this.rideService.createRide(request).subscribe({
// 			next: (response) => {
// 				this.popupsService.success(
// 					'Ride booked',
// 					'Your ride has been successfully booked!',
// 					{
// 						onConfirm: () => this.router.navigate(['rides', response.id]).then()
// 					}
// 				);
// 				this.sharedLocationService.clearLocations();
// 			},
// 			error: err => {
// 				this.popupsService.error('Booking failed', err.error?.message || 'An error occurred while booking your ride. Please try again.');
// 			}
// 		})
// 	}
// }
