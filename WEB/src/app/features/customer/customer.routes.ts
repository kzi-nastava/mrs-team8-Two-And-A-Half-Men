import { Routes } from '@angular/router';

export const CUSTOMER_ROUTES: Routes = [
	{
		path: 'rides/active',
		loadComponent: () => import('./ride-tracking/ride-tracking.component')
			.then(m => m.RideTrackingComponent),
	},
];
