import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { FormGroup, Validators, FormControl, FormBuilder } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterModule } from '@angular/router';
import { Inject } from '@angular/core';
import { DOCUMENT } from '@angular/core';
import { signal } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { Auth } from '../../service/auth';

@Component({
  selector: 'app-register',
  imports: [ ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './register.html',
  standalone: true,
  styleUrls: ['./register.css'],
})
export class Register {
  constructor(@Inject(DOCUMENT) private document: Document, private authService: Auth, private router: Router) {}
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
      const btn = this.document.getElementById('submit-btn');
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
        firstName,
        lastName,
        phone : phoneNumber,
        address,
        email,
        password,
      };
      const result = this.authService.Registar(user);
      btn?.setAttribute('disabled', 'true');
      result.subscribe({
        next: (response) => {
             Swal.fire({
                icon: 'success',
                  title: 'Registered!',
                    text: 'Please check your email to activate your account.',
                  showConfirmButton: true,
                  
            }).then(() => {
              this.router.navigate(['/login']);
            } );

        
        },
        error: (err) => {
          if (errorEl) {
            const errorMessage = err.error?.error || 'An error occurred during registration';
            errorEl.innerHTML = errorMessage;
          }
        }

      });
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
