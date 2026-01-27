import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenService {
	// --- Storage key ---
	private readonly TOKEN_KEY = 'jwt_token';
	private token: string | null = null;

	setToken(token: string, sessionOnly: boolean = false): void {
		this.token = token;
		if (sessionOnly) {
			sessionStorage.setItem(this.TOKEN_KEY, token);
			localStorage.removeItem(this.TOKEN_KEY);
		} else {
			localStorage.setItem(this.TOKEN_KEY, token);
			sessionStorage.removeItem(this.TOKEN_KEY);
		}
	}

	removeToken(): void {
		this.token = null;
		sessionStorage.removeItem(this.TOKEN_KEY);
		localStorage.removeItem(this.TOKEN_KEY);
	}

	getToken(): string | null {
		if (this.token) {
			return this.token;
		}
		const sessionToken = sessionStorage.getItem(this.TOKEN_KEY);
		if (sessionToken) {
			this.token = sessionToken;
			return sessionToken;
		}
		const localToken = localStorage.getItem(this.TOKEN_KEY);
		if (localToken) {
			this.token = localToken;
			return localToken;
		}
		return null;
	}
}
