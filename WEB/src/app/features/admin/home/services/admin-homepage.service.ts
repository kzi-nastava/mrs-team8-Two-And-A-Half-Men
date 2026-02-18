import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Ride } from '@shared/models/ride.model';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
	providedIn: 'root',
})
export class AdminHomepageService {
	private http = inject(HttpClient);

	loadActiveRides(driverName: string): Observable<Ride[]> {
		return this.http.get<Ride[]>(`api/${environment.apiVersion}/rides/active`, {params: { name: driverName }});
	}
	loadPanics(): Observable<Ride[]> {
		return this.http.get<Ride[]>(`api/${environment.apiVersion}/rides/panics`);
	}
}
