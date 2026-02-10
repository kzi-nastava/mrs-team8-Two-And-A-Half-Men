import {
	Input,
	OnChanges,
	SimpleChanges,
	Component,
	AfterViewInit,
	OnDestroy,
	effect,
} from '@angular/core';
import { MapService } from './services/map.service';
import { LocationPinService } from './services/location-pin.service';
import { DriverMarkerService } from './services/driver-marker.service';
import { RouteService } from './services/route.service';
import { DriverLocationManagerService } from '@shared/services/driver-location/driver-location-manager.service';
import { SharedLocationsService } from '@shared/services/locations/shared-locations.service';
import { DEFAULT_MAP_CONFIG, MapConfig } from '@shared/components/map/map.config';
import { PopupsService } from '@shared/services/popups/popups.service';

@Component({
	selector: 'app-map',
	templateUrl: './map.component.html',
	styleUrls: ['./map.component.css'],
})
export class MapComponent implements AfterViewInit, OnDestroy, OnChanges {
	@Input() path?: string;
	@Input() config: MapConfig = DEFAULT_MAP_CONFIG;
	private currentRouteId?: string;
	private mapInitialized = false;

	constructor(
		private mapService: MapService,
		private locationPinService: LocationPinService,
		private driverMarkerService: DriverMarkerService,
		private routeService: RouteService,
		private driverLocationManager: DriverLocationManagerService,
		private sharedLocationsService: SharedLocationsService,
		private popupService: PopupsService
	) {
		effect(() => {
			const locations = this.sharedLocationsService.locations();

			if (this.config.enableRouting && locations.length >= 2 && this.mapInitialized) {
				this.updateRouteFromLocations(locations);
			} else {
				this.clearCurrentRoute();
			}
		});
	}

	ngAfterViewInit(): void {
		console.log('Initializing map...');

		this.config = { ...DEFAULT_MAP_CONFIG, ...this.config };

		this.mapService.initMap('map', this.config.center!, this.config.zoom!);

		this.mapInitialized = true;

		this.initializeEnabledServices();

		if (this.path) {
			this.drawPath();
		}
	}

	private initializeEnabledServices(): void {
		if (this.config.enableLocationPins) {
			this.locationPinService.initialize();
		}

		if (this.config.enableDriverMarkers) {
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
			this.driverLocationManager.initialize();
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
					this.popupService.error('Error getting location:', error);
				},
			});
		});
	}

	private setupMapRightClickHandler(): void {
		const map = this.mapService.getMap();

		map.on('contextmenu', (e: L.LeafletMouseEvent) => {
			e.originalEvent.preventDefault();
			console.log('Right click - removing last location');
			this.sharedLocationsService.removeLastLocation();
		});
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (changes['path'] && this.path && this.mapInitialized) {
			this.drawPath();
		}
	}

	ngOnDestroy(): void {
		console.log('Destroying map component...');

		if (this.config.enableLocationPins) {
			this.locationPinService.cleanup();
		}

		if (this.config.enableDriverMarkers) {
			this.driverMarkerService.cleanup();
		}

		if (this.config.enableRouting) {
			this.routeService.cleanup();
		}

		if (this.config.enableDriverTracking) {
			this.driverLocationManager.cleanup();
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

	private drawPath(): void {
		this.clearCurrentRoute();

		if (!this.path) return;

		this.currentRouteId = this.routeService.drawLineFromGeohash(this.path, 12, {
			color: '#3388ff',
			weight: 6,
			opacity: 0.7,
		});

		this.routeService.fitRouteInView(this.currentRouteId);
	}
}
