import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { ForgotPassword } from './auth/forgot-password/forgot-password';
import { Register } from './auth/register/register';
import {ProfileComponent} from '@features/profile/pages/profile-page/profile.component';
import { DriversHistoryComponent } from './drivers-history/drivers-history';
import { App } from './app';
import { NavbarComponent } from './navbar/navbar';
import { ActivationComponent } from './auth/activation/activation';
import { HomePage } from './home-page/home-page';
import { RestartPassword } from './auth/restart-password/restart-password';


export const routes: Routes = [
	{
		path: 'profile',
		loadChildren: () => import('@features/profile/profile.routes')
			.then(m => m.PROFILE_ROUTES)
	},
    { path: 'login', component: Login, data: { animation: 'login' } },
    { path: 'forgot-password', component: ForgotPassword },
    { path: 'register', component: Register, data: { animation: 'register' } },
    { path: 'drivers-history', component: DriversHistoryComponent },
    { path: 'navbar', component: NavbarComponent},
    { path: 'activation', component: ActivationComponent },
    { path: 'home', component: HomePage },
    { path: 'reset-password', component: RestartPassword },
    { path: '**', redirectTo: 'home' }
];
