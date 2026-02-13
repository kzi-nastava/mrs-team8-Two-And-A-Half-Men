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
import { SharedLocationsService } from '@shared/services/locations/shared-locations.service';
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

	private currentRouteId?: string;
	private currentPathId?: string;
	private mapInitialized = false;

	private mapService = inject(MapService);
	private locationPinService = inject(LocationPinService);
	private driverMarkerService = inject(DriverMarkerService);
	private routeService = inject(RouteService);
	private driverLocationManagerService = inject(DriverLocationManagerService);
	private sharedLocationsService = inject(SharedLocationsService);
	private popupsService = inject(PopupsService);

	constructor() {
		effect(() => {
			const locations = this.sharedLocationsService.locations();

			// Only use shared locations if we don't have rideLocations (booking mode)
			if (
				!this.rideLocations &&
				this.config.enableRouting &&
				locations.length >= 2 &&
				this.mapInitialized
			) {
				this.updateRouteFromLocations(locations);
			} else if (!this.rideLocations) {
				this.clearCurrentRoute();
			}
		});
	}

	ngAfterViewInit(): void {
		console.log('Initializing map...');

		this.config = { ...DEFAULT_MAP_CONFIG, ...this.config };

		this.mapService.initMap('map', MAP_CENTER.center, MAP_CENTER.zoom);

		this.mapInitialized = true;

		this.initializeEnabledServices();

		// If we have rideLocations, set them up
		if (this.rideLocations && this.rideLocations.length > 0) {
			this.setupRideLocations(this.rideLocations);
		}

		if (this.path && this.config.enablePath) {
			this.drawPath();
		}
	}

	ngOnChanges(changes: SimpleChanges): void {
		// Handle config changes
		if (changes['config'] && !changes['config'].firstChange) {
			console.log('Map config changed:', this.config);
			this.handleConfigChange(changes['config'].previousValue, this.config);
		}

		// Handle rideLocations changes
		if (changes['rideLocations'] && this.mapInitialized) {
			const newLocations = changes['rideLocations'].currentValue;
			if (newLocations && newLocations.length > 0) {
				this.setupRideLocations(newLocations);
			}
		}

		// Handle path changes
		if (changes['path'] && this.path && this.mapInitialized && this.config.enablePath) {
			this.drawPath();
		}
	}

	private setupRideLocations(locations: NominatimResult[]): void {
		console.log('Setting up ride locations:', locations);

		// Clear existing pins and routes
		this.locationPinService.clearAllPins();
		this.clearCurrentRoute();

		// Add pins for each location
		if (this.config.enableLocationPins) {
			locations.forEach((location) => {
				this.locationPinService.addPin(parseFloat(location.lat), parseFloat(location.lon), {
					popupContent: location.display_name,
				});
			});
		}

		// Create route between locations
		if (this.config.enableRouting && locations.length >= 2) {
			this.updateRouteFromLocations(locations);
		}
	}

	private handleConfigChange(oldConfig: MapConfig, newConfig: MapConfig): void {
		if (!this.mapInitialized) return;

		// Handle location pins
		if (oldConfig.enableLocationPins !== newConfig.enableLocationPins) {
			if (newConfig.enableLocationPins) {
				this.locationPinService.initialize();
				// Re-add ride locations if they exist
				if (this.rideLocations && this.rideLocations.length > 0) {
					this.rideLocations.forEach((location) => {
						this.locationPinService.addPin(
							parseFloat(location.lat),
							parseFloat(location.lon),
							{
								popupContent: location.display_name,
							},
						);
					});
				}
			} else {
				this.locationPinService.cleanup();
			}
		}

		// Handle driver markers
		if (oldConfig.enableActiveDriverMarkers !== newConfig.enableActiveDriverMarkers) {
			if (newConfig.enableActiveDriverMarkers) {
				this.driverMarkerService.initialize();
			} else {
				this.driverMarkerService.cleanup();
			}
		}

		// Handle routing
		if (oldConfig.enableRouting !== newConfig.enableRouting) {
			if (newConfig.enableRouting) {
				this.routeService.initialize();
				// Re-create route if we have locations
				if (this.rideLocations && this.rideLocations.length >= 2) {
					this.updateRouteFromLocations(this.rideLocations);
				}
			} else {
				this.routeService.cleanup();
				this.clearCurrentRoute();
			}
		}

		// Handle driver tracking
		if (oldConfig.enableDriverTracking !== newConfig.enableDriverTracking) {
			if (newConfig.enableDriverTracking) {
				this.driverLocationManagerService.initialize();
			} else {
				this.driverLocationManagerService.cleanup();
			}
		}

		// Handle click handlers
		if (oldConfig.enableClickToAddLocation !== newConfig.enableClickToAddLocation) {
			if (newConfig.enableClickToAddLocation) {
				this.setupMapClickHandler();
			} else {
				this.removeMapClickHandler();
			}
		}

		if (oldConfig.enableRightClickToRemove !== newConfig.enableRightClickToRemove) {
			if (newConfig.enableRightClickToRemove) {
				this.setupMapRightClickHandler();
			} else {
				this.removeMapRightClickHandler();
			}
		}

		// Handle path display
		if (oldConfig.enablePath !== newConfig.enablePath) {
			if (newConfig.enablePath && this.path) {
				this.drawPath();
			} else {
				this.clearPath();
			}
		}
	}

	private initializeEnabledServices(): void {
		if (this.config.enableLocationPins) {
			this.locationPinService.initialize();
		}

		if (this.config.enableActiveDriverMarkers) {
			this.driverMarkerService.initialize();
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

		if (this.config.enableDriverTracking) {
			this.driverLocationManagerService.initialize();
		}
	}

	private setupMapClickHandler(): void {
		const map = this.mapService.getMap();

		map.on('click', (e: L.LeafletMouseEvent) => {
			const { lat, lng } = e.latlng;

			console.log('Map clicked at:', lat, lng);

			this.sharedLocationsService.addLocationFromCoordinates(lat, lng).subscribe({
				next: (nominatimResult) => {
					console.log('Location added from map click:', nominatimResult.display_name);
				},
				error: (error) => {
					console.error('Error getting location:', error);
					this.popupsService.error('Error getting location:', error);
				},
			});
		});
	}

	private removeMapClickHandler(): void {
		const map = this.mapService.getMap();
		map.off('click');
	}

	private setupMapRightClickHandler(): void {
		const map = this.mapService.getMap();

		map.on('contextmenu', (e: L.LeafletMouseEvent) => {
			e.originalEvent.preventDefault();
			console.log('Right click - removing last location');
			this.sharedLocationsService.removeLastLocation();
		});
	}

	private removeMapRightClickHandler(): void {
		const map = this.mapService.getMap();
		map.off('contextmenu');
	}

	ngOnDestroy(): void {
		console.log('Destroying map component...');

		if (this.config.enableLocationPins) {
			this.locationPinService.cleanup();
		}

		if (this.config.enableActiveDriverMarkers) {
			this.driverMarkerService.cleanup();
		}

		if (this.config.enableRouting) {
			this.routeService.cleanup();
		}

		if (this.config.enableDriverTracking) {
			this.driverLocationManagerService.cleanup();
		}

		this.mapService.destroyMap();
		this.mapInitialized = false;
	}

	// ===== Public API Methods =====

	addLocationPin(lat: number, lng: number): string {
		return this.locationPinService.addPin(lat, lng);
	}

	removeLocationPin(id: string): void {
		this.locationPinService.removePin(id);
	}

	clearAllLocationPins(): void {
		this.locationPinService.clearAllPins();
		this.sharedLocationsService.clearLocations();
	}

	getLocationPins() {
		return this.locationPinService.getAllPins();
	}

	highlightDriver(driverId: number): void {
		this.driverMarkerService.highlightMarker(driverId);
	}

	createRoute(waypoints: [number, number][], options?: any): string {
		return this.routeService.createRoute(waypoints, options);
	}

	createSimpleRoute(start: [number, number], end: [number, number]): string {
		return this.routeService.createSimpleRoute(start, end);
	}

	removeRoute(routeId: string): void {
		this.routeService.removeRoute(routeId);
	}

	clearAllRoutes(): void {
		this.routeService.clearAllRoutes();
		this.currentRouteId = undefined;
	}

	fitRouteInView(routeId: string): void {
		this.routeService.fitRouteInView(routeId);
	}

	toggleLocationPinClick(enable: boolean): void {
		if (enable) {
			this.locationPinService.enableClickToAdd();
		} else {
			this.locationPinService.disableClickToAdd();
		}
	}

	toggleLocationPinRightClick(enable: boolean): void {
		if (enable) {
			this.locationPinService.enableRightClickToRemoveLast();
		} else {
			this.locationPinService.disableRightClickToRemoveLast();
		}
	}

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

		console.log(`Route created with ${waypoints.length} waypoints`);
	}

	private clearCurrentRoute(): void {
		if (this.currentRouteId) {
			this.routeService.removeRoute(this.currentRouteId);
			this.currentRouteId = undefined;
			console.log('Current route cleared');
		}
	}

	private clearPath(): void {
		if (this.currentPathId) {
			this.routeService.removeRoute(this.currentPathId);
			this.currentPathId = undefined;
			console.log('Current path cleared');
		}
	}

	private drawPath(): void {
		this.clearPath();

		if (!this.path) return;

		console.log('Drawing geohash path:', this.path);

		this.currentPathId = this.routeService.drawLineFromGeohash(this.path, 12, {
			color: '#e74c3c', // Different color for actual path (red)
			weight: 4,
			opacity: 0.8,
		});

		this.routeService.fitRouteInView(this.currentPathId);
	}
}
