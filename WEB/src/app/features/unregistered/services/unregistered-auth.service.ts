import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '@core/services/auth-service.service';
import { Observable, tap } from 'rxjs';
import { AuthResponse, Login, User } from '@features/unregistered/models/auth.model';
import { environment } from '@environments/environment';

@Injectable({
	providedIn: 'root',
})
export class UnregisteredAuthService {
	private readonly http = inject(HttpClient);
	private readonly authService = inject(AuthService);

	public login(loginData: Login, rememberMe: boolean): Observable<AuthResponse> {
		return this.http.post<AuthResponse>(`/api/${environment.apiVersion}/login`, loginData).pipe(
			tap((response) => {
				this.authService.saveLogin(
					response.accessToken,
					{
						role: response.role,
						firstName: response.firstName,
						lastName: response.lastName,
						email: response.email,
						imgSrc: response.imgUrl,
					},
					rememberMe,
				);
			}),
		);
	}

	public forgotPassword(email: string): Observable<{ message: string }> {
		return this.http.post<{ message: string }>(
			`/api/${environment.apiVersion}/forgot-password`,
			{
				email,
			},
		);
	}

	public register(user: User): Observable<{ message: string }> {
		return this.http.post<{ message: string }>(
			`/api/${environment.apiVersion}/users/register`,
			user,
		);
	}

	public activateAccount(token: string): Observable<{ message: string }> {
		// Create the service method for link to backend
		return this.http.post<{ message: string }>(`/api/${environment.apiVersion}/activate`, {
			token,
		});
	}

	public restartPassword(token: string, newPassword: string): Observable<{ message: string }> {
		return this.http.post<{ message: string }>(
			`/api/${environment.apiVersion}/reset-password`,
			{
				token,
				newPassword,
			},
		);
	}
}
