import { Injectable, Signal, signal } from '@angular/core';
import * as L from 'leaflet';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class SheredLocationsService {

    constructor(private http: HttpClient) {}

    public locations = signal<NominatimResult[]>([]);
    setLocation(location: NominatimResult) {
        this.locations.update(list => [...list, location]);
    }
    addLocation(location: L.Marker) {
        this.markerToNominatim(location).subscribe((nominatimResult) => {
           this.locations.update(list => [...list, nominatimResult]);
        });
        console.log('Location added:', this.locations());
    }
    reverseGeocode(lat: number, lon: number) {
    const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}`;
    return this.http.get<any>(url);
  }
  markerToNominatim(marker: L.Marker) {
  const { lat, lng } = marker.getLatLng();

  return this.reverseGeocode(lat, lng).pipe(
    map(res => ({
      place_id: res.place_id,
      display_name: res.display_name,
      lat: res.lat,
      lon: res.lon,
    }))
  );
}

      

}
