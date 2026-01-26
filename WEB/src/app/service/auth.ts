import { inject, Injectable } from '@angular/core';
import { User } from '../auth/models/user.model';
import { signal } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AuthResponse } from '../auth/models/authmodel';
import { Login } from '../auth/models/loginmodel';
import { HttpHeaders } from '@angular/common/http';
import { JwtHelperService } from '@auth0/angular-jwt';
import {AuthService} from '@core/services/auth-service.service';
import { LoggedInUser } from '@core/models/loggedInUser.model';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  constructor() {}
public login(logindata: Login, rememberMe: boolean): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('http://localhost:8080/api/v1/login', logindata).pipe(
      tap((response) => {
        this.authService.saveLogin(
			response.accessToken,
			{
				role: response.role,
				firstName: response.firstName,
				lastName: response.lastName,
				email: response.email,
				imgSrc: response.imgUrl
			},
			rememberMe);
      })
    );
  }
public forgotPassword(email: string): Observable<{ message: string }> {
  return this.http.post<{ message: string }>('http://localhost:8080/api/v1/forgot-password', { email });
  }
public logOut(): void {
  if (this.isLoggedInLocalStorage()) {
    localStorage.removeItem('authTokenUser');
  }
  if (this.isLoggedInSessionStorage()) {
    sessionStorage.removeItem('authTokenUser');
  }
  this.user.set(null);
  this.role.set("");
}
public Registar(user : User): Observable<{ message : string }> {

  return this.http.post<{ message : string }>('http://localhost:8080/api/v1/users/register', user);

}
  private headers = new HttpHeaders({
    'Content-Type': 'application/json',
    skip: 'true',
  });


  user = signal<User | null>(null);
  role = signal<string>("");
  getRole(): any {
    if (this.isLoggedInSessionStorage()) {
      const accessToken: any = sessionStorage.getItem('authTokenUser');
      const helper = new JwtHelperService();
      this.role.set(helper.decodeToken(accessToken).role);
      return helper.decodeToken(accessToken).role;
    } else if (this.isLoggedInLocalStorage()) {
      const accessToken: any = localStorage.getItem('authTokenUser');
      const helper = new JwtHelperService();
      this.role.set(helper.decodeToken(accessToken).role);
      return helper.decodeToken(accessToken).role;
    }
    this.role.set("");
    return "";
  }

  isLoggedInLocalStorage(): boolean {
    return localStorage.getItem('authTokenUser') != null;
  }
  isLoggedInSessionStorage(): boolean {
    return sessionStorage.getItem('authTokenUser') != null;
  }


public activateAccount(token: string): Observable<{ message: string }> {
  // Create the service method for link to backend
  return  this.http.post<{ message: string }>('http://localhost:8080/api/v1/activate', { token });
}
public setUser(email: string, imgSrc: string, firstName: string, lastName: string): void {

  var role = this.getRole();
  this.user.set({ email: email, imgUrl: imgSrc, firstName: firstName, lastName: lastName, role: role });
}
public restartPassword(token: string, newPassword: string): Observable<{ message: string }> {
  return this.http.post<{ message: string }>('http://localhost:8080/api/v1/reset-password', { token, newPassword });
}
}
