import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { DriverLocation } from '../models/driver-location';
import { DriverLocationService } from './driver-location-service';
import { DriverLocationWebsocketService } from './driver-location-websocket-service';
import { DriverMarkerManagerService } from './driver-marker-manager-service';

@Injectable({
  providedIn: 'root'
})
export class DriverLocationManagerService {
  private locationsSubject = new BehaviorSubject<Map<number, DriverLocation>>(new Map());
  public locations$: Observable<Map<number, DriverLocation>> = this.locationsSubject.asObservable();

  constructor(
    private driverLocationService: DriverLocationService,
    private driverLocationWebSocket: DriverLocationWebsocketService,
    private markerManager: DriverMarkerManagerService
  ) {}

  initialize(): void {
    this.loadInitialLocations();
    this.subscribeToUpdates();
  }

  loadInitialLocations(): void {
    this.driverLocationService.getAllDriverLocations().subscribe({
      next: (locations: DriverLocation[]) => {
        console.log('Initial locations loaded:', locations.length);
        const locationsMap = new Map<number, DriverLocation>();
        locations.forEach(location => {
          if (location.latitude && location.longitude) {
            locationsMap.set(location.driverId, location);
            this.markerManager.addOrUpdateMarker(location);
          }
        });
        this.locationsSubject.next(locationsMap);
      },
      error: (error) => {
        console.error('Error loading driver locations:', error);
      }
    });
  }

  subscribeToUpdates(): void {
    this.driverLocationWebSocket.driverLocations$.subscribe({
      next: (locationsMap: Map<number, DriverLocation>) => {
        console.log('WebSocket update received, drivers:', locationsMap.size);
        
        // Update or add markers for all received locations
        locationsMap.forEach((location, driverId) => {
          if (location.latitude && location.longitude) {
            this.markerManager.addOrUpdateMarker(location);
          }
        });

        // Remove markers for drivers that are no longer available
        const currentLocations = this.locationsSubject.value;
        currentLocations.forEach((_, driverId) => {
          if (!locationsMap.has(driverId)) {
            this.markerManager.removeMarker(driverId);
          }
        });

        this.locationsSubject.next(new Map(locationsMap));
      },
      error: (error) => {
        console.error('WebSocket subscription error:', error);
      }
    });
  }

  getLocation(driverId: number): DriverLocation | undefined {
    return this.locationsSubject.value.get(driverId);
  }

  getAllLocations(): DriverLocation[] {
    return Array.from(this.locationsSubject.value.values());
  }

  cleanup(): void {
    this.markerManager.clearAllMarkers();
    this.locationsSubject.next(new Map());
  }
}