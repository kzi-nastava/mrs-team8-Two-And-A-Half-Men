import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { Ride } from '@shared/models/ride.model';

@Injectable({
	providedIn: 'root',
})
export class CustomerRideService {
	private http = inject(HttpClient);

	loadBookedRides(): Observable<Ride[]> {
		return this.http.get<Ride[]>(`api/${environment.apiVersion}/rides/booked`);
	}
}
