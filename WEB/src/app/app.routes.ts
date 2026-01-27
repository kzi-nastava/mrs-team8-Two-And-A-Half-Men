import { Routes } from '@angular/router';
import { Login } from '@features/unregistered/pages/login/login';
import { ForgotPassword } from '@features/unregistered/pages/forgot-password/forgot-password';
import { Register } from '@features/unregistered/pages/register/register';
import { DriversHistoryComponent } from './drivers-history/drivers-history';
import { NavbarComponent } from './navbar/navbar';
import { ActivationComponent } from '@features/unregistered/pages/activation-page/activation-page.component';
import { HomePage } from '@features/unregistered/pages/home-page/home-page';
import { RestartPassword } from '@features/unregistered/pages/restart-password/restart-password';

export const routes: Routes = [
	{
		path: '',
		loadChildren: () =>
			import('@features/unregistered/unregistered.routes').then(
				(m) => m.UNREGISTERED_ROUTES
			),
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
		path: 'error',
		loadChildren: () => import('@features/errors/errors.routes').then((m) => m.ERROR_ROUTES),
	},
	{ path: 'drivers-history', component: DriversHistoryComponent },
	{ path: 'home', component: HomePage },
	{ path: '**', redirectTo: '/error/not-found' },
];
