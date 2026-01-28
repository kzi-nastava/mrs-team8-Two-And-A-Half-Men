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
			import('./ride-tracking/ride-tracking.component').then((m) => m.RideTrackingComponent),
	},
];
