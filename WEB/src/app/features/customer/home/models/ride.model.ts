export interface RouteItem {
	address: string;
	latitude: number;
	longitude: number;
}

export interface BookRideRequest {
	route?: RouteItem[]
}
