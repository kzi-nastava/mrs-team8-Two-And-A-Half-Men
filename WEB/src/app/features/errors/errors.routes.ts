import { Routes } from '@angular/router';

export const ERROR_ROUTES: Routes = [
	{
		path: 'not-found',
		loadComponent: () =>
			import('./pages/not-found-page/not-found-page.component').then(
				(m) => m.NotFoundPageComponent,
			),
	},
	{
		path: 'unauthorized',
		loadComponent: () =>
			import('./pages/unauthenticated-page/unauthenticated-page.component').then(
				(m) => m.UnauthenticatedPageComponent,
			),
	},
];
