import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { Auth } from './auth';
import { PanicService } from './panic-service';
import { DriverLocation } from '../driver-location/models/driver-location';
import { DriverLocationWebsocketService } from '../driver-location/services/driver-location-websocket-service';
import { RideService } from './ride-service';


@Injectable({
  providedIn: 'root',
})
export class WebSocket {

    private stompClient: any;
    private isLogedIn: boolean = false;

  constructor(
    private auth: Auth, 
    private panicService: PanicService, 
    private driverLocationService: DriverLocationWebsocketService,
    private rideService: RideService
  ) {}
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
        that.subscribeToRideUpdates();
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
          this.driverLocationService.updateDriverLocation(location);
        }
      });
    }
  }

  private subscribeToRideUpdates() {
  if (this.isLogedIn) {
    this.stompClient.subscribe('/topic/ride-updates', (message: any) => {
      if (message.body) {
        const rideUpdate = JSON.parse(message.body);
        console.log('Ride update received:', rideUpdate);
        this.rideService.setCurrentRide(rideUpdate);
      }
    });
  }
}

  sendDriverLocation(location: { latitude: number; longitude: number }) {
    if (this.isLogedIn && this.stompClient) {
      this.stompClient.send('/app/driver/location', {}, JSON.stringify(location));
    }
  }
  
  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect();
      this.isLogedIn = false;
    }
  }
}
