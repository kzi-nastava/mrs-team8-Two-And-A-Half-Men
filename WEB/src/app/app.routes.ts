import { RouterModule, Routes } from '@angular/router';
import { Login } from './login/login';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { App } from './app';
export const routes: Routes = [
    { path: 'login', component: Login },
    { path: '',component: App }
];
