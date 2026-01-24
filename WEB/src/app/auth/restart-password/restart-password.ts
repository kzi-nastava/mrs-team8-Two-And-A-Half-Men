import { Component } from '@angular/core';
import { FormGroup, Validators, FormControl, ReactiveFormsModule } from '@angular/forms';
import { Auth } from '../../service/auth';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-restart-password',
  imports: [ReactiveFormsModule],
  templateUrl: './restart-password.html',
  styleUrl: './restart-password.css',
})
export class RestartPassword {
  constructor(private authService: Auth ,private router: Router) {}
     restartPasswordForm = new FormGroup({
    password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    confirmPassword: new FormControl('', [Validators.required])
  });
  onSubmit() {
    if (this.restartPasswordForm.valid) {
      const password = (this.restartPasswordForm.get('password')?.value ?? '') as string;
      const confirmPassword = (this.restartPasswordForm.get('confirmPassword')?.value ?? '') as string;
      if(password !== confirmPassword) {
        this.restartPasswordForm.get('confirmPassword')?.setErrors({ mismatch: true });
        return;
      }
      const token = new URLSearchParams(window.location.search).get('token') || '';
      if(!token) {
        return;
      }
      // Call the Auth service to reset the password
      this.authService.restartPassword(token, password).subscribe({
        next: (response) => {
          Swal.fire('Success', 'Password has been reset successfully.', 'success');
          this.router.navigate(['/login']);
        },
        error: (error) => {
          Swal.fire('Error', 'Failed to reset password. Please try again.', 'error');
        }
      });

    }
  }

}
