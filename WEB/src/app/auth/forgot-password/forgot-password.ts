import { Component } from '@angular/core';
import { FormGroup, Validators, FormControl } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { Auth } from '../../service/auth';
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
      const result = this.authService.forgotPassword(username);
      if (result) {
        alert('Password reset link sent to your email!');
      } else {
        alert('No account found with that email.');
      }
    }
  }

}
