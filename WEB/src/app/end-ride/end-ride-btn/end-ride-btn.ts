import { Component } from '@angular/core';
import { EndRideService } from '../../end-ride/service/end-ride-service';
import { Input } from '@angular/core';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-end-ride-btn',
  imports: [],
  templateUrl: './end-ride-btn.html',
  styleUrl: './end-ride-btn.css',
})
export class EndRideBtn {
  constructor(private endRideService: EndRideService) {}
  
  @Input() 
  rideId!: number;
  endRide() {
    this.endRideService.endRide(this.rideId).subscribe({
      next: (response) => {
        console.log('Ride ended successfully:', response);
        Swal.fire({
          title: 'Success',
          text: 'Ride ended successfully! Cost: ' + response.cost + ', Time: ' + response.time + ' minutes.',
          icon: 'success',
          confirmButtonText: 'Finnish a ride'
        }).then(() => {
          window.location.reload();
          // TODO logic for finishing a ride
        }
        );
      },
      error: (error) => {
        console.error('Error ending ride:', error);
      }
    });
  }
  
}
