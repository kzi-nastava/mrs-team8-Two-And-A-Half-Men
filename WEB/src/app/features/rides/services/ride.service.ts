import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@environments/environment';
import { CostTime } from '@shared/models/cost-time.model';
import { Observable } from 'rxjs';
import { StartRideResponse } from '@features/rides/models/api-responses.model';
import { Ride } from '@shared/models/ride.model';
import { NoteResponse } from '@features/rides/models/api-responses.model';
import {RatingResponse} from '@shared/models/rating-response.model';

@Injectable({
	providedIn: 'root',
})
export class RideService {
	private http = inject(HttpClient);

	getRide(rideId: number): Observable<Ride> {
		return this.http.get<Ride>(`/api/${environment.apiVersion}/rides/${rideId}`);
	}

	cancelRide(
		rideId: number,
		reason?: string,
		cancelledBy?: string,
	): Observable<{ message: string | null }> {
		return this.http.patch<{ message: string | null }>(
			`/api/${environment.apiVersion}/rides/${rideId}/cancel`,
			{ reason, cancelledBy },
		);
	}

	endRide(rideId: number): Observable<CostTime> {
		return this.http.patch<CostTime>(`/api/${environment.apiVersion}/rides/${rideId}/end`, {});
	}

	finishRide(rideId: number, isInterrupted: boolean, isPayed: boolean): Observable<void> {
		return this.http.patch<void>(`/api/${environment.apiVersion}/rides/${rideId}/finish`, {
			isInterrupted,
			isPayed,
		});
	}

	startRide(rideId: number): Observable<StartRideResponse> {
		return this.http.patch<StartRideResponse>(
			`/api/${environment.apiVersion}/rides/${rideId}/start`,
			{},
		);
	}

	submitRating(
		rideId: number,
		ratingData: { driverRating: number; vehicleRating: number; comment: string },
		accessToken: string | null,
	): Observable<RatingResponse> {
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

	leaveNote(
		rideId: number,
		noteText: string,
		accessToken: string | null,
	): Observable<NoteResponse> {
		let params = new HttpParams();

		if (accessToken) {
			params = params.set('accessToken', accessToken);
		}

		return this.http.post<NoteResponse>(
			`/api/${environment.apiVersion}/rides/${rideId}/notes`,
			{ noteText },
			{ params },
		);
	}

	triggerPanic(token: string | null): Observable<never> {
		if (!token) {
			return this.http.post<never>(`/api/${environment.apiVersion}/rides/panic`, {});
		}
		return this.http.post<never>(
			`/api/${environment.apiVersion}/rides/panic?accessToken=${token}`,
			{},
		);
	}
	handlePanic(rideId: number): Observable<void> {
		return this.http.post<void>(`/api/${environment.apiVersion}/rides/${rideId}/panic/resolve`, {});
	}
}
