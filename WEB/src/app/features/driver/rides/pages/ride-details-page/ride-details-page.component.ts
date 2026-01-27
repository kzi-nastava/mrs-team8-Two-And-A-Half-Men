import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
	selector: 'app-rides-details-page',
	imports: [],
	templateUrl: './ride-details-page.component.html',
	styleUrl: './ride-details-page.component.css',
})
export class RideDetailsPageComponent implements OnInit {
	rideId: number = 0;
	private activeRoute: ActivatedRoute = inject(ActivatedRoute);
	private router = inject(Router);
	ngOnInit() {
		const id = this.activeRoute.snapshot.paramMap.get('rideId');
		console.log(id);
		if (id === null || isNaN(parseInt(id))) {
			this.router
				.navigate(['error', 'not-found'], { queryParams: { msg: `Ride does not exist` } })
				.then();
		}
	}
}
