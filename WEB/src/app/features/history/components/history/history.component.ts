import { Component, inject, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
	RIDE_HISTORY_CONFIGS,
	PAGE_SIZE_OPTIONS,
	SORT_OPTIONS_BY_ROLE,
	SortField,
} from '@shared/models/ride-config';
import { HistoryService } from '@features/history/services/history.service';
import { LoggedInUserRole } from '@core/models/loggedInUser.model';
import { Ride } from '@shared/models/ride.model';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { RidesListComponent, RidesListConfig } from '@shared/components/rides/ride-list/ride-list.component';

@Component({
	selector: 'app-history',
	standalone: true,
	imports: [CommonModule, FormsModule, RidesListComponent],
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
	filterDriverId = signal<string>('');
	filterCustomerId = signal<string>('');

	config = computed(() => RIDE_HISTORY_CONFIGS[this.userRole()]);

	// Create a config object for the rides list component
	ridesListConfig = computed<RidesListConfig>(() => ({
		canViewDetails: this.config().canViewDetails,
		showDriverInfo: this.config().showDriverInfo,
		showPassengerInfo: this.config().showPassengerInfo,
		showPanicButton: this.config().showPanicButton,
		showReorderOption: this.config().showReorderOption,
	}));

	pageSizeOptions = PAGE_SIZE_OPTIONS;
	sortOptions = computed(() => SORT_OPTIONS_BY_ROLE[this.userRole()]);

	showFilters = signal(false);

	protected readonly Math = Math;

	ngOnInit() {
		this.loadRides();
	}

	loadRides() {
		this.historyService.getHistory();
	}

	onRideClick(ride: Ride) {
		this.historyService.selectedRide.set(ride);
		this.router.navigate([ride.id], { relativeTo: this.route }).then();
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

		// Apply user filters if available
		if (this.config().showUserFilters) {
			const driverId = this.filterDriverId() ? +this.filterDriverId() : null;
			const customerId = this.filterCustomerId() ? +this.filterCustomerId() : null;
			this.historyService.setUserFilters(driverId, customerId);
		}
	}

	clearFilters() {
		this.filterStartDate.set('');
		this.filterEndDate.set('');
		this.filterDriverId.set('');
		this.filterCustomerId.set('');
		this.historyService.clearFilters();
	}
}
