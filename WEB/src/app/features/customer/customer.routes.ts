import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export const CUSTOMER_ROUTES: Routes = [
	{
		path: 'home',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.CUSTOMER])],
		loadComponent: () =>
			import('./home/pages/customer-home-page/customer-home-page.component').then(
				(m) => m.CustomerHomePageComponent,
			),
	},
	{
		path: 'history',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.CUSTOMER])],
		loadComponent: () =>
			import('@features/history/components/history/history.component').then(
				(m) => m.HistoryComponent,
			),
		data: { userRole: LoggedInUserRole.CUSTOMER },
	},
	{
		path: 'history/:id',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.CUSTOMER])],
		loadComponent: () =>
			import('@features/history/components/ride-details/ride-details.component').then(
				(m) => m.RideDetailsComponent,
			),
		data: { userRole: LoggedInUserRole.CUSTOMER },
	},
	{
		path: 'rides/booked',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.CUSTOMER])],
		loadComponent: () =>
			import('./rides/pages/booked-rides-page/booked-rides-page').then((m) => m.BookedRides),
	},

	{
		path: 'rides/:id',
		loadComponent: () =>
			import('./rides/pages/ride-tracking/ride-tracking.component').then(
				(m) => m.RideTrackingComponent,
			),
	},
	{
		path: 'rides/:rideId/rating',
		loadComponent: () =>
			import('./rides/pages/rating-page/rating-page.component').then(
				(m) => m.RatingPageComponent,
			),
	},
];
