// navbar.component.ts
import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CancellationButton } from "../cancellation/cancellation-button/cancellation-button";
import { PanicButton } from "../panic/panic-button/panic-button";
import { WebSocket } from '../service/web-socket';
import { EndRideBtn } from '../end-ride/end-ride-btn/end-ride-btn';
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, PanicButton, CancellationButton, EndRideBtn],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent {

  constructor(private router: Router, private webSocket: WebSocket) {}
  private isOk = signal(true);
  ngOnInit() {
    if(this.isOk() == true)
    { 
      this.webSocket.connect();
      this.isOk.set(false);
    }
  }
  onProfileClick() {
    this.router.navigate(['/profile']);
  }

  onDriversRidesClick() {
  this.router.navigate(['/drivers-history']);
  }

  onLoginClick() {
    this.router.navigate(['/login']);
  }

  onRegisterClick() {
    this.router.navigate(['/register']);
  }
}