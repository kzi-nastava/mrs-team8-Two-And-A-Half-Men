import { Component } from '@angular/core';
import { PanicService } from '../../service/panic-service';

@Component({
  selector: 'app-panic-button',
  imports: [],
  templateUrl: './panic-button.html',
  styleUrl: './panic-button.css',
})
export class PanicButton {
  constructor(private panicService: PanicService) {}

  triggerPanic() {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    
    this.panicService.panicAlert(token).subscribe(
      (response) => {
        console.log('Panic alert sent successfully:', response);
      },
      (error) => {
        console.error('Error sending panic alert:', error);
      }
    );
  }

}
