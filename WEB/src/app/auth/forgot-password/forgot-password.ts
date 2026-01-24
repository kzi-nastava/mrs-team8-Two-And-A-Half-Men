import { Component } from '@angular/core';
import { FormGroup, Validators, FormControl } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { Auth } from '../../service/auth';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-forgot-password',
  imports: [ReactiveFormsModule],
  templateUrl: './forgot-password.html',
  styleUrls: ['./forgot-password.css'],
})
export class ForgotPassword {

   forgotPasswordForm = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.email])
  });
  constructor(private authService: Auth) {}
  onSubmit() {
    if (this.forgotPasswordForm.valid) {
      const username = (this.forgotPasswordForm.get('username')?.value ?? '') as string;
      this.authService.forgotPassword(username).subscribe({
        next: (response) => {
          Swal.fire('Success', "Link sent to your email", 'success');
        },
        error: (error) => {
            Swal.fire('Error', 'Failed to send password reset email. Please try again.', 'error');
        }
      });
    }
  }

}
