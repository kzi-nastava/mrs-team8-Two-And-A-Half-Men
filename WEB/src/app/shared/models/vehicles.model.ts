export interface VehicleInfo {
	id?: number;
	type: string;
	model: string;
	licensePlate: string;
	numberOfSeats: number;
	additionalServices: string[];
}

export interface VehicleType {
	id: number;
	typeName: string;
	description: string;
	price: number;
}

export interface AdditionalService {
	id: number;
	name: string;
	description: string;
}

export interface VehicleOptions {
	vehicleTypes: VehicleType[];
	additionalServices: AdditionalService[];
}
