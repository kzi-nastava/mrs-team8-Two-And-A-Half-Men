import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CancelRideButtonComponent } from '@features/driver/rides/components/cancel-ride-button/cancel-ride-button.component';
import { EndRideButtonComponent } from '@features/driver/rides/components/end-ride-button/end-ride-button.component';
import { PanicButtonComponent } from '@shared/components/panic-button/panic-button.component';
import {
	StartRideButtonComponent
} from '@features/driver/rides/components/start-ride-button/start-ride-button.component';

@Component({
	selector: 'app-rides-details-page',
	imports: [
		CancelRideButtonComponent,
		EndRideButtonComponent,
		PanicButtonComponent,
		StartRideButtonComponent,
	],
	templateUrl: './ride-details-page.component.html',
	styleUrl: './ride-details-page.component.css',
})
export class RideDetailsPageComponent implements OnInit {
	private activeRoute: ActivatedRoute = inject(ActivatedRoute);
	private router = inject(Router);

	rideId = signal<number>(0);

	ngOnInit() {
		const id = this.activeRoute.snapshot.paramMap.get('rideId');
		if (id === null || isNaN(parseInt(id))) {
			this.router
				.navigate(['error', 'not-found'], { queryParams: { msg: `Ride does not exist` } })
				.then();
		}
		this.rideId.set(parseInt(id!));
	}

	protected refresh() {
		alert("Refresh ride details logic goes here")
	}
}
