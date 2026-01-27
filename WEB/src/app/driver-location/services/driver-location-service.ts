import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DriverLocation } from '../models/driver-location';

@Injectable({
  providedIn: 'root',
})
export class DriverLocationService {

  constructor(private http: HttpClient) {}

  getAllDriverLocations(): Observable<DriverLocation[]> {
    return this.http.get<DriverLocation[]>(`/api/v1/drivers/locations`);
  }

  getDriverLocation(driverId: number): Observable<DriverLocation> {
    return this.http.get<DriverLocation>(
      `/api/v1/drivers/locations/${driverId}`
    );
  }

  getNearbyDrivers(
    longitude: number,
    latitude: number,
    radiusKm: number = 5
  ): Observable<DriverLocation[]> {
    return this.http.get<DriverLocation[]>(
      `/api/v1/drivers/locations/nearby`,
      {
        params: {
          longitude: longitude.toString(),
          latitude: latitude.toString(),
          radiusKm: radiusKm.toString(),
        },
      }
    );
  }
}