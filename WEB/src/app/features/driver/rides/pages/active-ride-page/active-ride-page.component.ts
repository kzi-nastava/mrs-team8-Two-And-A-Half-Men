import { Component, inject, signal } from '@angular/core';
import { EndRideButtonComponent } from '@features/driver/rides/components/end-ride-button/end-ride-button.component';
import { PanicButtonComponent } from '@shared/components/panic-button/panic-button.component';
import { DriverRideService } from '@features/driver/rides/services/driver-ride.service';
import { ActiveRide } from '@features/driver/rides/models/active-ride.model';

@Component({
	selector: 'app-active-ride-page',
	imports: [EndRideButtonComponent, PanicButtonComponent],
	providers: [DriverRideService],
	templateUrl: './active-ride-page.component.html',
	styleUrl: './active-ride-page.component.css',
})
export class ActiveRidePageComponent {
	private driverRideService = inject(DriverRideService);

	activeRide = signal<ActiveRide | null>(null);

	ngOnInit() {
		console.log("AAA")
		this.driverRideService.getActiveRide().subscribe({
			next: activeRide => {
				this.activeRide.set(activeRide);
			},
			error: err => {
				console.log(err);
			}
		});
	}
}
