import { Injectable, signal, computed } from '@angular/core';
import { DriverLocation } from '../../models/driver-location';

@Injectable({
  providedIn: 'root',
})
export class DriverLocationWebsocketService {

  private _driverLocations = signal<Map<number, DriverLocation>>(new Map());
  private _connected = signal<boolean>(false);

  readonly driverLocations = this._driverLocations.asReadonly();
  readonly connected = this._connected.asReadonly();

  readonly driversArray = computed(() =>
    Array.from(this._driverLocations().values())
  );

  readonly activeDriversCount = computed(
    () => this._driverLocations().size
  );

  constructor() {}

  setConnected(value: boolean): void {
    this._connected.set(value);
  }

  updateDriverLocation(location: DriverLocation): void {
    this._driverLocations.update((map) => {
      const newMap = new Map(map);

      // offline / remove driver
      if (location.latitude == null || location.longitude == null) {
        newMap.delete(location.driverId);
      } else {
        newMap.set(location.driverId, location);
      }

      return newMap;
    });
  }

  clearAll(): void {
    this._driverLocations.set(new Map());
  }
}
