import { Injectable } from '@angular/core';
import { User } from '../auth/models/user.model';
import { signal } from '@angular/core';


@Injectable({
  providedIn: 'root',
})
export class Auth {
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
}
