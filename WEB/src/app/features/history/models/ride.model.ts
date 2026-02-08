export enum RideStatus {
	PENDING = "PENDING",
	ACCEPTED = "ACCEPTED",
	ACTIVE = "ACTIVE",
	FINISHED = "FINISHED",
	INTERRUPTED = "INTERRUPTED",
	CANCELLED  = "CANCELLED",
	PANICKED = "PANICKED",
}

export interface Ride {
  id: number;
  startTime: Date;
  endTime: Date;
  scheduledTime: Date;

  driverName: string;
  rideOwnerName: string;

  status: RideStatus;
  path: string;
  cancellationReason: string;
  price: number;
  totalCost: number;

  additionalServices: string[];
  addresses: string[];
  passengersMails: string[];
}
