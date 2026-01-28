import { inject, Injectable } from '@angular/core';
import {
	DriverRegistrationRequest,
	DriverRegistrationResponse,
} from '@features/admin/users/models/drivers.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';

@Injectable()
export class AdminUserService {
	private http = inject(HttpClient);

	registerDriver(
		registrationRequest: DriverRegistrationRequest,
	): Observable<DriverRegistrationResponse> {
		return this.http.post<DriverRegistrationResponse>(
			`/api/${environment.apiVersion}/drivers/register`,
			registrationRequest,
		);
	}
}
