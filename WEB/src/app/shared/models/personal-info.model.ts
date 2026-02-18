export interface PersonalInfo {
	id?: number;
	firstName: string;
	lastName: string;
	phoneNumber: string;
	address: string;
	email: string;
	imgSrc?: string | null;
	role?: string;
	blockReason?: string | null;
	blocked?: boolean;
}
