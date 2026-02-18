export enum RideStatus {
	PENDING = 'PENDING',
	ACCEPTED = 'ACCEPTED',
	ACTIVE = 'ACTIVE',
	FINISHED = 'FINISHED',
	INTERRUPTED = 'INTERRUPTED',
	CANCELLED = 'CANCELLED',
	PANICKED = 'PANICKED',
}

export interface Ride {
	id: number;
	startTime: Date;
	endTime: Date;
	scheduledTime: Date;

	driverName: string;
	driverId: number;
	rideOwnerName: string;
	rideOwnerId: number;

	status: RideStatus;
	path: string;
	cancellationReason: string;
	cancelledBy?: 'DRIVER' | 'PASSENGER';
	price: number;
	totalCost: number;

	additionalServices: string[];
	locations: Location[];
	routeId: number,
	passengers: PassengerDetails[];
	favourite?: boolean;
}

export interface Location {
	geoHash: string;
	latitude: number;
	longitude: number;
	address: string;
}

export interface PassengerDetails {
	email: string;
	inconsistencyNote?: string;
	driverRating?: number;
	vehicleRating?: number;
	comment?: string;
}
