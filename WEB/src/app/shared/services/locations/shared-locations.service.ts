import { Injectable, Signal, signal } from '@angular/core';
import * as L from 'leaflet';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { NominatimService } from './nominatim-service';

@Injectable({
	providedIn: 'root',
})
export class SharedLocationsService {
	constructor(
		private http: HttpClient,
		private nominatimService: NominatimService,
	) {}

	public locations = signal<NominatimResult[]>([]);

	setLocation(location: NominatimResult) {
		this.locations.update((list) => [...list, location]);
	}

	addLocationFromCoordinates(lat: number, lon: number): Observable<NominatimResult> {
		return this.nominatimService.reverse(lat, lon).pipe(
			map((res) => {
				const nominatimResult: NominatimResult = {
					place_id: res.place_id,
					display_name: res.display_name,
					lat: res.lat,
					lon: res.lon,
				};

				this.locations.update((list) => {
					return [...list, nominatimResult];
				});

				console.log('Location added from coordinates:', this.locations());
				return nominatimResult;
			}),
		);
	}
	removeLastLocation(): void {
		this.locations.update((list) => list.slice(0, -1));
		console.log('Last location removed:', this.locations());
	}
	clearLocations(): void {
		this.locations.set([]);
		console.log('All locations cleared');
	}
	getStartLocation(): NominatimResult | null {
		const locs = this.locations();
		return locs.length > 0 ? locs[0] : null;
	}

	getEndLocation(): NominatimResult | null {
		const locs = this.locations();
		return locs.length > 1 ? locs[1] : null;
	}
}
