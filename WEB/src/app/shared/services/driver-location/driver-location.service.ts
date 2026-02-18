import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DriverLocation } from '@shared/models/driver-location';
import { environment } from '@environments/environment';

@Injectable({
	providedIn: 'root',
})
export class DriverLocationService {
	private http = inject(HttpClient);

	getAllDriverLocations(): Observable<DriverLocation[]> {
		return this.http.get<DriverLocation[]>(`/api/${environment.apiVersion}/drivers/locations`);
	}

	getDriverLocation(driverId: number): Observable<DriverLocation> {
		return this.http.get<DriverLocation>(
			`/api/${environment.apiVersion}/drivers/locations/${driverId}`,
		);
	}

	getNearbyDrivers(
		longitude: number,
		latitude: number,
		radiusKm: number = 5,
	): Observable<DriverLocation[]> {
		return this.http.get<DriverLocation[]>(
			`/api/${environment.apiVersion}/drivers/locations/nearby`,
			{
				params: {
					longitude: longitude.toString(),
					latitude: latitude.toString(),
					radiusKm: radiusKm.toString(),
				},
			},
		);
	}
}
