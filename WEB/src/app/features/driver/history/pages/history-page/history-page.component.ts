// drivers-history.component.ts
import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HistoryService } from '@features/driver/history/services/history.service';
import { Ride } from '@features/driver/history/models/ride.model';

@Component({
	selector: 'app-drivers-history',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './history-page.component.html',
	styleUrls: ['./history-page.component.css'],
})
export class DriversHistoryComponent implements OnInit {

	rideService = inject(HistoryService);

	selectedRide = signal<Ride | null>(null);
	startDate = signal<string | null>(null);
	endDate = signal<string | null>(null);

	ngOnInit(): void {
		this.rideService.load();
	}

	selectRide(ride: Ride): void {
		this.selectedRide.set(ride);
	}

	onFilter(): void {
		this.rideService.setDateRange(this.startDate(), this.endDate());
		this.rideService.filter();
	}
}
