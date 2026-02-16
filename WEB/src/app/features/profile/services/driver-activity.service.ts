import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { inject } from "@angular/core";
import { environment } from "@environments/environment";
import { Observable } from 'rxjs';



@Injectable({
  providedIn: "root"
})
export class DriverActivityService {
    private http = inject(HttpClient);

    public activateDriver(): Observable<any> {
        return this.http.patch(`/api/${environment.apiVersion}/driver/working/start`, {});
    }

    public  deactivateDriver(): Observable<any> {
        return this.http.patch(`/api/${environment.apiVersion}/driver/working/stop`, {});
    }
}