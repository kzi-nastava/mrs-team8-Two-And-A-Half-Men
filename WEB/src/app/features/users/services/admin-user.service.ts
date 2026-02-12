import { inject, Injectable } from '@angular/core';
import {
	DriverRegistrationRequest,
	DriverRegistrationResponse,
} from '@features/users/models/drivers.model';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '@environments/environment';
import {
	UserDetailResponse,
	UserFilters,
	UserPageResponse,
} from '@features/users/models/user.model';

@Injectable()
export class AdminUserService {
	private http = inject(HttpClient);

	registerDriver(
		registrationRequest: DriverRegistrationRequest,
	): Observable<DriverRegistrationResponse> {
		return this.http.post<DriverRegistrationResponse>(
			`/api/${environment.apiVersion}/register/drivers`,
			registrationRequest,
		);
	}

	getUsers(
		page: number = 0,
		size: number = 10,
		sort?: string,
		direction: 'ASC' | 'DESC' = 'ASC',
		filters?: UserFilters,
	): Observable<UserPageResponse> {
		let params = new HttpParams().set('page', page.toString()).set('size', size.toString());

		if (sort) {
			params = params.set('sortBy', sort);
			params = params.set('sortDirection', direction);
		}

		// Add filters if provided
		if (filters) {
			Object.keys(filters).forEach((key) => {
				const value = filters[key as keyof UserFilters];
				if (value !== undefined && value !== null && value !== '') {
					params = params.set(key, value.toString());
				}
			});
		}

		return this.http.get<UserPageResponse>(`/api/${environment.apiVersion}/users`, { params });
	}

	getUserDetails(userId: number): Observable<UserDetailResponse> {
		return this.http.get<UserDetailResponse>(`/api/${environment.apiVersion}/users/${userId}`);
	}

	blockUser(userId: number, reason: string): Observable<void> {
		return this.http.patch<void>(`/api/${environment.apiVersion}/users/${userId}/block`, { reason });
	}

	unblockUser(userId: number): Observable<void> {
		return this.http.patch<void>(`/api/${environment.apiVersion}/users/${userId}/unblock`, {});
	}

	approveChangeRequest(requestId: number): Observable<void> {
		return this.http.post<void>(`/api/${environment.apiVersion}/profile-update-requests/${requestId}/approve`, {});
	}

	rejectChangeRequest(requestId: number): Observable<void> {
		return this.http.post<void>(`/api/${environment.apiVersion}/profile-update-requests/${requestId}/reject`, {});
	}
}
