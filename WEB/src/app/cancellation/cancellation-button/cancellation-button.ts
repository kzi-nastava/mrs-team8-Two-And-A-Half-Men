import { Component, Input } from '@angular/core';
import Swal from 'sweetalert2';
import { CancelationService } from '../../service/cancelation';
import { Auth } from '../../service/auth';
@Component({
  selector: 'app-cancellation-button',
  imports: [],
  templateUrl: './cancellation-button.html',
  styleUrl: './cancellation-button.css',
})
export class CancellationButton {

  constructor(private cancelationService: CancelationService, private auth: Auth) {}

  @Input() itemId ?: string|number;
    cancel(): void {
      console.log(this.auth.getRole());
        if(this.auth.getRole() == 'ROLE_DRIVER') {
          Swal.fire({
      title: 'Cancellation Reason',
            html:
            `
             <div style="text-align: left; width: 90%;">
          <label style="display: block; margin-bottom: 10px; font-weight: bold;">
            Cancelled by:
          </label>
          <select id="cancelledBy" class="swal2-select" style="width: 90%; margin-bottom: 15px;">
            <option value="">Select...</option>
            <option value="CUSTOMER">Customer issues</option>
            <option value="DRIVER">Driver issues</option>
          </select>
          
          <label style="display: block; margin-bottom: 10px; font-weight: bold;">
            Reason:
          </label>
          <textarea 
            id="cancellationReason" 
            class="swal2-textarea" 
            placeholder="Enter your reason here..." 
            style="width: 90%; height: 300px; resize: none;"
            aria-label="Enter your cancellation reason"></textarea>
        </div>
        `,
        showCancelButton: true,
        confirmButtonText: 'Cancel Ride',
        cancelButtonText: 'Cancel',
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        preConfirm: () => {
          const reason = (Swal.getPopup()?.querySelector('#cancellationReason') as HTMLTextAreaElement).value;
          const cancelledBy = (Swal.getPopup()?.querySelector('#cancelledBy') as HTMLSelectElement).value;
          if (!reason) {
            return null;
          }
          if (!cancelledBy) {
            return null;
          }
          return `${cancelledBy}: ${reason}`;
        }
      }).then((result) => {
        if (result.isConfirmed) {
          const cancelledBy = result.value.split(': ')[0];
          const reason = result.value.split(': ')[1];
          if (!reason) {
            return;
          }
          const message = this.cancelationService.createCancellationRequest(reason, cancelledBy, this.itemId!);
          message.subscribe({
            next: () => {
              Swal.fire('Cancelled!', 'The ride has been cancelled.', 'success');
            },
            error: (error) => {
              Swal.fire('Error!', 'There was an error cancelling the ride. ' + error.message, 'error');
            }
          });
        }
      });
    }else if(this.auth.getRole() == 'ROLE_CUSTOMER'){
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
