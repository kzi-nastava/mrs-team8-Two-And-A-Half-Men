import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserProfile } from '@features/profile/models/user-profile.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import {
	ChangePasswordRequest,
	ChangePasswordResponse,
	UpdateProfileRequest,
	UpdateProfileResponse,
	UploadPictureResponse,
} from '@features/profile/models/update-profile.model';

@Injectable({
	providedIn: 'root',
})
export class ProfileService {
	private http = inject(HttpClient);

	getUserProfile(): Observable<UserProfile> {
		return this.http.get<UserProfile>(`/api/${environment.apiVersion}/profile`);
	}

	updateUserProfile(updateRequest: UpdateProfileRequest): Observable<UpdateProfileResponse> {
		return this.http.patch<UpdateProfileResponse>(
			`/api/${environment.apiVersion}/profile`,
			updateRequest,
		);
	}

	uploadPhoto(file: File): Observable<UploadPictureResponse> {
		const formData = new FormData();
		formData.append('file', file); // 'file' is the key expected by your backend

		// PUT request
		return this.http.put<UploadPictureResponse>(
			`/api/${environment.apiVersion}/profile/picture`,
			formData,
		);
	}

	changePassword(request: ChangePasswordRequest): Observable<ChangePasswordResponse> {
		return this.http.patch<ChangePasswordResponse>(
			`/api/${environment.apiVersion}/profile/change-password`,
			request,
		);
	}

	cancelPendingRequest(requestId: number): Observable<{ ok: boolean }> {
		return this.http.post<{ ok: boolean }>(
			`/api/${environment.apiVersion}/profile-update-requests/${requestId}/cancel`,
			null,
		);
	}
}
