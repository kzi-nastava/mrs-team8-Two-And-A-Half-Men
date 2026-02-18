import { Injectable } from '@angular/core';
import * as L from 'leaflet';
import { MapService } from './map.service';
import { DriverLocation } from '@shared/models/driver-location';

@Injectable({
	providedIn: 'root',
})
export class DriverMarkerService {
	private markers = new Map<number, L.Marker>();

	private availableIcon = L.icon({
		iconUrl: 'assets/icons/available.png',
		iconSize: [40, 40],
		iconAnchor: [20, 40],
	});

	private occupiedIcon = L.icon({
		iconUrl: 'assets/icons/occupied.png',
		iconSize: [40, 40],
		iconAnchor: [20, 40],
	});

	constructor(private mapService: MapService) {}

	initialize(): void {
		if (!this.mapService.isInitialized()) {
			console.warn('Map not initialized. Cannot initialize DriverMarkerService.');
			return;
		}
		console.log('DriverMarkerService initialized');
	}

	addOrUpdateMarker(location: DriverLocation): void {
		if (!this.mapService.isInitialized()) {
			console.warn('Map not initialized');
			return;
		}

		const map = this.mapService.getMap();
		const position: L.LatLngExpression = [location.latitude, location.longitude];

		if (this.markers.has(location.driverId)) {
			// Update existing marker
			const marker = this.markers.get(location.driverId)!;
			marker.setLatLng(position);
			marker.setIcon(location.isOccupied ? this.occupiedIcon : this.availableIcon);

			// Update popup content
			marker.setPopupContent(this.createPopupContent(location));
		} else {
			// Create new marker
			const marker = L.marker(position, {
				icon: location.isOccupied ? this.occupiedIcon : this.availableIcon,
			}).addTo(map);

			marker.bindPopup(this.createPopupContent(location));

			this.markers.set(location.driverId, marker);
			console.log(`Driver marker added: ${location.driverId}`);
		}
	}

	removeMarker(driverId: number): void {
		const marker = this.markers.get(driverId);
		if (!marker) return;

		const map = this.mapService.getMap();
		map.removeLayer(marker);
		this.markers.delete(driverId);

		console.log(`Driver marker removed: ${driverId}`);
	}

	getAllMarkers(): Map<number, L.Marker> {
		return new Map(this.markers);
	}

	clearAllMarkers(): void {
		if (!this.mapService.isInitialized()) return;

		const map = this.mapService.getMap();

		this.markers.forEach((marker) => map.removeLayer(marker));
		this.markers.clear();

		console.log('All driver markers cleared');
	}

	highlightMarker(driverId: number): void {
		const marker = this.markers.get(driverId);
		if (!marker) return;

		marker.openPopup();
	}

	private createPopupContent(location: DriverLocation): string {
		return `
      <div style="min-width: 150px;">
        <b>${location.driverName || 'Driver: ' + location.driverId}</b><br>
        Status: <span style="color: ${location.isOccupied ? '#e74c3c' : '#27ae60'}">
          ${location.isOccupied ? 'Occupied' : 'Free'}
        </span><br>
        ${location.vehicleTypeName ? 'Vozilo: ' + location.vehicleTypeName + '<br>' : ''}
        <small>ID: ${location.driverId}</small>
      </div>
    `;
	}

	cleanup(): void {
		this.clearAllMarkers();
	}
}
