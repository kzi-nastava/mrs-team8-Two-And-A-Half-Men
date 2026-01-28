import { Component, inject } from '@angular/core';
import { RouteForm } from '@shared/components/forms/route-form/route-form';
import { MapComponent } from '@shared/components/map/map.component';
import { ButtonDirective } from '@shared/directives/button/button.directive';
import { SharedLocationsService } from '@shared/services/locations/shared-locations.service';
import { BookRideRequest } from '@features/customer/home/models/ride.model';
import { RideService } from '@features/customer/home/services/ride.service';

@Component({
	selector: 'app-customer-home-page',
	imports: [RouteForm, MapComponent, ButtonDirective],
	templateUrl: './customer-home-page.component.html',
	styleUrl: './customer-home-page.component.css',
})
export class CustomerHomePageComponent {

	private sharedLocationService = inject(SharedLocationsService);
	private rideService = inject(RideService);

	protected createRide() {
		const request: BookRideRequest = {
			route: this.sharedLocationService.locations().map((location) => ({
				address: location.display_name,
				latitude: parseFloat(location.lat),
				longitude: parseFloat(location.lon),
			})),
		};
		this.rideService.createRide(request).subscribe({
			next: () => {
				alert("CREATED")
			},
			error: err => {
				alert(err);
			}
		})
	}
}
