import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@environments/environment';
import { Observable } from 'rxjs';

export interface RatingResponse {
	passengerId: number;
	rideId: number;
	vehicleRating: number;
	driverRating: number;
	comment: string;
}

@Injectable({
	providedIn: 'root',
})
export class RatingService {
	private http = inject(HttpClient);

	submitRating(
		rideId: number,
		ratingData: { driverRating: number; vehicleRating: number; comment: string },
		accessToken: string | null,
	): Observable<RatingResponse> {
		console.log('AAA');
		let params = new HttpParams();
		if (accessToken) {
			params = params.set('accessToken', accessToken);
		}

		return this.http.post<RatingResponse>(
			`/api/${environment.apiVersion}/rides/${rideId}/rating`,
			ratingData,
			{ params },
		);
	}
}
