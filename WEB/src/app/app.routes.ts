import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { ForgotPassword } from './auth/forgot-password/forgot-password';
import { Register } from './auth/register/register';
import {ProfileComponent} from './profile/profile.component';
import { DriversHistoryComponent } from './drivers-history/drivers-history';
import { App } from './app';


export const routes: Routes = [
    { path: 'login', component: Login, data: { animation: 'login' } },
    { path: 'forgot-password', component: ForgotPassword },
    { path: 'register', component: Register, data: { animation: 'register' } },
    { path: 'drivers-history', component: DriversHistoryComponent },
    { path: 'profile', component: ProfileComponent},
    { path: '', component: App }
];
