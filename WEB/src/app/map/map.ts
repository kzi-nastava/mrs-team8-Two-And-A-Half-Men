import { Component, AfterViewInit, signal } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
})
export class MapComponent implements AfterViewInit {
  private map!: L.Map;
  markers = signal<L.Marker[]>([]);

  availableDriverIcon = L.icon({
    iconUrl: 'assets/icons/car-available.png',
    iconSize: [40, 40],
    iconAnchor: [20, 40],
    popupAnchor: [0, -40]
  });

  occupiedDriverIcon = L.icon({
    iconUrl: 'assets/icons/car-occupied.png',
    iconSize: [40, 40],
    iconAnchor: [20, 40],
    popupAnchor: [0, -40]
  });

  constructor() {}

  getMap(): L.Map | undefined {
    return this.map;
  }

  createMarker(
    position: L.LatLngExpression,
    isOccupied: boolean,
    popupContent: string
  ): L.Marker {
    const icon = isOccupied ? this.occupiedDriverIcon : this.availableDriverIcon;
    
    return L.marker(position, { icon })
      .bindPopup(popupContent);
  }

  private initMap(): void {
    this.map = L.map('map', {
      center: [45.2396, 19.8227],
      zoom: 13,
    });

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
      marker.on('click', () => {
        this.removeMarker(marker);
      });

      this.markers.update(m => [...m, marker]);
    });
  }


  removeLastMarker(): void {
    const currentMarkers = this.markers();

    if (currentMarkers.length === 0) {
      return;
    }

    const lastMarker = currentMarkers[currentMarkers.length - 1];
    this.map.removeLayer(lastMarker);

    this.markers.set(currentMarkers.slice(0, -1));
  }

  registerOnRightClick(): void {
    this.map.on('contextmenu', (e: L.LeafletMouseEvent) => {
      e.originalEvent.preventDefault();
      this.removeLastMarker();
    });
  }

  removeMarker(markerToRemove: L.Marker): void {
    this.map.removeLayer(markerToRemove);

    this.markers.update(markers =>
      markers.filter(marker => marker !== markerToRemove)
    );
  }

  removeMarkerByIndex(index: number): void {
    const currentMarkers = this.markers();

    if (index < 0 || index >= currentMarkers.length) {
      return;
    }

    const marker = currentMarkers[index];

    this.map.removeLayer(marker);

    this.markers.set(
      currentMarkers.filter((_, i) => i !== index)
    );
  }

  removeMarkerByCoordinates(lat: number, lng: number): void {
    const marker = this.markers().find(m => {
      const pos = m.getLatLng();
      return pos.lat === lat && pos.lng === lng;
    });

    if (!marker) {
      return;
    }

    this.removeMarker(marker);
  }

}
