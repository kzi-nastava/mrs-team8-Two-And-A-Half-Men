import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class PanicService {
  constructor(private http: HttpClient) {}

  panicAlert(token: string | null): Observable<any> {
    console.log('PanicService.panicAlert called with token:', token); // Debug log
    if(!token) {
      return this.http.post<any>('http://localhost:8080/api/v1/rides/panic',{});
    }
    return this.http.post<any>(`http://localhost:8080/api/v1/rides/panic?accessToken=${token}`,{});
  }
  public alertPanic(data: any) {
    let text = '';
    if(!data) {
      text = 'Panic alert received with no additional data.';
    } else if(data.passengerName) {
    text = `Panic alert received from ride ID: ${data.rideId} driver name: ${data.driverName}. 
      Triggered by ${data.passengerName} in location ${data.driverLocation}`;
    } else {
    text = `Panic alert received from ride ID: ${data.rideId} driver name: ${data.driverName}.
       in location ${data.driverLocation}`;
    }
       Swal.fire({
      title: 'Panic Alert',
      text: text,
      icon: 'warning',
      confirmButtonText: 'OK'
    });

  }
}
