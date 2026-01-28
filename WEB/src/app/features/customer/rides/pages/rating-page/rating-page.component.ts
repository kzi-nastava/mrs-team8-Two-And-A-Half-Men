import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RatingFormComponent } from '@shared/components/forms/rating-form/rating-form.component';

@Component({
	selector: 'app-rating-page',
	imports: [RatingFormComponent],
	templateUrl: './rating-page.component.html',
	styleUrl: './rating-page.component.css',
})
export class RatingPageComponent {
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
}
