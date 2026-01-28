import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BookRideRequest } from '@features/customer/home/models/ride.model';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

@Injectable({
	providedIn: 'root',
})
export class RideService {
	private http = inject(HttpClient);

	createRide(request: BookRideRequest): Observable<any> {
		return this.http.post<BookRideRequest>(`/api/${environment.apiVersion}/rides`, request)
	}
}
