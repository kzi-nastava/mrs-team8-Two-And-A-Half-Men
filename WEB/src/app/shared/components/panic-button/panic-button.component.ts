import { Component, inject } from '@angular/core';
import { PanicService } from '@shared/services/panic.service';
import { ButtonDirective } from '@shared/directives/button/button.directive';

@Component({
	selector: 'app-panic-button',
	imports: [ButtonDirective],
	templateUrl: './panic-button.component.html',
	styleUrl: './panic-button.component.css',
})
export class PanicButtonComponent {
	private panicService = inject(PanicService);

	triggerPanic() {
		const urlParams = new URLSearchParams(window.location.search);
		const token = urlParams.get('token');

		this.panicService.triggerPanic(token).subscribe({
			next: (response) => {
				console.log('Panic alert sent successfully:', response);
			},
			error: (error) => {
				console.error('Error sending panic alert:', error);
			},
		});
	}
}
