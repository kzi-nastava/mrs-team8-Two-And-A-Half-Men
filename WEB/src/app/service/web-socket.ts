import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';



@Injectable({
  providedIn: 'root',
})
export class WebSocket {

    private stompClient: any;
    private isLogedIn: boolean = false;

  constructor() {}
  connect() {
       
      const ws = new SockJS('http://localhost:8080/socket');
      
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
    if(this.isLogedIn){ 
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
      text: `Panic alert received from ride ID: ${data.rideId} by ${data.reportedBy}. Reason: ${data.reason}`,
      icon: 'warning',
      confirmButtonText: 'OK'
    });

  }
}
