// core/driver-location/auth.service.ts
import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { LoggedInUser } from '@core/models/loggedInUser.model';
import { AuthTokenService } from '@core/services/auth-token.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
	private http = inject(HttpClient);
	private tokenService = inject(AuthTokenService);

	// --- Reactive state ---
	private _user = signal<LoggedInUser | null>(null);
	readonly user = this._user.asReadonly();
	public readonly userProfileImage = computed(
		() => this._user()?.imgSrc || 'assets/default-profile.png',
	);

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
	updateUserInfo(user: Partial<LoggedInUser>) {
		const current = this._user();
		if (!current) return;

		this._user.update((current) => ({ ...current!, ...user }));
	}

	updateToken(token: string) {
		this.tokenService.setToken(token);
	}
}
