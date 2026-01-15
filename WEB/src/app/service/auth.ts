import { inject, Injectable } from '@angular/core';
import { User } from '../auth/models/user.model';
import { signal } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly http = inject(HttpClient);
  private _users = signal<User[]>([{
    id: "1",
    email: "user@example.com",
    role: "user",
    firstName: "John",
    lastName: "Doe",
    password: "password",
    address: "123 Main St",
    phone: "555-555-5555",
    imgUrl: "",
    isBlocked: false,
    isActive: true
  },
  {
    id: "2",
    email: "admin@example.com",
    role: "admin",
    firstName: "Jane",
    lastName: "Smith",
    password: "adminpass",
    address: "456 Elm St",
    phone: "555-555-1234",
    imgUrl: "",
    isBlocked: false,
    isActive: true
  }
]);
private _nextId = 3;
  constructor() {}
public login(email: string, password: string, rememberMe: boolean): User | null {
    const users = this._users();
    const user = users.find(u => u.email === email && u.password === password);
    if (user) {
      // Handle remember me functionality here
      return user;
    }
    return null;
  }
public forgotPassword(email: string): boolean {
    const users = this._users();
    const user = users.find(u => u.email === email);
    return !!user;
  }
public Registar(user : User): Observable<{ message : string }> { 

  return this.http.post<{ message : string }>('http://localhost:8080/api/v1/users/register', user);

}

public activateAccount(token: string): Observable<{ message: string }> {
  // Create the service method for link to backend
  return  this.http.post<{ message: string }>('http://localhost:8080/api/v1/activate', { token });
}

}