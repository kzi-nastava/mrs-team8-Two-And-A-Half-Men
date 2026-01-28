import { Routes } from '@angular/router';
import { HomePage } from '@features/unregistered/pages/home-page/home-page';

export const routes: Routes = [
	{
		path: '',
		loadChildren: () =>
			import('@features/unregistered/unregistered.routes').then((m) => m.UNREGISTERED_ROUTES),
	},
	{
		path: 'profile',
		loadChildren: () =>
			import('@features/profile/profile.routes').then((m) => m.PROFILE_ROUTES),
	},
	{
		path: 'driver',
		loadChildren: () => import('@features/driver/driver.routes').then((m) => m.DRIVER_ROUTES),
	},
	{
		path: 'customer',
		loadChildren: () => import('@features/customer/customer.routes').then((m) => m.CUSTOMER_ROUTES),
	},
	{
		path: 'error',
		loadChildren: () => import('@features/errors/errors.routes').then((m) => m.ERROR_ROUTES),
	},
	{
		path: 'customer',
		loadChildren: () =>
			import('@features/customer/customer.routes').then((m) => m.customerRoutes),
	},
	{ path: 'home', component: HomePage },
	{ path: '**', redirectTo: '/error/not-found' },
];
