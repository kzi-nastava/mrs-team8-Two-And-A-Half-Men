import { Component, OnInit, signal, Signal } from '@angular/core';
import { Auth } from '../../service/auth';
import { Inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-activation',
  imports: [],
  templateUrl: './activation.html',
  styleUrls: ['./activation.css'],
})
export class ActivationComponent implements OnInit {
  message = signal('');
  private hasSendRequest: boolean = false;

  constructor(@Inject(Auth) private authService: Auth) {}

  ngOnInit(): void {
    console.log('ngOnInit called'); // ✅ Check if called
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
        console.log('Token:', token); // ✅ Check token
    if(this.hasSendRequest) {
      return;
    }
    this.hasSendRequest = true;
    if(!token) {
      this.message.set("Invalid activation link.");
      return;
    }
    this.authService.activateAccount(token).subscribe({
      next: (response) => {
        console.log('Activation response:', response); // ✅ Check response

        this.message.set(response.message);
      },
      error: (err) => {
        this.message.set("Activation failed. Please try again.");
      }
    });
  }

}
