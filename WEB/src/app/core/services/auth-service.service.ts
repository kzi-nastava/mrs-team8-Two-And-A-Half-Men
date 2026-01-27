// core/services/auth.service.ts
import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '@environments/environment';
import { LoggedInUser } from '@core/models/loggedInUser.model';
import { Router } from '@angular/router';
import { TokenService } from '@core/services/token.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
	private http = inject(HttpClient);
	private tokenService = inject(TokenService);

	// --- Reactive state ---
	private _user = signal<LoggedInUser | null>(null);
	readonly user = this._user.asReadonly();

	readonly fullName = computed(() => this._user()?.firstName + ' ' + this._user()?.lastName);

	constructor() {
		// On service init, load JWT from storage and fetch user
		const token = this.tokenService.getToken();
		if (token) {
			this.fetchUserInfo();
		}
	}

	// --- Save login result (called by login page) ---
	saveLogin(token: string, user: LoggedInUser, rememberMe = false) {
		this._user.set(user);
		this.tokenService.setToken(token, !rememberMe);
	}
	// --- Logout ---
	logout() {
		this._user.set(null);
		this.tokenService.removeToken();
	}

	// --- Check if user is logged in ---
	isLoggedIn(): boolean {
		return this.tokenService.getToken() !== null;
	}

	// --- Fetch user info from API ---
	fetchUserInfo() {
		return this.http.get<LoggedInUser>(`/api/${environment.apiVersion}/me`).subscribe({
			next: (value) => this._user.set(value),
			error: () => {
				this.logout();
			},
		});
	}

	// --- Update user info locally and optionally call API ---
	private router = inject(Router);
	updateUserInfo(user: Partial<LoggedInUser>) {
		const current = this._user();
		if (!current) return;

		const updatedUser = { ...current, ...user };
		this._user.set(updatedUser);
	}

	getToken(): string | null {
		return this.tokenService.getToken();
	}

	updateToken(token: string) {
		this.tokenService.setToken(token);
	}

	handleUnauthorized(error: HttpErrorResponse) {
		this.logout();
		if (!error.url?.includes(`/${environment.apiVersion}/me`)) {
			this.router.navigate(['login']).then(() => {});
		}
	}
}
