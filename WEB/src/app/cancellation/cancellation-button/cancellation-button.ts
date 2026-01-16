import { Component, Input } from '@angular/core';
import Swal from 'sweetalert2';
import { CancelationService } from '../../service/cancelation';
@Component({
  selector: 'app-cancellation-button',
  imports: [],
  templateUrl: './cancellation-button.html',
  styleUrl: './cancellation-button.css',
})
export class CancellationButton {

  constructor(private cancelationService: CancelationService) {}

  @Input() itemId ?: string|number;
    cancel(): void {
      //implement role logic later
        if(true) {
          Swal.fire({
      title: 'Cancellation Reason',
      input: 'textarea',
      inputLabel: 'Please provide a reason for cancellation',
      inputPlaceholder: 'Enter your reason here...',
      inputAttributes: {
        'aria-label': 'Enter your cancellation reason'
      },
      showCancelButton: true,
      confirmButtonText: 'Cancel',
      cancelButtonText: 'Close',
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      inputValidator: (value) => {
        if (!value) {
          return 'You need to provide a reason!';
        }
        return null;
      }
    }).then((result) => {
      if (result.isConfirmed) {
        const message = this.cancelationService.createCancellationRequest(result.value ?? '', this.itemId ?? '');
        message.subscribe({
          next: (response) => {
             Swal.fire('Cancelled!', 'Your cancellation has been processed.', 'success');
          },
          error: (err) => {
            Swal.fire('Error', 'There was an error processing your cancellation.' + err.message, 'error');
          }
        });
       
      }
    });
    }else {
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
          // Add your cancellation logic here
          Swal.fire('Cancelled!', 'Your ride has been cancelled.', 'success');
        }
    });
        }
    }
}
