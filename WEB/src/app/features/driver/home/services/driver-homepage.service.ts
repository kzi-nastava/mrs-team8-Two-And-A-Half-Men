import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Ride } from '@shared/models/ride.model';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
	providedIn: 'root',
})
export class DriverHomepageService {
	private http = inject(HttpClient);

	loadActiveRides(): Observable<Ride[]> {
		return this.http.get<Ride[]>(`api/${environment.apiVersion}/rides/my`);
	}
}
