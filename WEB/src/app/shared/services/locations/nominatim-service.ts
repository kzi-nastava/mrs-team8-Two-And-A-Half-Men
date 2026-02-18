import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry, tap } from 'rxjs/operators';
import { NominatimResult } from '@shared/models/nominatim-results.model';

@Injectable({
	providedIn: 'root',
})
export class NominatimService {
	private readonly baseUrl = 'https://nominatim.openstreetmap.org';
	private readonly userAgent = 'RouteEstimateApp/1.0 (your-email@example.com)';

	private lastRequestTime = 0;
	private readonly minRequestInterval = 2000; // 2 seconds

	private http = inject(HttpClient);

	search(query: string, limit: number = 5): Observable<NominatimResult[]> {
		this.waitForRateLimit();

		const url = `${this.baseUrl}/search`;
		const params = {
			format: 'json',
			q: query,
			limit: limit.toString(),
			addressDetails: '1',
		};

		return this.http
			.get<NominatimResult[]>(url, {
				params,
				headers: {
					'User-Agent': this.userAgent,
					'Accept-Language': 'sr-RS,sr;q=0.9,en;q=0.8',
					'skip': 'true'
				},
			})
			.pipe(
				tap(() => (this.lastRequestTime = Date.now())),
				retry({
					count: 1,
					delay: 2000,
				}),
				catchError(this.handleError),
			);
	}

	reverse(lat: number, lon: number): Observable<NominatimResult> {
		this.waitForRateLimit();

		const url = `${this.baseUrl}/reverse`;
		const params = {
			format: 'json',
			lat: lat.toString(),
			lon: lon.toString(),
			addressDetails: '1',
		};

		return this.http
			.get<NominatimResult>(url, {
				params,
				headers: {
					'User-Agent': this.userAgent,
					'Accept-Language': 'sr-RS,sr;q=0.9,en;q=0.8',
				},
			})
			.pipe(
				tap(() => (this.lastRequestTime = Date.now())),
				retry({
					count: 1,
					delay: 2000,
				}),
				catchError(this.handleError),
			);
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
			errorMessage =
				'Network error - Nominatim is potentially unavailable. Try again in few minutes.';
		} else if (error.status === 429) {
			errorMessage = 'Too much requests for Nominatim API. Try again in few minutes.';
		} else if (error.status === 403) {
			errorMessage = 'Nominatim API blocked request.';
		} else {
			errorMessage = `Server Error: ${error.status} - ${error.message}`;
		}

		console.error('Nominatim API Error:', errorMessage, error);
		return throwError(() => new Error(errorMessage));
	}
}
