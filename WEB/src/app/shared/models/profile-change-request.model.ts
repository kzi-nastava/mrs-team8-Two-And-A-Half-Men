export interface PendingChangeRequest {
	id: number;
	firstName?: string;
	lastName?: string;
	phoneNumber?: string;
	address?: string;
	email?: string;
	imgSrc?: string | null;
	vehicleType?: string;
	model?: string;
	licensePlate?: string;
	numberOfSeats?: number;
	additionalServices?: string[];
}
