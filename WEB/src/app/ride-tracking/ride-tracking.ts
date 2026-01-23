import { Component, OnInit, OnDestroy } from '@angular/core';
import { Auth } from '../service/auth';
import { UserRole } from '../model/user-role';
import { RideService, Ride } from '../service/ride-service';
import { DriverLocationManagerService } from '../driver-location/services/driver-location-manager-service';
import { RideInfoPanelComponent } from './ride-info-panel/ride-info-panel';
import { DriverRideInfoPanelComponent } from './driver-ride-info-panel/driver-ride-info-panel';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-ride-tracking',
  standalone: true,
  imports: [RideInfoPanelComponent, DriverRideInfoPanelComponent],
  templateUrl: './ride-tracking.html',
  styleUrls: ['./ride-tracking.css']
})
export class RideTrackingComponent implements OnInit, OnDestroy {
  currentUserRole: UserRole | null = null;
  UserRole = UserRole;
  
  rideData: any = {
    id: 0,
    stops: [],
    remainingDistance: 0,
    remainingTime: 0
  };

  private destroy$ = new Subject<void>();

  constructor(
    private auth: Auth,
    private rideService: RideService,
    private driverLocationManager: DriverLocationManagerService
  ) {}

  ngOnInit() {
    this.currentUserRole = this.auth.getRole();
    this.currentUserRole = UserRole.PASSENGER;
    this.loadCurrentRide();
    
    this.driverLocationManager.initialize();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    
    this.driverLocationManager.cleanup();
  }

  loadCurrentRide() {
    const user = this.auth.user();
    
    if (user?.id) {
      this.rideService.getActiveRide(user.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (ride: Ride) => {
            console.log('Active ride loaded:', ride);
            this.mapRideToDisplayData(ride);
            this.rideService.setCurrentRide(ride);
          },
          error: (error) => {
            console.error('Error loading active ride:', error);
            this.loadTestData();
          }
        });
    } else {
      this.loadTestData();
    }
  }

  mapRideToDisplayData(ride: Ride) {
    this.rideData = {
      id: ride.id,
      stops: ride.stops.map((stop, index) => ({
        name: stop.address,
        latitude: stop.latitude,
        longitude: stop.longitude,
        completed: stop.completed || false,
      })),
      remainingDistance: ride.estimatedDistance,
      remainingTime: ride.estimatedDuration,
      status: ride.status
    };
  }

  findCurrentStopIndex(stops: any[]): number {
    const index = stops.findIndex(stop => !stop.completed);
    return index !== -1 ? index : 0;
  }

  loadTestData() {
    this.rideData = {
      id: 1,
      stops: [
        { 
          name: 'Bulevar Despota Stefana 7, Novi Sad', 
          latitude: 45.2551, 
          longitude: 19.8451,
          completed: false 
        },
        { 
          name: 'Bulevar Despota Stefana 7, Novi Sad', 
          latitude: 45.2561, 
          longitude: 19.8461,
          completed: true 
        },
        { 
          name: 'Bulevar Despota Stefana 7, Novi Sad', 
          latitude: 45.2571, 
          longitude: 19.8471,
          completed: false 
        },
        { 
          name: 'Novosadskog sajma 15, Novi Sad', 
          latitude: 45.2581, 
          longitude: 19.8481,
          completed: false 
        }
      ],
      remainingDistance: 7.2,
      remainingTime: 50,
      status: 'active'
    };
  }

  onSaveNote(note: string) {
    if (this.rideData.id) {
      this.rideService.saveNote(this.rideData.id, note)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            console.log('Note saved successfully:', note);
            alert('Note saved!');
          },
          error: (error) => {
            console.error('Error saving note:', error);
            alert('Failed to save note');
          }
        });
    }
  }

  onPanic() {
    console.log('PANIC triggered!');
  }

  onEndRide() {
    if (this.rideData.id) {
      const confirmed = confirm('Are you sure you want to end this ride?');
      if (confirmed) {
        this.rideService.endRide(this.rideData.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              console.log('Ride ended successfully');
              this.rideData.status = 'COMPLETED';
              alert('Ride completed successfully!');
            },
            error: (error) => {
              console.error('Error ending ride:', error);
              alert('Failed to end ride');
            }
          });
      }
    }
  }
}