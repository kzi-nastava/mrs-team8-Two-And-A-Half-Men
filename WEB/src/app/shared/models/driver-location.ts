export interface DriverLocation {
	driverId: number;
	driverName?: string;
	driverEmail?: string;
	latitude: number;
	longitude: number;
	isActive?: boolean;
	isOccupied: boolean;
	currentRideId?: number | null;
	vehicleTypeName?: string;
	timestamp?: number;
}
