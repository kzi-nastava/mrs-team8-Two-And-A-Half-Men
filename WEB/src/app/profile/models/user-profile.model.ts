// src/app/models/user-profile.model.ts

export interface UserProfile {
	id: string;
	firstName: string;
	lastName: string;
	phoneNumber: string;
	address: string;
	email: string;
	photoUrl?: string;
}

export interface VehicleInfo {
	type: string;
	numberOfSeats: number;
	model: string;
	plateNumber: string;
	additionalServices: string[];
}

export interface VehicleType {
	id: string;
	name: string;
	description?: string;
}

export interface AdditionalService {
	id: string;
	name: string;
	enabled: boolean;
}

export interface PasswordChange {
	newPassword: string;
	confirmPassword: string;
}
