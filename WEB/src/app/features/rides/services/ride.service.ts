import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { CostTime } from '@shared/models/cost-time.model';
import { Observable, tap } from 'rxjs';
import { StartRideResponse } from '@features/rides/models/api-responses.model';
import { ActiveRide } from '@features/rides/models/active-ride.model';
import { Ride } from '@shared/models/ride.model';

@Injectable({
	providedIn: 'root'
})
export class RideService {
	private http = inject(HttpClient);

	getActiveRide() {
		return this.http.get<ActiveRide>(`api/${environment.apiVersion}/rides/me/active`);
	}

	getRide(rideId: number): Observable<Ride> {
		return this.http
			.get<Ride>(`/api/${environment.apiVersion}/rides/${rideId}`);
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

	startRide(rideId: number): Observable<StartRideResponse> {
		return this.http.patch<StartRideResponse>(
			`/api/${environment.apiVersion}/rides/${rideId}/start`,
			{},
		);
	}
}
