import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from "@environments/environment";
import { CostTimeDTO } from '../../model/CostTimeDTO';
@Injectable({
  providedIn: 'root',
})
export class EndRideService {

  constructor(private http: HttpClient) {}

  public endRide(id : number): Observable<CostTimeDTO> {

    return this.http.patch<CostTimeDTO>(`${environment.apiUrl}/v1/rides/${id}/end`, {});
  }
}
