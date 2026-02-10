import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '@environments/environment';
import { Ride } from '@features/history/models/ride.model';
import { PopupsService } from '@shared/services/popups/popups.service';
import { SortDirection, SortField } from '@features/history/models/ride-history-config';

@Injectable({
	providedIn: 'root',
})
export class HistoryService {
	private http = inject(HttpClient);
	popupsService = inject(PopupsService);

	rides = signal<Ride[]>([]);
	page = signal(0);
	size = signal(5);
	totalElements = signal(0);
	loading = signal(false);
	loadingDetails = signal(false);

	sortField = signal<SortField>('scheduledTime');
	sortDirection = signal<SortDirection>('DESC');

	startDate = signal<string | null>(null);
	endDate = signal<string | null>(null);

	driverId = signal<number | null>(null);
	customerId = signal<number | null>(null);

	selectedRide = signal<Ride | null>(null);

	getHistory() {
		this.loading.set(true);

		let params = new HttpParams()
			.set('page', this.page())
			.set('size', this.size())
			.set('sortBy', this.sortField())
			.set('sortDirection', this.sortDirection());

		if (this.startDate()) {
			params = params.set('startDate', this.startDate()!);
		}

		if (this.endDate()) {
			params = params.set('endDate', this.endDate()!);
		}

		if (this.driverId()) {
			params = params.set('driverId', this.driverId()!);
		}

		if (this.customerId()) {
			params = params.set('customerId', this.customerId()!);
		}

		this.http.get<any>(`/api/${environment.apiVersion}/rides/history`, { params }).subscribe({
			next: (res) => {
				this.rides.set(res.content);
				this.totalElements.set(res.totalElements);
				this.loading.set(false);
			},
			error: (err) => {
				this.popupsService.error(
					'Error',
					'Failed to load ride history. Please try again later. ' + err.message,
				);
				this.loading.set(false);
			},
		});
	}

	getRideDetails(): Observable<Ride> {
		this.loadingDetails.set(true);

		return this.http
			.get<Ride>(`/api/${environment.apiVersion}/rides/${this.selectedRide()?.id}`)
			.pipe(
				tap({
					next: () => this.loadingDetails.set(false),
					error: () => this.loadingDetails.set(false),
				}),
			);
	}

	setPage(page: number) {
		this.page.set(page);
		this.getHistory();
	}

	setPageSize(size: number) {
		this.size.set(size);
		this.page.set(0);
		this.getHistory();
	}

	setSortField(field: SortField) {
		this.sortField.set(field);
		this.page.set(0);
		this.getHistory();
	}

	toggleSortDirection() {
		this.sortDirection.set(this.sortDirection() === 'ASC' ? 'DESC' : 'ASC');
		this.page.set(0);
		this.getHistory();
	}

	setSorting(field: SortField, direction: SortDirection) {
		this.sortField.set(field);
		this.sortDirection.set(direction);
		this.page.set(0);
		this.getHistory();
	}

	setDateRange(startDate: string | null, endDate: string | null) {
		this.startDate.set(startDate);
		this.endDate.set(endDate);
		this.page.set(0);
		this.getHistory();
	}

	setUserFilters(driverId: number | null, customerId: number | null) {
		this.driverId.set(driverId);
		this.customerId.set(customerId);
		this.page.set(0);
		this.getHistory();
	}

	clearFilters() {
		this.startDate.set(null);
		this.endDate.set(null);
		this.driverId.set(null);
		this.customerId.set(null);
		this.page.set(0);
		this.getHistory();
	}

	reset() {
		this.page.set(0);
		this.size.set(5);
		this.sortField.set('scheduledTime');
		this.sortDirection.set('DESC');
		this.startDate.set(null);
		this.endDate.set(null);
		this.driverId.set(null);
		this.customerId.set(null);
	}

	toggleFavorite(rideId: number) {
	}
}
