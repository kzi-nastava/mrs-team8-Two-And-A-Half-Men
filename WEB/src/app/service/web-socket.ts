import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { Auth } from './auth';
import { PanicService } from './panic-service';
import { BehaviorSubject, Observable } from 'rxjs';
import { DriverLocation } from '../driver-location/models/driver-location';



@Injectable({
  providedIn: 'root',
})
export class WebSocket {

    private stompClient: any;
    private isLogedIn: boolean = false;

    private driverLocationsSubject = new BehaviorSubject<Map<number, DriverLocation>>(new Map());
    public driverLocations$: Observable<Map<number, DriverLocation>> = this.driverLocationsSubject.asObservable();


  constructor(private auth: Auth, private panicService: PanicService) {}
  connect() {
       
      const ws = new SockJS('http://localhost:8080/socket', null,
      {
      });

      this.stompClient = Stomp.over(ws);
      this.stompClient.debug = (str : any) => {
        console.log(str);
      };

      let that = this;
      this.stompClient.connect({}, function(){ 
        that.isLogedIn = true;
        that.subscribeToPanic();
        that.subscribeToDriverLocations();
      });

      
  }

  private subscribeToPanic() {
    console.log('this.auth.getRole()', this.auth.getRole());
    if(this.isLogedIn && this.auth.getRole() == 'ROLE_ADMIN') { 
        this.stompClient.subscribe('/topic/panic', (message: any) => {
          console.log('Panic alert received:', message.body);
          if (message.body) {
         let data = JSON.parse(message.body);
        this.panicService.alertPanic(data);
        }
      });
    }
  }

  private subscribeToDriverLocations() {
    if (this.isLogedIn) {
      this.stompClient.subscribe('/topic/driver-locations', (message: any) => {
        if (message.body) {
          const location: DriverLocation = JSON.parse(message.body);
          console.log(location);
          this.updateDriverLocation(location);
        }
      });
    }
  }

  sendDriverLocation(location: { latitude: number; longitude: number }) {
    if (this.isLogedIn && this.stompClient) {
      this.stompClient.send('/app/driver/location', {}, JSON.stringify(location));
    }
  }

  private updateDriverLocation(location: DriverLocation) {
    const currentLocations = this.driverLocationsSubject.value;
    
    if (location.latitude === null || location.longitude === null) {
      currentLocations.delete(location.driverId);
    } else {
      currentLocations.set(location.driverId, location);
    }
    
    this.driverLocationsSubject.next(new Map(currentLocations));
  }
  
  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect();
      this.isLogedIn = false;
    }
  }
}
