import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookedRide } from '../../models/BookedRide';
import { CustomerRideService } from '../../services/customer-ride-service';
import { CancelRideButton } from '../../components/cancel-ride-button/cancel-ride-button';
import { Router } from '@angular/router';

@Component({
	selector: 'app-booked-rides',
	imports: [CommonModule, CancelRideButton],
	templateUrl: './booked-rides-page.html',
	styleUrls: ['./booked-rides-page.css'],
})
export class BookedRides implements OnInit {
	public bookedRides = signal<BookedRide[]>([]);
	public BookedRidesService = inject(CustomerRideService);
	public router = inject(Router);

	ngOnInit(): void {
		this.loadRides();
	}
	loadRides(): void {
		this.BookedRidesService.loadBookedRides().subscribe({
			next: (rides) => this.bookedRides.set(rides),
			error: () => this.bookedRides.set([]),
		});
	}
	RideClicked(ride: BookedRide): void {
		if (this.canTrackRide(ride)) {
			this.router.navigate(['rides', ride.id], { queryParams: { rideId: ride.id } }).then();
		}
	}
	canCancelRide(ride: BookedRide): boolean {
		if (!ride.scheduleTime) return false;
		const scheduledTime = new Date(ride.scheduleTime);
		const currentTime = new Date();
		const timeDifferenceMinutes =
			(scheduledTime.getTime() - currentTime.getTime()) / 1000 / 60;
		return timeDifferenceMinutes > 10;
	}
	canTrackRide(ride: BookedRide): boolean {
		return ride.status === 'ACTIVE';
	}
	isValidDate(value: any): boolean {
		if (!value) return false;
		if (typeof value === 'string' && value.toLowerCase() === 'immediate') return false;
		const date = new Date(value);
		return !isNaN(date.getTime());
	}
}
