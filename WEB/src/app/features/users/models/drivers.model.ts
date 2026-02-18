import { PersonalInfo } from '@shared/models/personal-info.model';
import { VehicleInfo } from '@shared/models/vehicles.model';

export interface DriverInformation {
	personalInfo: PersonalInfo;
	vehicleInfo: VehicleInfo;
}

export interface DriverRegistrationRequestPersonal {
	firstName: string;
	lastName: string;
	email: string;
	address: string;
	phoneNumber: string;
}

export interface DriverRegistrationRequestVehicle {
	// Vehicle info
	model: string
	licensePlate: string
	numberOfSeats: number
	typeId: number
	additionalServicesIds: number[]
}

export interface DriverRegistrationRequest {
	personalInfo: DriverRegistrationRequestPersonal
	vehicleInfo: DriverRegistrationRequestVehicle
}

export interface DriverRegistrationResponse {
	vehicleId: number,
	driverId: number,
	ok: boolean,
	message: string
}
