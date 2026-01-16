import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class CancelationService {
  constructor(private http: HttpClient) {}

  createCancellationRequest(reason: string, id: string | number): Observable<{ message: string | null }> {

    return this.http.post<{ message: string | null }>(`http://localhost:8080/api/v1/rides/${id}/cancel`, { reason });
  }
  
}
