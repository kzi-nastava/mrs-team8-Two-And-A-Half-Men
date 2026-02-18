import {
	Input,
	OnChanges,
	SimpleChanges,
	Component,
	AfterViewInit,
	OnDestroy,
	effect,
	inject,
} from '@angular/core';
import { MapService } from './services/map.service';
import { LocationPinService } from './services/location-pin.service';
import { DriverMarkerService } from './services/driver-marker.service';
import { RouteService } from './services/route.service';
import { DriverLocationManagerService } from '@shared/services/driver-location/driver-location-manager.service';
import { LocationsService } from '@shared/services/locations/locations.service';
import { DEFAULT_MAP_CONFIG, MapConfig } from '@shared/components/map/map.config';
import { PopupsService } from '@shared/services/popups/popups.service';
import { NominatimResult } from '@shared/models/nominatim-results.model';

const MAP_CENTER: { center: [number, number]; zoom: number } = {
	center: [45.2396, 19.8227],
	zoom: 13,
};

@Component({
	selector: 'app-map',
	templateUrl: './map.component.html',
	styleUrls: ['./map.component.css'],
})
export class MapComponent implements AfterViewInit, OnDestroy, OnChanges {
	@Input() path?: string;
	@Input() config: MapConfig = DEFAULT_MAP_CONFIG;
	@Input() rideLocations?: NominatimResult[];
	@Input() trackingDriverId?: number;

	private currentRouteId?: string;
	private currentPathId?: string;
	private mapInitialized = false;

	private mapService = inject(MapService);
	private locationPinService = inject(LocationPinService);
	private routeService = inject(RouteService);
	private driverLocationManagerService = inject(DriverLocationManagerService);
	private locationsService = inject(LocationsService);
	private popupsService = inject(PopupsService);

	constructor() {
		effect(() => {
			const locations = this.locationsService.locations();

			// Wait until the map is ready (effect runs once on construction too)
			if (!this.mapInitialized || this.rideLocations) return;

			// ── Pins ────────────────────────────────────────────────────────
			if (this.config.enableLocationPins) {
				this.locationPinService.clearAllPins();
				locations.forEach((loc) => {
					this.locationPinService.addPin(
						parseFloat(loc.lat),
						parseFloat(loc.lon),
						{ popupContent: loc.display_name },
					);
				});
			}

			// ── Route ───────────────────────────────────────────────────────
			if (this.config.enableRouting) {
				if (locations.length >= 2) {
					this.updateRouteFromLocations(locations);
				} else {
					this.clearCurrentRoute();
				}
			}
		});
	}

	ngAfterViewInit(): void {
		this.config = { ...DEFAULT_MAP_CONFIG, ...this.config };

		this.mapService.initMap('map', MAP_CENTER.center, MAP_CENTER.zoom);
		this.mapInitialized = true;

		this.initializeEnabledServices();

		if (this.rideLocations && this.rideLocations.length > 0) {
			this.setupRideLocations(this.rideLocations);
		}

		if (this.path && this.config.enablePath) {
			this.drawPath();
		}
		console.log(this.config)
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (changes['config'] && !changes['config'].firstChange) {
			this.handleConfigChange(changes['config'].previousValue, this.config);
		}

		if (changes['rideLocations'] && this.mapInitialized) {
			const newLocations = changes['rideLocations'].currentValue;
			if (newLocations && newLocations.length > 0) {
				this.setupRideLocations(newLocations);
			} else {
				// rideLocations cleared — remove pins and route
				this.locationPinService.clearAllPins();
				this.clearCurrentRoute();
			}
		}

		if (changes['path'] && this.mapInitialized && this.config.enablePath) {
			this.drawPath();
		}

		if (changes['trackingDriverId'] && this.mapInitialized && this.config.enableDriverTracking) {
			this.handleTrackingDriverChange(
				changes['trackingDriverId'].previousValue,
				changes['trackingDriverId'].currentValue,
			);
		}
	}

	// ===== Private setup =====

	private initializeEnabledServices(): void {
		if (this.config.enableLocationPins) {
			this.locationPinService.initialize();
		}

		if (this.config.enableActiveDriverMarkers) {
			// Show all active drivers from WebSocket
			this.driverLocationManagerService.initializeAllDrivers();
		}

		if (this.config.enableDriverTracking && this.trackingDriverId != null) {
			// Track only the specific driver for this ride
			this.driverLocationManagerService.initializeDriverTracking(this.trackingDriverId).then();
		}

		if (this.config.enableRouting) {
			this.routeService.initialize();
		}

		if (this.config.enableClickToAddLocation) {
			this.setupMapClickHandler();
		}

		if (this.config.enableRightClickToRemove) {
			this.setupMapRightClickHandler();
		}
	}

	private setupRideLocations(locations: NominatimResult[]): void {
		this.locationPinService.clearAllPins();
		this.clearCurrentRoute();

		if (this.config.enableLocationPins) {
			locations.forEach((location) => {
				this.locationPinService.addPin(parseFloat(location.lat), parseFloat(location.lon), {
					popupContent: location.display_name,
				});
			});
		}

		if (this.config.enableRouting && locations.length >= 2) {
			this.updateRouteFromLocations(locations);
		}
	}

	private handleConfigChange(oldConfig: MapConfig, newConfig: MapConfig): void {
		if (!this.mapInitialized) return;

		// Location pins
		if (oldConfig.enableLocationPins !== newConfig.enableLocationPins) {
			if (newConfig.enableLocationPins) {
				this.locationPinService.initialize();
				if (this.rideLocations) {
					this.rideLocations.forEach((loc) =>
						this.locationPinService.addPin(parseFloat(loc.lat), parseFloat(loc.lon), {
							popupContent: loc.display_name,
						}),
					);
				}
			} else {
				this.locationPinService.cleanup();
			}
		}

		// Click to add
		if (oldConfig.enableClickToAddLocation !== newConfig.enableClickToAddLocation) {
			if (newConfig.enableClickToAddLocation) {
				this.setupMapClickHandler();
			} else {
				this.removeMapClickHandler();
			}
		}

		// Right click to remove
		if (oldConfig.enableRightClickToRemove !== newConfig.enableRightClickToRemove) {
			if (newConfig.enableRightClickToRemove) {
				this.setupMapRightClickHandler();
			} else {
				this.removeMapRightClickHandler();
			}
		}

		// All active driver markers
		if (oldConfig.enableActiveDriverMarkers !== newConfig.enableActiveDriverMarkers) {
			if (newConfig.enableActiveDriverMarkers) {
				this.driverLocationManagerService.initializeAllDrivers();
			} else {
				this.driverLocationManagerService.cleanupAllDrivers();
			}
		}

		// Single driver tracking
		if (oldConfig.enableDriverTracking !== newConfig.enableDriverTracking) {
			if (newConfig.enableDriverTracking && this.trackingDriverId != null) {
				this.driverLocationManagerService.initializeDriverTracking(this.trackingDriverId);
			} else {
				this.driverLocationManagerService.cleanupDriverTracking();
			}
		}

		// Routing
		if (oldConfig.enableRouting !== newConfig.enableRouting) {
			if (newConfig.enableRouting) {
				this.routeService.initialize();
				if (this.rideLocations && this.rideLocations.length >= 2) {
					this.updateRouteFromLocations(this.rideLocations);
				}
			} else {
				this.clearCurrentRoute();
				this.routeService.cleanup();
			}
		}

		// Path (polyline from geohash)
		if (oldConfig.enablePath !== newConfig.enablePath) {
			if (newConfig.enablePath && this.path) {
				this.drawPath();
			} else {
				this.clearPath();
			}
		}
	}

	private handleTrackingDriverChange(oldDriverId?: number, newDriverId?: number): void {
		if (oldDriverId != null) {
			this.driverLocationManagerService.cleanupDriverTracking();
		}
		if (newDriverId != null) {
			this.driverLocationManagerService.initializeDriverTracking(newDriverId);
		}
	}

	// ===== Click handlers =====

	private setupMapClickHandler(): void {
		const map = this.mapService.getMap();
		map.on('click', (e: L.LeafletMouseEvent) => {
			const { lat, lng } = e.latlng;
			// Only update the locations signal — the constructor effect will
			// redraw all pins and the route automatically.
			this.locationsService.addLocationFromCoordinates(lat, lng).subscribe({
				error: (error) => {
					console.error('Error getting location:', error);
					this.popupsService.error('Error getting location:', error);
				},
			});
		});
	}

	private removeMapClickHandler(): void {
		this.mapService.getMap().off('click');
	}

	private setupMapRightClickHandler(): void {
		const map = this.mapService.getMap();
		map.on('contextmenu', (e: L.LeafletMouseEvent) => {
			e.originalEvent.preventDefault();
			this.locationsService.removeLastLocation();
			this.locationPinService.removeLastPin();
		});
	}

	private removeMapRightClickHandler(): void {
		this.mapService.getMap().off('contextmenu');
	}

	// ===== Route helpers =====

	private updateRouteFromLocations(locations: any[]): void {
		this.clearCurrentRoute();

		const waypoints: [number, number][] = locations.map((loc) => [
			parseFloat(loc.lat),
			parseFloat(loc.lon),
		]);

		this.currentRouteId = this.routeService.createRoute(waypoints, {
			color: '#3388ff',
			weight: 6,
			opacity: 0.7,
		});
	}

	private clearCurrentRoute(): void {
		if (this.currentRouteId) {
			this.routeService.removeRoute(this.currentRouteId);
			this.currentRouteId = undefined;
		}
	}

	private clearPath(): void {
		if (this.currentPathId) {
			this.routeService.removeRoute(this.currentPathId);
			this.currentPathId = undefined;
		}
	}

	private drawPath(): void {
		this.clearPath();
		if (!this.path) return;

		this.currentPathId = this.routeService.drawLineFromGeohash(this.path, 12, {
			color: '#e74c3c',
			weight: 4,
			opacity: 0.8,
		});

		this.routeService.fitRouteInView(this.currentPathId);
	}

	// ===== Lifecycle =====

	ngOnDestroy(): void {
		if (this.config.enableLocationPins) {
			this.locationPinService.cleanup();
		}

		if (this.config.enableActiveDriverMarkers) {
			this.driverLocationManagerService.cleanupAllDrivers();
		}

		if (this.config.enableDriverTracking) {
			this.driverLocationManagerService.cleanupDriverTracking();
		}

		if (this.config.enableRouting) {
			this.routeService.cleanup();
		}

		this.locationsService.clearLocations();
		this.mapService.destroyMap();
		this.mapInitialized = false;
	}
}
