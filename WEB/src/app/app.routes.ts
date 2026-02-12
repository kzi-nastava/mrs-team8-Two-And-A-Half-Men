import { Routes } from '@angular/router';
import { HomePage } from '@features/unregistered/pages/home-page/home-page';

export const routes: Routes = [
	{
		path: '',
		loadChildren: () =>
			import('@features/unregistered/unregistered.routes').then((m) => m.UNREGISTERED_ROUTES),
	},
	{
		path: '',
		loadChildren: () =>
			import('@features/customer/customer.routes').then((m) => m.CUSTOMER_ROUTES),
	},
	{
		path: 'history',
		loadChildren: () =>
			import('@features/history/history.routes').then((m) => m.HISTORY_ROUTES),
	},
	{
		path: 'profile',
		loadChildren: () =>
			import('@features/profile/profile.routes').then((m) => m.PROFILE_ROUTES),
	},
	{
		path: 'users',
		loadChildren: () => import('@features/users/users.routes').then((m) => m.USERS_ROUTES),
	},
	{
		path: 'settings',
		loadChildren: () =>
			import('@features/settings/settings.routes').then((m) => m.SETTINGS_ROUTES),
	},
	{
		path: 'chat',
		loadChildren: () => import('@features/chat/chat.routes').then((m) => m.CHAT_ROUTES),
	},
	{
		path: 'reports',
		loadChildren: () =>
			import('@features/reports/reports.routes').then((m) => m.REPORTS_ROUTES),
	},
	{
		path: 'driver',
		loadChildren: () => import('@features/driver/driver.routes').then((m) => m.DRIVER_ROUTES),
	},
	{
		path: 'error',
		loadChildren: () => import('@features/errors/errors.routes').then((m) => m.ERROR_ROUTES),
	},
	{
		path: 'admin',
		loadChildren: () => import('@features/admin/admin.routes').then((m) => m.ADMIN_ROUTES),
	},
	{
		path: 'rides',
		loadChildren: () => import('@features/rides/rides.routes').then((m) => m.RIDES_ROUTES),
	},
	{ path: '**', redirectTo: '/error/not-found' },
];
