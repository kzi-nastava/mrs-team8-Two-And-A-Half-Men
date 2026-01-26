// drivers-history.component.ts
import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RideService } from './services/ride.service';
import { Ride } from './models/ride.model';

@Component({
  selector: 'app-drivers-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './drivers-history.html',
  styleUrls: ['./drivers-history.css']
})
export class DriversHistoryComponent implements OnInit {

  selectedRide = signal<Ride | null>(null);
  startDate = signal<string | null>(null);
  endDate = signal<string | null>(null);

  constructor(public rideService: RideService) {}

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
