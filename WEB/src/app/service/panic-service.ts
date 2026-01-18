import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

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
}
