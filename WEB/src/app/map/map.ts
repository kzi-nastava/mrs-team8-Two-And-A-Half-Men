import { Component, AfterViewInit, signal, effect } from '@angular/core';
import * as L from 'leaflet';
import { SheredLocationsService } from '../service/shered-locations-service';
import { MapService } from './services/map-service'; 
import { DriverLocationManagerService } from '../driver-location/services/driver-location-manager-service';

@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
})
export class MapComponent implements AfterViewInit {
  private map!: L.Map;
  markers: L.Marker[] = [];

  constructor(
    private sharedLocationsService: SheredLocationsService, 
    private driverLocationManager: DriverLocationManagerService,
    private mapService: MapService) {
     effect(() => {
        const locations = this.sharedLocationsService.locations();
        console.log('Locations updated:', locations);
        for (const marker of this.markers) {
            console.log('Removing marker:', marker);
            this.map.removeLayer(marker);
        }
        this.markers = [];
        locations.forEach(location => {
            const marker = L.marker([parseFloat(location.lat), parseFloat(location.lon)]).addTo(this.map);
            marker.on('click', () => {
                this.removeMarker(marker);
            });
            this.markers.push(marker);
        }
        );
    });
  }

  getMap(): L.Map | undefined {
    return this.map;
  }

  // createMarker(
  //   position: L.LatLngExpression,
  //   isOccupied: boolean,
  //   popupContent: string
  // ): L.Marker {
  //   const icon = isOccupied ? this.occupiedDriverIcon : this.availableDriverIcon;
    
  //   return L.marker(position, { icon })
  //     .bindPopup(popupContent);
  // }

  private initMap(): void {
    this.map = this.mapService.initMap('map', [45.2396, 19.8227], 13);
    this.driverLocationManager.initialize();

    const tiles = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        maxZoom: 18,
        minZoom: 3,
        attribution:
          '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      }
    );
    tiles.addTo(this.map);
  }

  ngAfterViewInit(): void {
    const DefaultIcon = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.6.0/dist/images/marker-icon.png',
      iconSize: [25, 41], 
      iconAnchor: [12, 41],
    });

    L.Marker.prototype.options.icon = DefaultIcon;

    this.initMap();
    this.registerOnClick();
    this.registerOnRightClick();
  }

  registerOnClick(): void {
    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const { lat, lng } = e.latlng;
      const marker = L.marker([lat, lng]).addTo(this.map);
      this.markers.push(marker);
      marker.on('click', () => {
        this.removeMarker(marker);
      });
      this.sharedLocationsService.addLocation(marker); 
    });
  }


  removeLastMarker(): void {
    const currentMarkers = this.markers;
    console.log('Current markers count:', currentMarkers.length);
    console.log(this.sharedLocationsService.locations())
    if (currentMarkers.length === 0) {
      return;
    }
    console.log('Removing last marker');
    const lastMarker = currentMarkers[currentMarkers.length - 1];
    this.map.removeLayer(lastMarker);
    this.sharedLocationsService.locations.set(this.sharedLocationsService.locations().slice(0, -1));
  }

  registerOnRightClick(): void {
    this.map.on('contextmenu', (e: L.LeafletMouseEvent) => {
      e.originalEvent.preventDefault();
      this.removeLastMarker();
    });
  }

  removeMarker(markerToRemove: L.Marker): void {
    this.map.removeLayer(markerToRemove);
    this.markers = this.markers.filter(marker => marker !== markerToRemove);
  }

  removeMarkerByIndex(index: number): void {
    const currentMarkers = this.markers;
    if (index < 0 || index >= currentMarkers.length) {
      return;
    }
    const marker = currentMarkers[index];
    this.map.removeLayer(marker);
    this.markers = currentMarkers.filter((_, i) => i !== index);
  }

  removeMarkerByCoordinates(lat: number, lng: number): void {
    const marker = this.markers.find(m => {
      const pos = m.getLatLng();
      return pos.lat === lat && pos.lng === lng;
    });

    if (!marker) {
      return;
    }
    this.removeMarker(marker);
  }

}
