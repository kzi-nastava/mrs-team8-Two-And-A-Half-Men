import { Injectable } from '@angular/core';
import { HttpClient} from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { BookedRide } from '../models/BookedRide';
import { Ride } from '@shared/models/ride.model';
@Injectable({
  providedIn: 'root',
})
export class CustomerRideService {

  private http = inject(HttpClient);

  cancelRide(rideId: number): Observable<{ message: string | null }> {
    return this.http.patch<{ message: string | null }>(`api/${environment.apiVersion}/rides/${rideId}/cancel`, {});
  }
  loadBookedRides(): Observable<Ride[]> {
    return this.http.get<Ride[]>(`api/${environment.apiVersion}/rides/booked`);
  }

}
