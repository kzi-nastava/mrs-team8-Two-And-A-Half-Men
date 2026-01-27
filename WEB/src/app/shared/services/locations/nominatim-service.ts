import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, delay, retry, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class NominatimService {
  private readonly baseUrl = 'https://nominatim.openstreetmap.org';
  private readonly userAgent = 'RouteEstimateApp/1.0 (your-email@example.com)';
  
  private MOCK_MODE = false;
  
  private lastRequestTime = 0;
  private readonly minRequestInterval = 2000; // 2 sekunde

  constructor(private http: HttpClient) {}

  search(query: string, limit: number = 5): Observable<NominatimResult[]> {
    if (this.MOCK_MODE) {
      console.log('MOCK MODE: Search', query);
      return of(this.getMockSearchResults(query)).pipe(delay(300));
    }

    this.waitForRateLimit();

    const url = `${this.baseUrl}/search`;
    const params = {
      format: 'json',
      q: query,
      limit: limit.toString(),
      addressdetails: '1',
    };

    return this.http.get<NominatimResult[]>(url, {
      params,
      headers: {
        'User-Agent': this.userAgent,
        'Accept-Language': 'sr-RS,sr;q=0.9,en;q=0.8',
      },
    }).pipe(
      tap(() => this.lastRequestTime = Date.now()),
      retry({
        count: 1,
        delay: 2000,
      }),
      catchError(this.handleError)
    );
  }

  reverse(lat: number, lon: number): Observable<NominatimResult> {
    if (this.MOCK_MODE) {
      console.log('MOCK MODE: Reverse', lat, lon);
      return of(this.getMockReverseResult(lat, lon)).pipe(delay(300));
    }

    this.waitForRateLimit();

    const url = `${this.baseUrl}/reverse`;
    const params = {
      format: 'json',
      lat: lat.toString(),
      lon: lon.toString(),
      addressdetails: '1',
    };

    return this.http.get<NominatimResult>(url, {
      params,
      headers: {
        'User-Agent': this.userAgent,
        'Accept-Language': 'sr-RS,sr;q=0.9,en;q=0.8',
      },
    }).pipe(
      tap(() => this.lastRequestTime = Date.now()),
      retry({
        count: 1,
        delay: 2000,
      }),
      catchError(this.handleError)
    );
  }

  private getMockReverseResult(lat: number, lon: number): NominatimResult {
    const streets = ['Bulevar oslobođenja', 'Булевар деспота Стефана', 'Максима Горког', 'Zmaj Jovina', 'Народних Хероја'];
    const randomStreet = streets[Math.floor(Math.random() * streets.length)];
    const randomNumber = Math.floor(Math.random() * 100) + 1;
    
    return {
      place_id: Math.floor(Math.random() * 1000000),
      display_name: `${randomStreet} ${randomNumber}, Нови Сад, Јужнобачки управни округ, Војводина, Србија`,
      lat: lat.toString(),
      lon: lon.toString()
    };
  }

  private getMockSearchResults(query: string): NominatimResult[] {
    const results: NominatimResult[] = [];
    
    for (let i = 0; i < 3; i++) {
      results.push({
        place_id: Math.floor(Math.random() * 1000000),
        display_name: `${query} ${i + 1}, Нови Сад, Србија`,
        lat: (45.25 + Math.random() * 0.05).toString(),
        lon: (19.83 + Math.random() * 0.05).toString()
      });
    }
    
    return results;
  }

  private waitForRateLimit(): void {
    const now = Date.now();
    const timeSinceLastRequest = now - this.lastRequestTime;
    
    if (timeSinceLastRequest < this.minRequestInterval) {
      const waitTime = this.minRequestInterval - timeSinceLastRequest;
      console.log(`Rate limiting: waiting ${waitTime}ms`);
    }
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Unknown error occurred';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Client Error: ${error.error.message}`;
    } else if (error.status === 0) {
      errorMessage = 'Network error - Nominatim je možda nedostupan. Pokušajte ponovo za 10-15 minuta.';
    } else if (error.status === 429) {
      errorMessage = 'Previše zahteva ka Nominatim API-ju. Sačekajte 10-15 minuta.';
    } else if (error.status === 403) {
      errorMessage = 'Nominatim API je blokirao zahtev.';
    } else {
      errorMessage = `Server Error: ${error.status} - ${error.message}`;
    }

    console.error('Nominatim API Error:', errorMessage, error);
    return throwError(() => new Error(errorMessage));
  }
}