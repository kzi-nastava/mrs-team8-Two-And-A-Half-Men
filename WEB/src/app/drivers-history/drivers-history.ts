import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RideService } from './services/ride.service';
import { Ride } from './models/ride.model';

@Component({
  selector: 'app-drivers-history',
  standalone: true,
  imports: [CommonModule],  
  templateUrl: './drivers-history.html',
  styleUrls: ['./drivers-history.css'],
  providers: [DatePipe]     
})
export class DriversHistoryComponent implements OnInit {

  rides: Ride[] = [];
  selectedRide?: Ride;

  constructor(private rideService: RideService) {}

  ngOnInit(): void {
    this.loadRides();
  }

  loadRides(): void {
    this.rideService.getDriverRideHistory().subscribe(rides => {
      this.rides = rides;
    });
  }

  selectRide(ride: Ride): void {
    this.selectedRide = ride;
  }
}
