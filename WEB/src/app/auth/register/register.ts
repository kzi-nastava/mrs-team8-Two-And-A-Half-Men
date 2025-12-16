import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { FormGroup, Validators, FormControl, FormBuilder } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Auth } from '../../service/auth';

@Component({
  selector: 'app-register',
  imports: [ ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './register.html',
  standalone: true,
  styleUrl: './register.css',
})
export class Register {
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

      if (password !== confirmPassword) {
        alert('Passwords do not match.');
        return;
      }
      
      const user = {
        firstName,
        lastName,
        phoneNumber,
        address,
        email,
        password
      };
      console.log('Registration successful', user);
      alert(`Registration successful! Welcome, ${firstName} ${lastName}!`);
      // Here you would typically send the user data to your backend for registration
      
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
