import { Routes } from '@angular/router';

export const CUSTOMER_ROUTES: Routes = [
	{
		path: 'rides/booked',
		loadComponent: () =>
			import('./rides/pages/booked-rides-page/booked-rides-page').then((m) => m.BookedRides),
	},

	{
		path: 'rides/active',
		loadComponent: () =>
			import('@features/customer/rides/pages/ride-tracking/ride-tracking.component').then(
				(m) => m.RideTrackingComponent,
			),
	},
	{
		path: 'rides/:rideId/rating',
		loadComponent: () =>
			import('@features/customer/rides/pages/rating-page/rating-page.component').then(m => m.RatingPageComponent),
	},
];
