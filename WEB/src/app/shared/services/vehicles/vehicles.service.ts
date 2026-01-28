import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { VehicleOptions } from '@shared/models/vehicles.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
	providedIn: 'root',
})
export class VehiclesService {
	private http = inject(HttpClient);

	getVehicleOptions(): Observable<VehicleOptions> {
		return this.http.get<VehicleOptions>(`/api/${environment.apiVersion}/vehicles/options`);
	}
}
