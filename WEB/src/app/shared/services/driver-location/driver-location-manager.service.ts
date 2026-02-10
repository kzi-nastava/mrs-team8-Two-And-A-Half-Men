import { Injectable, OnDestroy, effect } from '@angular/core';
import { DriverLocation } from '../../models/driver-location';
import { DriverLocationService } from './driver-location.service';
import { DriverLocationWebsocketService } from './driver-location-websocket.service';
import { DriverMarkerService } from '@shared/components/map/services/driver-marker.service';
import { WebSocketService } from '@core/services/web-socket.service';

@Injectable({
  providedIn: 'root',
})
export class DriverLocationManagerService implements OnDestroy {
  private isInitialized = false;

  constructor(
    private driverLocationService: DriverLocationService,
    private driverLocationWebSocket: DriverLocationWebsocketService,
    private driverMarkerService: DriverMarkerService,
    private webSocket: WebSocketService // koristi postojeći WS
  ) {
    // update map markers whenever driverLocations Signal changes
    effect(() => {
      const locations = this.driverLocationWebSocket.driverLocations();
      this.updateMarkers(locations);
    });
  }

  initialize(): void {
    if (this.isInitialized) {
      console.warn('DriverLocationManager already initialized');
      return;
    }

    console.log('Initializing DriverLocationManager...');

    this.loadInitialLocations();
    // this.webSocket.connect(); // start STOMP connection


    this.isInitialized = true;
  }

  private loadInitialLocations(): void {
    this.driverLocationService.getAllDriverLocations().subscribe({
      next: (locations: DriverLocation[]) => {
        locations.forEach((loc) =>
          this.driverLocationWebSocket.updateDriverLocation(loc)
        );
      },
      error: (error) => console.error('Error loading initial driver locations:', error),
    });
  }

  private updateMarkers(locationsMap: Map<number, DriverLocation>): void {
    locationsMap.forEach((location) => {
      if (location.latitude != null && location.longitude != null) {
        this.driverMarkerService.addOrUpdateMarker(location);
      }
    });

    // remove markers for drivers no longer present
    const currentMarkers = this.driverMarkerService.getAllMarkers();
    currentMarkers.forEach((_, driverId) => {
      if (!locationsMap.has(driverId)) {
        this.driverMarkerService.removeMarker(driverId);
      }
    });
  }

  highlightDriver(driverId: number): void {
    this.driverMarkerService.highlightMarker(driverId);
  }

  getAllDriverLocations(): DriverLocation[] {
    return Array.from(this.driverLocationWebSocket.driverLocations().values());
  }

  // isWebSocketConnected(): boolean {
  //   return this.driverLocationWebSocket.connected();
  // }

  // reconnectWebSocket(): void {
  //   this.driverLocationWebSocket.clearAll();
  //   this.webSocket.disconnect();
  //   this.webSocket.connect();
  // }

  cleanup(): void {
    console.log('Cleaning up DriverLocationManager...');
    this.driverLocationWebSocket.clearAll();
    // this.webSocket.disconnect();
    this.driverMarkerService.clearAllMarkers();
    this.isInitialized = false;
  }

  ngOnDestroy(): void {
    this.cleanup();
  }
}
