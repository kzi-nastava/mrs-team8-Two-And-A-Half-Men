import { Injectable } from '@angular/core';
import * as L from 'leaflet';

@Injectable({
  providedIn: 'root',
})
export class MapService {
  private map?: L.Map;

  initMap(
    containerId: string,
    center: [number, number],
    zoom = 13
  ): L.Map {
    if (this.map) {
      console.warn('Map already initialized');
      return this.map;
    }

    this.map = L.map(containerId).setView(center, zoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      minZoom: 3,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.map);

    return this.map;
  }

  getMap(): L.Map {
    if (!this.map) {
      throw new Error('Map is not initialized. Call initMap() first.');
    }
    return this.map;
  }

  isInitialized(): boolean {
    return !!this.map;
  }

  destroyMap(): void {
    if (this.map) {
      this.map.remove();
      this.map = undefined;
    }
  }
}