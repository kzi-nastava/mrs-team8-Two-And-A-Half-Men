export interface StopPoint {
	address: string;
	latitude: number;
	longitude: number;
}

export interface FavouriteRoute {
	id: number;
	points: StopPoint[];
}

export interface FavouriteRoutesDTO {
	routes: FavouriteRoute[];
}

export interface EditFavouritesResponse {
	status: string;
	customerId: number;
	routeId: number;
	ok: boolean;
}
