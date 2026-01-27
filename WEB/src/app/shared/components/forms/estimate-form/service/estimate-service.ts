import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable, tap } from 'rxjs';
import { environment } from '@environments/environment';

@Injectable({
  providedIn: 'root',
})
export class EstimateService {
  constructor(private http: HttpClient) {}

  estimateTime(start: NominatimResult, end: NominatimResult): Observable<number> {
    const bookingParams = {
  routeId: null,
  route: [
    { address: start.display_name, latitude: start.lat, longitude: start.lon },
    { address: end.display_name, latitude: end.lat, longitude: end.lon }
  ]
    };
    console.log('Booking Params:', bookingParams);

    return this.http.post<any>("/api/v1/rides/estimates", bookingParams)
      .pipe(
        map(response => response.time) // this is in minutes
      );
  }


}
