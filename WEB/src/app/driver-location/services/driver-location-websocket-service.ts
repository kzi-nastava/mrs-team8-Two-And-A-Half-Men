import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { DriverLocation } from '../models/driver-location'

@Injectable({
  providedIn: 'root',
})
export class DriverLocationWebsocketService {

  private driverLocationsSubject = new BehaviorSubject<Map<number, DriverLocation>>(new Map());
  public driverLocations$: Observable<Map<number, DriverLocation>> = this.driverLocationsSubject.asObservable();


  public updateDriverLocation(location: DriverLocation) {
    const currentLocations = this.driverLocationsSubject.value;
    
    if (location.latitude === null || location.longitude === null) {
      currentLocations.delete(location.driverId);
    } else {
      currentLocations.set(location.driverId, location);
    }
    
    this.driverLocationsSubject.next(new Map(currentLocations));
  }
}
