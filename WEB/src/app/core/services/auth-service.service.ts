// core/services/auth.service.ts
import { Injectable, signal, computed, inject } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import { environment } from '@environments/environment';
import { tap } from 'rxjs/operators';
import {LoggedInUser} from '@core/models/loggedInUser.model';
import {Router} from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {
	private http = inject(HttpClient);

	// --- Reactive state ---
	private _user = signal<LoggedInUser | null>(null);
	readonly user = this._user.asReadonly();

	readonly fullName = computed(() => this._user()?.firstName + ' ' + this._user()?.lastName);

	private _jwt: string | null = null; // in-memory JWT (cached)

	// --- Storage key ---
	private readonly JWT_KEY = 'jwt_token';

	constructor() {
		// On service init, load JWT from storage and fetch user
		const token = sessionStorage.getItem(this.JWT_KEY) || localStorage.getItem(this.JWT_KEY);
		if (token) {
			this._jwt = token;
			// this.fetchUserInfo().subscribe({ error: () => this.logout() });
		}
	}

	// --- Save login result (called by login page) ---
	saveLogin(token: string, user: LoggedInUser, rememberMe = false) {
		this._jwt = token;
		this._user.set(user);

		if (rememberMe) {
			localStorage.setItem(this.JWT_KEY, token);
			sessionStorage.removeItem(this.JWT_KEY);
		} else {
			sessionStorage.setItem(this.JWT_KEY, token);
			localStorage.removeItem(this.JWT_KEY);
		}
	}
	// --- Logout ---
	logout() {
		this._jwt = null;
		this._user.set(null);
		sessionStorage.removeItem(this.JWT_KEY);
		localStorage.removeItem(this.JWT_KEY);
	}

	// --- Check if user is logged in ---
	isLoggedIn(): boolean {
		return !!this._jwt;
	}

	// --- Fetch user info from API ---
	fetchUserInfo() {
		return this.http.get<LoggedInUser>(`/api/${environment.apiVersion}/me`)
			.pipe(
				tap(user => this._user.set(user))
			);
	}

	// --- Optional: helper to get JWT (for HTTP interceptors) ---
	getToken() {
		return this._jwt;
	}

	// --- Update user info locally and optionally call API ---
	private router = inject(Router);
	updateUserInfo(user: Partial<LoggedInUser>) {
		const current = this._user();
		if (!current) return;

		const updatedUser = { ...current, ...user };
		this._user.set(updatedUser);
	}

	updateToken(token: string) {
		this._jwt = token;
		if (sessionStorage.getItem(this.JWT_KEY)) {
			sessionStorage.setItem(this.JWT_KEY, token);
		} else if (localStorage.getItem(this.JWT_KEY)) {
			localStorage.setItem(this.JWT_KEY, token);
		}
	}

	handleUnauthorized(error: HttpErrorResponse) {
		this.logout();
		if (!error.url?.includes(`/${environment.apiVersion}/me`)) {
			this.router.navigate(['login']).then(() => {});
		}
	}
}
