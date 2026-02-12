export interface StartRideResponse {
	ok: boolean;
	driverStatus: string;
	rideStatus: string;
	message: string;
}

export interface NoteResponse {
	rideId: number;
	passengerMail: number;
	noteText: string;
}
