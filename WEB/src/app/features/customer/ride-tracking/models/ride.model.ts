export interface RideTracking {
	id: number;
	driverId: number;
	stops: Location[];
	status: string;
	startTime?: Date;
}

export interface Location {
	id: number;
	geoHash: string;
	latitude: number;
	longitude: number;
	address: string;
}

export interface NoteResponse {
	rideId: number;
	passengerMail: number;
	noteText: string;
}
