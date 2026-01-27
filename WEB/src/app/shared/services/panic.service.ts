import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import Swal from 'sweetalert2';
import { environment } from '@environments/environment';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class PanicService {
	private http = inject(HttpClient)

	triggerPanic(token: string | null): Observable<never> {
		if (!token) {
			return this.http.post<never>(`/api/${environment.apiVersion}/rides/panic`, {});
		}
		return this.http.post<never>(
			`/api/${environment.apiVersion}/rides/panic?accessToken=${token}`,
			{},
		);
	}

	public handlePanic(data: any) {
		let text = '';
		if (!data) {
			text = 'Panic alert received with no additional data.';
		} else if (data.passengerName) {
			text = `Panic alert received from ride ID: ${data.rideId} driver name: ${data.driverName}.
      Triggered by ${data.passengerName} in location ${data.driverLocation}`;
		} else {
			text = `Panic alert received from ride ID: ${data.rideId} driver name: ${data.driverName}.
       in location ${data.driverLocation}`;
		}
		Swal.fire({
			title: 'Panic Alert',
			text: text,
			icon: 'warning',
			confirmButtonText: 'OK',
		}).then();
	}
}
