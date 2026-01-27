import { Component, AfterViewInit, OnDestroy, effect } from '@angular/core';
import { MapService } from './services/map-service';
import { LocationPinService } from './services/location-pin-service';
import { DriverMarkerService } from './services/driver-marker-service';
import { RouteService } from './services/route-service';
import { DriverLocationManagerService } from '../driver-location/services/driver-location-manager-service';
import { SheredLocationsService } from '../service/shered-locations-service';

@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
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
    private sheredLocationsService: SheredLocationsService
  ) {
  
  }

  ngAfterViewInit(): void {
    console.log('Initializing map...');
    
    // Initialize the map
    this.mapService.initMap('map', [45.2396, 19.8227], 13);
    this.mapInitialized = true;

    // Initialize all services
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

  // Postavlja handler za klik na mapu
  private setupMapClickHandler(): void {
    const map = this.mapService.getMap();
    
    map.on('click', (e: L.LeafletMouseEvent) => {
      const { lat, lng } = e.latlng;
      
      console.log('Map clicked at:', lat, lng);
      
      // Dodaj lokaciju u SheredLocationsService (to će triggerovati effect)
      this.sheredLocationsService.addLocationFromCoordinates(lat, lng).subscribe({
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

  // Postavlja handler za desni klik (remove last)
  private setupMapRightClickHandler(): void {
    const map = this.mapService.getMap();
    
    map.on('contextmenu', (e: L.LeafletMouseEvent) => {
      e.originalEvent.preventDefault();      
      console.log('Right click - removing last location');
      this.sheredLocationsService.removeLastLocation();
    });
  }

  ngOnDestroy(): void {
    console.log('Destroying map component...');
    
    // Cleanup all services
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
    this.sheredLocationsService.clearLocations();
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
}