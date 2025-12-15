import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { FormGroup, Validators, FormControl, FormBuilder } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Auth } from '../../service/auth';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {
  loginFailed = false;
  loginForm = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    rememberMe: new FormControl(false)
  });

  constructor(private authService: Auth) {}


  onSubmit() {
    if (this.loginForm.valid) {
      const username = this.loginForm.get('username')?.value ?? '';
      const password = this.loginForm.get('password')?.value ?? '';
      const rememberMe = !!this.loginForm.get('rememberMe')?.value;
      const user = this.authService.login(username, password, rememberMe);
      if (user) {
        console.log('Login successful', user);
        alert(`Welcome, ${user.firstName} ${user.lastName}!`);
        // Handle remember me functionality here
      } else {
        console.log('Login failed');
        this.loginFailed = true;
      }

    } else {
      console.log('Form is invalid');
      alert('Please fill in the form correctly.');
    }
  }
}
