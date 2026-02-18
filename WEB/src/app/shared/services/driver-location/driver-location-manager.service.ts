import { Injectable, OnDestroy, effect, inject } from '@angular/core';
import { DriverLocation } from '../../models/driver-location';
import { DriverLocationService } from './driver-location.service';
import { DriverLocationWebsocketService } from '@shared/services/driver-location/driver-location-websocket.service';
import { DriverMarkerService } from '@shared/components/map/services/driver-marker.service';

@Injectable({
	providedIn: 'root',
})
export class DriverLocationManagerService implements OnDestroy {
	private allDriversInitialized = false;
	private trackedDriverId?: number;
	private trackingUnsubscribe?: () => void;

	private driverLocationService = inject(DriverLocationService);
	private driverLocationWebsocketService = inject(DriverLocationWebsocketService);
	private driverMarkerService = inject(DriverMarkerService);

	constructor() {
		// React to WebSocket location updates for "all drivers" mode
		effect(() => {
			const locations = this.driverLocationWebsocketService.driverLocations();

			if (this.allDriversInitialized) {
				this.syncAllMarkers(locations);
			} else if (this.trackedDriverId != null) {
				this.syncSingleMarker(locations, this.trackedDriverId);
			}
		});
	}

	/**
	 * Initialize display of ALL active drivers (enableActiveDriverMarkers).
	 * Loads initial locations via HTTP then keeps in sync via WebSocket.
	 */
	initializeAllDrivers(): void {
		if (this.allDriversInitialized) {
			console.warn('DriverLocationManager: all-drivers mode already initialized');
			return;
		}

		console.log('DriverLocationManager: initializing all-drivers mode');
		this.loadInitialLocations();
		this.allDriversInitialized = true;
	}

	/**
	 * Clean up all-drivers mode markers without touching the WebSocket subscription
	 * (WebSocket is managed by DriverLocationWebsocketService itself).
	 */
	cleanupAllDrivers(): void {
		console.log('DriverLocationManager: cleaning up all-drivers mode');
		this.driverMarkerService.clearAllMarkers();
		this.allDriversInitialized = false;
	}

	/**
	 * Initialize tracking of a SINGLE driver (enableDriverTracking).
	 * Subscribes to that driver's WebSocket topic and shows only their marker.
	 */
	async initializeDriverTracking(driverId: number): Promise<void> {
		if (this.trackedDriverId === driverId) {
			console.warn(`DriverLocationManager: already tracking driver ${driverId}`);
			return;
		}

		// Clean up any previous single-driver tracking
		this.cleanupDriverTracking();

		console.log(`DriverLocationManager: initializing tracking for driver ${driverId}`);
		this.trackedDriverId = driverId;

		// Load last known position via HTTP
		this.driverLocationService.getDriverLocation(driverId).subscribe({
			next: (location) => this.driverLocationWebsocketService.updateDriverLocation(location),
			error: (err) =>
				console.error(`DriverLocationManager: error loading driver ${driverId}:`, err),
		});

		// Subscribe to live updates for this specific driver
		try {
			this.trackingUnsubscribe =
				await this.driverLocationWebsocketService.subscribeToSpecificDriver(driverId);
		} catch (err) {
			console.error(`DriverLocationManager: error subscribing to driver ${driverId}:`, err);
		}
	}

	/**
	 * Stop tracking a single driver and remove their marker.
	 */
	cleanupDriverTracking(): void {
		if (this.trackedDriverId == null) return;

		console.log(
			`DriverLocationManager: cleaning up tracking for driver ${this.trackedDriverId}`,
		);

		if (this.trackingUnsubscribe) {
			this.trackingUnsubscribe();
			this.trackingUnsubscribe = undefined;
		}

		this.driverMarkerService.removeMarker(this.trackedDriverId);
		this.driverLocationWebsocketService.unsubscribeFromSpecificDriver(this.trackedDriverId);
		this.trackedDriverId = undefined;
	}

	// ===== Private helpers =====

	private loadInitialLocations(): void {
		this.driverLocationService.getAllDriverLocations().subscribe({
			next: (locations: DriverLocation[]) => {
				locations.forEach((loc) =>
					this.driverLocationWebsocketService.updateDriverLocation(loc),
				);
			},
			error: (error) =>
				console.error('DriverLocationManager: error loading initial locations:', error),
		});
	}

	/** Update markers for all drivers, removing stale ones. */
	private syncAllMarkers(locationsMap: Map<number, DriverLocation>): void {
		locationsMap.forEach((location) => {
			if (location.latitude != null && location.longitude != null) {
				this.driverMarkerService.addOrUpdateMarker(location);
			}
		});

		// Remove markers for drivers that are no longer in the map
		const currentMarkers = this.driverMarkerService.getAllMarkers();
		currentMarkers.forEach((_, driverId) => {
			if (!locationsMap.has(driverId)) {
				this.driverMarkerService.removeMarker(driverId);
			}
		});
	}

	/** Update marker for only the tracked driver. */
	private syncSingleMarker(locationsMap: Map<number, DriverLocation>, driverId: number): void {
		const location = locationsMap.get(driverId);
		if (location && location.latitude != null && location.longitude != null) {
			this.driverMarkerService.addOrUpdateMarker(location);
		} else {
			// Driver went offline — remove their marker
			this.driverMarkerService.removeMarker(driverId);
		}
	}

	ngOnDestroy(): void {
		this.cleanupAllDrivers();
		this.cleanupDriverTracking();
	}
}
