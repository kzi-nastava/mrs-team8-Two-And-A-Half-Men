import { Injectable } from '@angular/core';
import * as L from 'leaflet';
import { MapService } from './map.service';

export interface LocationPin {
	id: string;
	lat: number;
	lng: number;
	marker: L.Marker;
}

@Injectable({
	providedIn: 'root',
})
export class LocationPinService {
	private pins = new Map<string, LocationPin>();
	private clickEnabled = false;
	private rightClickEnabled = false;

	private defaultIcon = L.icon({
		iconUrl: 'https://unpkg.com/leaflet@1.6.0/dist/images/marker-icon.png',
		iconSize: [25, 41],
		iconAnchor: [12, 41],
	});

	constructor(private mapService: MapService) {}

	initialize(): void {
		if (!this.mapService.isInitialized()) {
			console.warn('Map not initialized. Cannot initialize LocationPinService.');
			return;
		}
		console.log('LocationPinService initialized');
	}

	enableClickToAdd(): void {
		if (this.clickEnabled) return;

		const map = this.mapService.getMap();
		map.on('click', this.onMapClick.bind(this));
		this.clickEnabled = true;
	}

	disableClickToAdd(): void {
		if (!this.clickEnabled) return;

		const map = this.mapService.getMap();
		map.off('click', this.onMapClick.bind(this));
		this.clickEnabled = false;
	}

	enableRightClickToRemoveLast(): void {
		if (this.rightClickEnabled) return;

		const map = this.mapService.getMap();
		map.on('contextmenu', this.onMapRightClick.bind(this));
		this.rightClickEnabled = true;
	}

	disableRightClickToRemoveLast(): void {
		if (!this.rightClickEnabled) return;

		const map = this.mapService.getMap();
		map.off('contextmenu', this.onMapRightClick.bind(this));
		this.rightClickEnabled = false;
	}

	private onMapClick(e: L.LeafletMouseEvent): void {
		const { lat, lng } = e.latlng;
		this.addPin(lat, lng);
	}

	private onMapRightClick(e: L.LeafletMouseEvent): void {
		e.originalEvent.preventDefault();
		this.removeLastPin();
	}

	addPin(lat: number, lng: number, options?: { icon?: L.Icon; popupContent?: string }): string {
		const map = this.mapService.getMap();
		const id = this.generateId();

		const marker = L.marker([lat, lng], {
			icon: options?.icon || this.defaultIcon,
		}).addTo(map);

		if (options?.popupContent) {
			marker.bindPopup(options.popupContent);
		}

		// Allow marker click to remove (only if click to add is NOT enabled - to avoid conflicts)
		if (!this.clickEnabled) {
			marker.on('click', () => {
				this.removePin(id);
			});
		}

		const pin: LocationPin = { id, lat, lng, marker };
		this.pins.set(id, pin);

		console.log(`Pin added at [${lat}, ${lng}] with id: ${id}`);
		return id;
	}

	removePin(id: string): void {
		const pin = this.pins.get(id);
		if (!pin) return;

		const map = this.mapService.getMap();
		map.removeLayer(pin.marker);
		this.pins.delete(id);

		console.log(`Pin removed: ${id}`);
	}

	removeLastPin(): void {
		const pinsArray = Array.from(this.pins.values());
		if (pinsArray.length === 0) return;

		const lastPin = pinsArray[pinsArray.length - 1];
		this.removePin(lastPin.id);
	}

	removePinByCoordinates(lat: number, lng: number): void {
		const pin = Array.from(this.pins.values()).find((p) => p.lat === lat && p.lng === lng);

		if (pin) {
			this.removePin(pin.id);
		}
	}

	clearAllPins(): void {
		const map = this.mapService.getMap();

		this.pins.forEach((pin) => {
			map.removeLayer(pin.marker);
		});

		this.pins.clear();
		console.log('All pins cleared');
	}

	getAllPins(): LocationPin[] {
		return Array.from(this.pins.values());
	}

	getPinById(id: string): LocationPin | undefined {
		return this.pins.get(id);
	}

	getPinsCount(): number {
		return this.pins.size;
	}

	private generateId(): string {
		return `pin_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
	}

	cleanup(): void {
		this.disableClickToAdd();
		this.disableRightClickToRemoveLast();
		this.clearAllPins();
	}
}
