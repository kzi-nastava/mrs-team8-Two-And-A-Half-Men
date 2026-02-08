import { Component, inject, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RIDE_HISTORY_CONFIGS } from '@features/history/models/ride-history-config';
import { HistoryService } from '@features/history/services/history.service';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';
import { Ride, RideStatus } from '@features/history/models/ride.model';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { PAGE_SIZE_OPTIONS, SORT_OPTIONS_BY_ROLE, SortField } from '@features/history/models/ride-history-config';

@Component({
	selector: 'app-history',
	standalone: true,
	imports: [CommonModule, FormsModule],
	templateUrl: './history.component.html',
	styleUrl: './history.component.css',
})
export class HistoryComponent {
	private route = inject(ActivatedRoute);
	private router = inject(Router);

	userRole = toSignal(this.route.data.pipe(map((data) => data['userRole'] as LoggedInUserRole)), {
		requireSync: true,
	});

	historyService = inject(HistoryService);

	rides = this.historyService.rides;
	loading = this.historyService.loading;
	totalElements = this.historyService.totalElements;
	page = this.historyService.page;
	size = this.historyService.size;
	sortField = this.historyService.sortField;
	sortDirection = this.historyService.sortDirection;

	filterStartDate = signal<string>('');
	filterEndDate = signal<string>('');

	config = computed(() => RIDE_HISTORY_CONFIGS[this.userRole()]);

	pageSizeOptions = PAGE_SIZE_OPTIONS;
	sortOptions = computed(() => SORT_OPTIONS_BY_ROLE[this.userRole()]);

	showFilters = signal(false);

	protected readonly RideStatus = RideStatus;
	protected readonly Math = Math;

	ngOnInit() {
		this.loadRides();
	}

	loadRides() {
		this.historyService.getHistory();
	}

	onRideClick(ride: Ride) {
		if (this.config().canViewDetails) {
			this.historyService.selectedRide.set(ride);
			this.router.navigate([ride.id], { relativeTo: this.route }).then();
		}
	}

	onReorderNow(ride: Ride) {
		console.log('Reorder ride:', ride);
	}

	// Pagination
	nextPage() {
		if ((this.page() + 1) * this.size() < this.totalElements()) {
			this.historyService.setPage(this.page() + 1);
		}
	}

	previousPage() {
		if (this.page() > 0) {
			this.historyService.setPage(this.page() - 1);
		}
	}

	onPageSizeChange(newSize: number) {
		this.historyService.setPageSize(newSize);
	}

	// Sorting
	onSortFieldChange(field: SortField) {
		this.historyService.setSortField(field);
	}

	toggleSortDirection() {
		this.historyService.toggleSortDirection();
	}

	// Filtering
	toggleFilters() {
		this.showFilters.set(!this.showFilters());
	}

	applyFilters() {
		const startValue = this.filterStartDate();
		const endValue = this.filterEndDate();

		const start = startValue ? startValue + 'T00:00:00' : null;
		const end = endValue ? endValue + 'T23:59:59' : null;

		this.historyService.setDateRange(start, end);
	}

	clearFilters() {
		this.filterStartDate.set('');
		this.filterEndDate.set('');
		this.historyService.clearFilters();
	}

	// Helper methods
	getStatusClass(status: RideStatus): string {
		const statusClasses = {
			[RideStatus.PENDING]: 'status-pending',
			[RideStatus.ACCEPTED]: 'status-accepted',
			[RideStatus.ACTIVE]: 'status-active',
			[RideStatus.FINISHED]: 'status-finished',
			[RideStatus.INTERRUPTED]: 'status-interrupted',
			[RideStatus.CANCELLED]: 'status-cancelled',
			[RideStatus.PANICKED]: 'status-panicked',
		};
		return statusClasses[status];
	}

	getStatusText(status: RideStatus): string {
		const statusTexts = {
			[RideStatus.PENDING]: 'Pending',
			[RideStatus.ACCEPTED]: 'Accepted',
			[RideStatus.ACTIVE]: 'Active',
			[RideStatus.FINISHED]: 'Finished',
			[RideStatus.INTERRUPTED]: 'Interrupted',
			[RideStatus.CANCELLED]: 'Cancelled',
			[RideStatus.PANICKED]: 'Panicked',
		};
		return statusTexts[status];
	}

	formatDate(date: Date | string): string {
		const d = new Date(date);
		return d.toLocaleString('sr-RS', {
			year: 'numeric',
			month: '2-digit',
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit',
		});
	}
}
