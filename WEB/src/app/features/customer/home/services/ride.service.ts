import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BookRideRequest, BookRideResponse } from '@features/customer/home/models/ride.model';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

@Injectable({
	providedIn: 'root',
})
export class RideService {
	private http = inject(HttpClient);

	createRide(request: BookRideRequest): Observable<BookRideResponse> {
		return this.http.post<BookRideResponse>(`/api/${environment.apiVersion}/rides`, request)
	}
}
