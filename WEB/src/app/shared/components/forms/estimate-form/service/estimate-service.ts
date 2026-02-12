import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NominatimResult } from '@shared/models/nominatim-results.model';

@Injectable({
	providedIn: 'root',
})
export class EstimateService {
	constructor(private http: HttpClient) {}

	estimateTime(start: NominatimResult, end: NominatimResult) {
		const bookingParams = {
			routeId: null,
			route: [
				{ address: start.display_name, latitude: start.lat, longitude: start.lon },
				{ address: end.display_name, latitude: end.lat, longitude: end.lon },
			],
		};
		console.log('Booking Params:', bookingParams);

		return this.http.post<{ time: number }>('/api/v1/rides/estimates', bookingParams);
	}
}
