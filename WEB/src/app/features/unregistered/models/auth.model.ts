export interface AuthResponse {
	accessToken: string;
	email: string;
	imgUrl: string | null;
	firstName: string;
	lastName: string;
	role: string;
}

export interface Login {
	username: string;
	password: string;
}

export interface ResetPasswordDTO {
	token: string;
	newPassword: string;
}

export interface User {
	id?: string;
	role?: string;
	email?: string;
	firstName?: string;
	lastName?: string;
	password?: string;
	address?: string;
	phone?: string;
	imgUrl?: string;
	isBlocked?: boolean;
	isActive?: boolean;
}
