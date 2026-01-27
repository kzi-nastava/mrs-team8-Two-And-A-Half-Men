import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { CostTime } from '@shared/models/cost-time.model';
import { Observable } from 'rxjs';

@Injectable()
export class DriverRideService {
	private http = inject(HttpClient);

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
}
