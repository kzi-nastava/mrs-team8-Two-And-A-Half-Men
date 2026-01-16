import { inject, Injectable } from '@angular/core';
import { User } from '../auth/models/user.model';
import { signal } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AuthResponse } from '../auth/models/authmodel';
import { Login } from '../auth/models/loginmodel';
import { HttpHeaders } from '@angular/common/http';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly http = inject(HttpClient);

  constructor() {}
public login(logindata: Login): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('http://localhost:8080/api/v1/login', logindata);
  }
public forgotPassword(email: string): boolean {
    
    return true;
  }
public Registar(user : User): Observable<{ message : string }> { 

  return this.http.post<{ message : string }>('http://localhost:8080/api/v1/users/register', user);

}
  private headers = new HttpHeaders({
    'Content-Type': 'application/json',
    skip: 'true',
  });


  user = signal<String | null>(null);
   getRole(): any {
    if (this.isLoggedInSessionStorage()) {
      const accessToken: any = sessionStorage.getItem('authTokenUser');
      const helper = new JwtHelperService();
      return helper.decodeToken(accessToken).role;
    } else if (this.isLoggedInLocalStorage()) {
      const accessToken: any = localStorage.getItem('authTokenUser');
      const helper = new JwtHelperService();
      return helper.decodeToken(accessToken).role;
    }

    return null;
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
public setUser(): void {
  this.user.set(this.getRole());
}

}