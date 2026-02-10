import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { CostTime } from '@shared/models/cost-time.model';
import { Observable } from 'rxjs';
import { StartRideResponse } from '@features/driver/rides/models/api-responses.model';
import { ActiveRide } from '@features/driver/rides/models/active-ride.model';

@Injectable()
export class DriverRideService {
	private http = inject(HttpClient);

	getActiveRide() {
		return this.http.get<ActiveRide>(`api/${environment.apiVersion}/rides/me/active`);
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
