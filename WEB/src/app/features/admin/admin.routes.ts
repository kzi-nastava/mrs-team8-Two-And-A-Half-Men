import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { roleGuard } from '@core/guards/role-guard';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export const ADMIN_ROUTES: Routes = [
	{
		path: '',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('./home/pages/admin-home-page/admin-home-page.component').then(
				(m) => m.AdminHomePageComponent,
			),
	},
	{
		path: 'settings',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('./settings/pages/settings-page/settings-page.component').then(
				(m) => m.SettingsPageComponent,
			),
	},
	{
		path: 'users',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('./users/pages/all-users-page/all-users-page.component').then(
				(m) => m.AllUsersPageComponent,
			),
	},
	{
		path: 'users/:userId',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('./users/pages/user-details-page/user-details-page.component').then(
				(m) => m.UserDetailsPageComponent,
			),
	},
	{
		path: 'users/drivers/new',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('./users/pages/new-driver-page/new-driver-page.component').then(
				(m) => m.NewDriverPageComponent,
			),
	},
	{
		path: 'history',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('@features/history/components/history/history.component').then(
				(m) => m.HistoryComponent,
			),
		data: { userRole: LoggedInUserRole.ADMIN },
	},
	{
		path: 'history/:id',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('@features/history/components/ride-details/ride-details.component').then(
				(m) => m.RideDetailsComponent,
			),
		data: { userRole: LoggedInUserRole.CUSTOMER },
	},
	{
		path: 'active-rides',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('./rides/pages/active-rides-page/active-rides-page.component').then(
				(m) => m.ActiveRidesPageComponent,
			),
	},
	{
		path: 'active-rides/:id',
		canActivate: [authGuard, roleGuard([LoggedInUserRole.ADMIN])],
		loadComponent: () =>
			import('./rides/pages/active-ride-details/active-ride-details.component').then(
				(m) => m.ActiveRideDetailsComponent,
			),
	},
];
