import { Component } from '@angular/core';

@Component({
  selector: 'app-end-ride-btn',
  imports: [],
  templateUrl: './end-ride-btn.html',
  styleUrl: './end-ride-btn.css',
})
export class EndRideBtn {

  endRide() {
    console.log('Ride ended');
    // Add logic to end the ride here
  }
  
}
