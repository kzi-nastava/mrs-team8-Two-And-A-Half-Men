import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export const CUSTOMER_ROUTES: Routes = [
	{
		path: 'rides/booked',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.CUSTOMER])],
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
