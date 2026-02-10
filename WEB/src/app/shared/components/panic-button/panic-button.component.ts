import { Component, inject } from '@angular/core';
import { PanicService } from '@shared/services/panic.service';
import { ButtonDirective } from '@shared/directives/button/button.directive';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-panic-button',
	imports: [ButtonDirective],
	templateUrl: './panic-button.component.html',
	styleUrl: './panic-button.component.css',
})
export class PanicButtonComponent {
	private panicService = inject(PanicService);
	private popupsService = inject(PopupsService);

	triggerPanic() {
		const urlParams = new URLSearchParams(window.location.search);
		const token = urlParams.get('token');

		this.panicService.triggerPanic(token).subscribe({
			next: (response) => {
				this.popupsService.success('Panic Alert', 'Panic alert sent successfully!');
			},
			error: (error) => {
				this.popupsService.error('Error', 'Failed to send panic alert. Please try again.');
			},
		});
	}
}
