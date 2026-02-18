export interface RouteItem {
	address: string;
	latitude: number;
	longitude: number;
}

export interface BookRideRequest {
	route?: RouteItem[];
	routeId?: number;
	scheduledTime?: string | null;
	passengers?: string[];
	vehicleTypeId?: number | null;
	additionalServicesIds?: number[];
}

export interface BookRideResponse {
	id: number,
	status: string,
	estimatedDistance: number,
}
