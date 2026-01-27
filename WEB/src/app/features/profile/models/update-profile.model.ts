
import { UserProfile } from './user-profile.model'; // Adjust path if needed


export interface UpdateProfileRequest {
	firstName?: string | null;
	lastName?: string | null;
	email?: string | null;
	address?: string | null;
	phoneNumber?: string | null;
	imgSrc?: string | null;

	// Vehicle info
	model?: string | null;
	licensePlate?: string | null;
	numberOfSeats?: number | null;
	vehicleTypeId?: number | null;
	additionalServiceIds?: number[] | null;
}

export interface UpdateProfileResponse {
	accessToken?: string;
	profile: UserProfile;
}

export interface UploadPictureResponse {
	filePath: string;
	ok: boolean
}

export interface ChangePasswordRequest {
	oldPassword: string;
	newPassword: string;
	confirmNewPassword: string;
}

export interface ChangePasswordResponse {
	accessToken: string;
	expiresIn?: number;
}
