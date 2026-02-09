// driver-location/history.service.ts
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Ride } from '@features/driver/history/models/ride.model';
import { PopupsService } from '@shared/services/popups/popups.service';

@Injectable({ providedIn: 'root' })
export class HistoryService {
	private apiUrl = '/api/v1/rides/history';
	private popupsService = inject(PopupsService);

	rides = signal<Ride[]>([]);
	page = signal(0);
	size = signal(5);
	totalElements = signal(0);

	sortDirection = signal<'ASC' | 'DESC'>('DESC');
	startDate = signal<string | null>(null);
	endDate = signal<string | null>(null);

	hasEverLoaded = signal(false);

	constructor(private http: HttpClient) {}

	load(): void {
		let params = new HttpParams()
			.set('page', this.page())
			.set('size', this.size())
			.set('sort', `scheduledTime,${this.sortDirection()}`);

		if (this.startDate()) {
			params = params.set('startDate', this.startDate()!);
		}

		if (this.endDate()) {
			params = params.set('endDate', this.endDate()!);
		}

		this.http.get<any>(this.apiUrl, { params }).subscribe({
			next: (res) => {
				this.rides.set(res.content);
				this.totalElements.set(res.totalElements);

				this.hasEverLoaded.set(true);
			},
			error: (err) => {
				this.popupsService.error(
					'Error',
					'Failed to load ride history. Please try again later. ' + err.message,
				);
			},
		});
	}

	setSort(direction: 'ASC' | 'DESC'): void {
		this.sortDirection.set(direction);
		this.page.set(0);
		this.load();
	}

	setDateRange(start: string | null, end: string | null): void {
		this.startDate.set(start);
		this.endDate.set(end);
	}

	filter(): void {
		this.page.set(0);
		this.load();
	}

	next(): void {
		if ((this.page() + 1) * this.size() < this.totalElements()) {
			this.page.update((p) => p + 1);
			this.load();
		}
	}

	prev(): void {
		if (this.page() > 0) {
			this.page.update((p) => p - 1);
			this.load();
		}
	}
}
