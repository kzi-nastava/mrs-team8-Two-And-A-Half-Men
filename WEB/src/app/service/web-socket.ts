import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { Auth } from './auth';



@Injectable({
  providedIn: 'root',
})
export class WebSocket {

    private stompClient: any;
    private isLogedIn: boolean = false;

  constructor(private auth: Auth) {}
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
      });

      
  }
  


  private subscribeToPanic() {
    console.log('this.auth.getRole()', this.auth.getRole());
    if(this.isLogedIn && this.auth.getRole() == 'ROLE_ADMIN') { 
        this.stompClient.subscribe('/topic/panic', (message: any) => {
          console.log('Panic alert received:', message.body);
          if (message.body) {
         let data = JSON.parse(message.body);
          this.showNottification(data);
        }
  });
    }
}
  private showNottification(data: any) {
    Swal.fire({
      title: 'Panic Alert',
      text: `Panic alert received from ride ID: ${data.rideId} driver name: ${data.driverName}. 
      Triggered by ${data.passengerName} in location ${data.driverLocation}`,
      icon: 'warning',
      confirmButtonText: 'OK'
    });

  }
}
