import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environments';

@Injectable({
  providedIn: 'root',
})
export class CancelationService {
  constructor(private http: HttpClient) {}

  createCancellationRequest(reason: string, cancelledBy: string, id: string | number): Observable<{ message: string | null }> {

    return this.http.post<{ message: string | null }>(`${environment.apiUrl}/v1/rides/${id}/cancel`, { reason, cancelledBy });
  }
  
}
