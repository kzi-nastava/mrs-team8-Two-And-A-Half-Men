import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { NoteResponse, RideTracking } from '@features/customer/rides/models/ride.model';
import { environment } from '@environments/environment';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class RideService {
	private http = inject(HttpClient);

	getActiveRide(accessToken: string | null): Observable<RideTracking> {
		let params = new HttpParams();

		if (accessToken) {
			params = params.set('accessToken', accessToken);
		}

		return this.http.get<RideTracking>(`/api/${environment.apiVersion}/rides/me/active`, { params });
	}

	saveNote(
		rideId: number,
		note: string,
		accessToken: string | null,
	): Observable<NoteResponse> {
		let params = new HttpParams();

		if (accessToken) {
			params = params.set('accessToken', accessToken);
		}

		return this.http.post<NoteResponse>(
			`/api/${environment.apiVersion}/rides/${rideId}/notes`,
			{ note },
			{ params },
		);
	}
}
