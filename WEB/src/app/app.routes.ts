import { RouterModule, Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { ForgotPassword } from './auth/forgot-password/forgot-password';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Register } from './auth/register/register';
import { App } from './app';
export const routes: Routes = [
    { path: 'login', component: Login, data: { animation: 'login' } },
    { path: 'forgot-password', component: ForgotPassword },
    { path: 'register', component: Register, data: { animation: 'register' } },
    { path: '', component: App }
];