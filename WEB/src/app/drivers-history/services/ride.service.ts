import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Ride } from '../models/ride.model';

@Injectable({
  providedIn: 'root'
})
export class RideService {

  private rides: Ride[] = [
    {
      id: '1',
      scheduledAt: new Date('2024-01-10T14:20'),
      startedAt: new Date('2024-01-10T14:30'),
      endedAt: new Date('2024-01-10T15:15'),
      status: 'COMPLETED',
      cost: 2500,
      userEmail: 'user1@gmail.com',
      passangersNumber: 1
    },
    {
      id: '2',
      scheduledAt: new Date('2024-01-12T08:50'),
      startedAt: new Date('2024-01-12T09:00'),
      endedAt: new Date('2024-01-12T10:45'),
      status: 'COMPLETED',
      cost: 4200,
      userEmail: 'user2@gmail.com',
      passangersNumber: 2
    }
  ];

  getDriverRideHistory(): Observable<Ride[]> {
    return of(this.rides);
  }
}
