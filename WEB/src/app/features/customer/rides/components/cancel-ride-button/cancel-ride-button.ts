import { Component, inject, input, output} from '@angular/core';
import Swal from 'sweetalert2';
import {CustomerRideService} from '../../services/customer-ride-service';

@Component({
  selector: 'app-cancel-ride-button',
  imports: [],
  templateUrl: './cancel-ride-button.html',
  styleUrl: './cancel-ride-button.css',
})
export class CancelRideButton {

  	rideId = input.required<number>();
    cancelled = output<void>();
    customerRideService = inject(CustomerRideService);
    cancel() {  
Swal.fire({ 
    
        title: 'Cancelation',
        text: 'Are you sure you want to cancel the ride?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, cancel it!',
        cancelButtonText: 'No, keep it',
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
      }).then((result) => {
        if (result.isConfirmed) {
        const message = this.customerRideService.cancelRide(this.rideId());
         message.subscribe({
      next: () => {
        Swal.fire('Cancelled!', 'The ride has been cancelled.', 'success');
        this.cancelled.emit();
      },
      error: (error) => {
        Swal.fire('Error!', 'There was an error cancelling the ride. ' + error.message, 'error');
        }
    });
        }
    });
    }
  }

