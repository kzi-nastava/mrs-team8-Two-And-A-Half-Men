import { Component, AfterViewInit, OnDestroy, effect } from '@angular/core';
import { MapService } from './services/map-service';
import { LocationPinService } from './services/location-pin-service';
import { DriverMarkerService } from './services/driver-marker-service';
import { RouteService } from './services/route-service';
import { DriverLocationManagerService } from '@shared/services/driver-location/driver-location-manager-service';
import { SharedLocationsService } from '@shared/services/locations/shared-locations.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
})
export class MapComponent implements AfterViewInit, OnDestroy {
  private currentRouteId?: string;
  private mapInitialized = false;

  constructor(
    private mapService: MapService,
    private locationPinService: LocationPinService,
    private driverMarkerService: DriverMarkerService,
    private routeService: RouteService,
    private driverLocationManager: DriverLocationManagerService,
    private sharedLocationsService: SharedLocationsService
  ) {
    effect(() => {
      const locations = this.sharedLocationsService.locations();
      console.log('Locations changed, updating route. Count:', locations.length);

      // Ako ima bar 2 lokacije, iscrtaj rutu
      if (locations.length >= 2 && this.mapInitialized) {
        this.updateRouteFromLocations(locations);
      } else {
        // Ukloni rutu ako ima manje od 2 tačke
        this.clearCurrentRoute();
      }
    });
  }

  ngAfterViewInit(): void {
    console.log('Initializing map...');

    // Initialize the map
    this.mapService.initMap('map', [45.2396, 19.8227], 13);
    this.mapInitialized = true;

    // Initialize all driver-location
    this.locationPinService.initialize();
    this.driverMarkerService.initialize();
    this.routeService.initialize();

    // VAŽNO: Povezujemo klik na mapu sa SheredLocationsService
    this.setupMapClickHandler();
    this.setupMapRightClickHandler();

    // Initialize driver location tracking
    this.driverLocationManager.initialize();

    console.log('Map component fully initialized');
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
          alert('Greška pri dobavljanju adrese. Pokušajte ponovo.');
        }
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

  ngOnDestroy(): void {
    console.log('Destroying map component...');

    // Cleanup all driver-location
    this.locationPinService.cleanup();
    this.driverMarkerService.cleanup();
    this.routeService.cleanup();
    this.driverLocationManager.cleanup();
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

    const waypoints: [number, number][] = locations.map(loc => [
      parseFloat(loc.lat),
      parseFloat(loc.lon)
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
}
