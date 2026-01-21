import { Injectable } from '@angular/core';
import * as L from 'leaflet';
import { DriverLocation } from '../models/driver-location';
import { MapService } from './../../map/services/map-service';

@Injectable({
  providedIn: 'root',
})
export class DriverMarkerManagerService {
  private markers = new Map<number, L.Marker>();

  constructor(private mapService: MapService) {}

  addOrUpdateMarker(location: DriverLocation): void {
    const map = this.mapService.getMap();
    if (!map) {
      console.warn('Map not initialized');
      return;
    }

    const position: L.LatLngExpression = [
      location.latitude,
      location.longitude,
    ];

    if (this.markers.has(location.driverId)) {
      // Update existing marker
      const marker = this.markers.get(location.driverId)!;
      marker.setLatLng(position);
      marker.setIcon(
        location.isOccupied
          ? this.mapService.getOccupiedIcon()
          : this.mapService.getAvailableIcon()
      );
    } else {
      // Create new marker
      const marker = L.marker(position, {
        icon: location.isOccupied
          ? this.mapService.getOccupiedIcon()
          : this.mapService.getAvailableIcon(),
      }).addTo(map);

      // Add popup with driver info
      marker.bindPopup(`
        <b>${location.driverName || 'Driver ' + location.driverId}</b><br>
        Status: ${location.isOccupied ? 'Zauzet' : 'Slobodan'}<br>
        ${location.vehicleTypeName ? 'Vozilo: ' + location.vehicleTypeName : ''}
      `);

      this.markers.set(location.driverId, marker);
    }
  }

  removeMarker(driverId: number): void {
    const marker = this.markers.get(driverId);
    if (!marker) return;

    const map = this.mapService.getMap();
    if (map) {
      map.removeLayer(marker);
    }
    this.markers.delete(driverId);
  }

  clearAllMarkers(): void {
    const map = this.mapService.getMap();
    if (!map) return;

    this.markers.forEach(marker => map.removeLayer(marker));
    this.markers.clear();
  }
}