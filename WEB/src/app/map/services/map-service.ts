import { Injectable } from '@angular/core';
import * as L from 'leaflet';

@Injectable({
  providedIn: 'root',
})
export class MapService {
  private map?: L.Map;

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

  initMap(containerId: string, center: [number, number], zoom = 13): L.Map {
    this.map = L.map(containerId).setView(center, zoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; OpenStreetMap',
    }).addTo(this.map);

    return this.map;
  }

  getMap(): L.Map | undefined {
    return this.map;
  }

  getAvailableIcon(): L.Icon {
    return this.availableIcon;
  }

  getOccupiedIcon(): L.Icon {
    return this.occupiedIcon;
  }
}
