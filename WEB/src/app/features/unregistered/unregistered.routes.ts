import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth-guard';
import { loggedInGuard } from '@features/unregistered/guards/loggedin-guard';

export const UNREGISTERED_ROUTES: Routes = [
	{
		path: '',
		canActivate: [loggedInGuard],
		loadComponent: () => import('./pages/home-page/home-page')
			.then((m) => m.HomePage),
	},
	{
		path: 'login',
		loadComponent: () => import('./pages/login/login')
			.then((m) => m.Login),
		data: { animation: 'login' },
	},
	{
		path: 'register',
		loadComponent: () => import('./pages/register/register')
			.then((m) => m.Register),
		data: { animation: 'register' },
	},
	{
		path: 'forgot-password',
		loadComponent: () => import('./pages/forgot-password/forgot-password')
			.then((m) => m.ForgotPassword),
	},
	{
		path: 'activation',
		loadComponent: () => import('@features/unregistered/pages/activation-page/activation-page.component')
			.then((m) => m.ActivationComponent),
	},
	{
		path: 'reset-password',
		loadComponent: () => import('./pages/restart-password/restart-password')
			.then((m) => m.RestartPassword),
	}
];
