import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import { EditFavouritesResponse, FavouriteRoutesDTO } from '@shared/models/favourite-route.model';


@Injectable({
  providedIn: 'root',
})
export class FavouriteRoutesService {
	private http = inject(HttpClient);

	public getFavouriteRoutes() {
		return this.http.get<FavouriteRoutesDTO>(`/api/${environment.apiVersion}/routes/favourites`);
	}

	public addToFavourites(routeId: number) {
		return this.http.post<EditFavouritesResponse>(
			`/api/${environment.apiVersion}/routes/${routeId}/favourites`,
			{},
		);
	}

	public removeFromFavourites(routeId: number) {
		return this.http.delete<EditFavouritesResponse>(
			`/api/${environment.apiVersion}/routes/${routeId}/favourites`,
		);
	}
}
