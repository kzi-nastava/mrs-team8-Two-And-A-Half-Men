import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { FormGroup, Validators, FormControl, FormBuilder } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Inject } from '@angular/core';
import { DOCUMENT } from '@angular/core';

import { Auth } from '../../service/auth';

@Component({
  selector: 'app-register',
  imports: [ ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './register.html',
  standalone: true,
  styleUrls: ['./register.css'],
})
export class Register {
  constructor(@Inject(DOCUMENT) private document: Document, private authService: Auth) {}
  step = 1;
  registerForm = new FormGroup({
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    phoneNumber : new FormControl('', [Validators.required, Validators.pattern('^\\+?[1-9]\\d{1,14}$')]),
    address : new FormControl('', [Validators.required]),
    email:new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    confirmPassword: new FormControl('', [Validators.required ])
  });


  onSubmit() {
    if (this.registerForm.valid) {
      const firstName = this.registerForm.get('firstName')?.value ?? '';
      const lastName = this.registerForm.get('lastName')?.value ?? '';
      const phoneNumber = this.registerForm.get('phoneNumber')?.value ?? '';
      const address = this.registerForm.get('address')?.value ?? '';
      const email = this.registerForm.get('email')?.value ?? '';
      const password = this.registerForm.get('password')?.value ?? '';
      const confirmPassword = this.registerForm.get('confirmPassword')?.value ?? '';
      const errorEl = document.getElementById('error-register');
       if (errorEl) {
          errorEl.innerHTML = "";
        }
      if (password !== confirmPassword) {
        if (errorEl) {
          errorEl.innerHTML = "Passwords do not match";
        }
        return;
      }      
      const user = {
        id: null,
        firstName,
        lastName,
        phone : phoneNumber,
        address,
        email,
        password,
        imgUrl : null,
        isActive: null,
        isBlocked: null,
        role: null
      };
      const result = this.authService.Registar(user);
      if(result != "") { 
        if (errorEl) {
          errorEl.innerHTML = result;
        }
        return;
      }
    } else {
      console.log('Form is invalid');
      alert('Please fill in the form correctly.');
    }
  }
  previous() { 
    this.step = this.step - 1;
  }
  next() {
    if (this.isValid()) {
    this.step = this.step + 1;
  }
  }
  isValid() : boolean
  { 
    if(this.step === 1) {
      if(this.registerForm.get('firstName')?.invalid) {
        this.registerForm.get('firstName')?.markAsTouched();
      }
      if(this.registerForm.get('lastName')?.invalid) {
        this.registerForm.get('lastName')?.markAsTouched();
      }
      if(this.registerForm.get('phoneNumber')?.invalid) {
        this.registerForm.get('phoneNumber')?.markAsTouched();
      }
      if(this.registerForm.get('address')?.invalid) {
        this.registerForm.get('address')?.markAsTouched();
      }
      return !!(this.registerForm.get('firstName')?.valid) && !!(this.registerForm.get('lastName')?.valid) && !!(this.registerForm.get('phoneNumber')?.valid) && !!(this.registerForm.get('address')?.valid);
    }
    return false;
  }
}
