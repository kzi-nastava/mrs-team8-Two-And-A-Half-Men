import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

export interface RideStop {
  id?: number;
  address: string;
  latitude: number;
  longitude: number;
  order: number;
  completed?: boolean;
}

export interface Ride {
  id: number;
  driverId: number;
  passengerId: number;
  stops: RideStop[];
  estimatedDistance: number;
  estimatedDuration: number;
  status: 'PENDING' | 'ACCEPTED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  startTime?: Date;
  endTime?: Date;
}

@Injectable({
  providedIn: 'root'
})
export class RideService {
  private apiUrl = 'http://localhost:8080/api/v1/rides';
  
  private currentRideSubject = new BehaviorSubject<Ride | null>(null);
  currentRide$ = this.currentRideSubject.asObservable();

  constructor(private http: HttpClient) {}

  getActiveRide(userId: number): Observable<Ride> {
    return this.http.get<Ride>(`${this.apiUrl}/active/${userId}`);
  }

  getRideById(rideId: number): Observable<Ride> {
    return this.http.get<Ride>(`${this.apiUrl}/${rideId}`);
  }

  endRide(rideId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${rideId}/end`, {});
  }

  completeStop(rideId: number, stopId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${rideId}/stops/${stopId}/complete`, {});
  }

  saveNote(rideId: number, note: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${rideId}/notes`, { note });
  }

  triggerPanic(rideId: number, location: { latitude: number, longitude: number }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${rideId}/panic`, { location });
  }

  setCurrentRide(ride: Ride | null) {
    this.currentRideSubject.next(ride);
  }

  getCurrentRide(): Ride | null {
    return this.currentRideSubject.value;
  }
}