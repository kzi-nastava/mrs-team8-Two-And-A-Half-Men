import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ButtonDirective } from '@shared/directives/button/button.directive';
import { User, UserFilters, UserPageResponse } from '@features/admin/users/models/user.model';
import { AdminUserService } from '@features/admin/users/services/admin-user.service';
import { PopupsService } from '@shared/services/popups/popups.service';
import { BoxDirective } from '@shared/directives/box/box.directive';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
	selector: 'app-all-users-page',
	imports: [CommonModule, FormsModule, BoxDirective],
	providers: [AdminUserService],
	templateUrl: './all-users-page.component.html',
	styleUrl: './all-users-page.component.css',
})
export class AllUsersPageComponent {
	private usersService = inject(AdminUserService);
	private router = inject(Router);
	private popupsService = inject(PopupsService);

	users = signal<User[]>([]);
	isLoading = signal<boolean>(false);

	// Pagination
	currentPage = signal<number>(0);
	pageSize = signal<number>(10);
	totalElements = signal<number>(0);
	totalPages = signal<number>(0);
	pageSizeOptions = signal<number[]>([5, 10, 20, 50]);

	// Sorting
	sortColumn =  signal<string | null>(null);
	sortDirection = signal<'asc' | 'desc'>('asc');

	// Filters
	filters = signal<UserFilters>({});
	showFilters = signal<boolean>(false);

	// Role and status options
	roleOptions = ['ADMIN', 'CUSTOMER', 'DRIVER'];
	driverStatusOptions = ['BUSY', 'INACTIVE', 'AVAILABLE'];
	blockedOptions = [
		{ label: 'All', value: null },
		{ label: 'Blocked', value: true },
		{ label: 'Active', value: false },
	];
	pendingRequestsOptions = [
		{ label: 'All', value: null },
		{ label: 'Has Pending', value: true },
		{ label: 'No Pending', value: false },
	];

	// For template usage
	Math = Math;

	ngOnInit(): void {
		this.loadUsers();
	}

	loadUsers(): void {
		this.isLoading.set(true);

		this.usersService
			.getUsers(
				this.currentPage(),
				this.pageSize(),
				this.sortColumn() || undefined,
				this.sortDirection(),
				this.filters(),
			)
			.subscribe({
				next: (response: UserPageResponse) => {
					this.users.set(response.content);
					this.totalElements.set(response.totalElements);
					this.totalPages.set(response.totalPages);
					this.isLoading.set(false);
				},
				error: (err) => {
					this.popupsService.error('Error', 'Failed to load users. Please try again.');
					this.isLoading.set(false);
					console.error('Error loading users:', err);
				},
			});
	}

	onSort(column: string): void {
		if (this.sortColumn() === column) {
			this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
		} else {
			this.sortColumn.set(column);
			this.sortDirection.set('asc');
		}
		this.currentPage.set(0);
		this.loadUsers();
	}

	getSortIcon(column: string): string {
		if (this.sortColumn() !== column) {
			return '↕️';
		}
		return this.sortDirection() === 'asc' ? '↑' : '↓';
	}

	onPageChange(page: number): void {
		if (page >= 0 && page < this.totalPages()) {
			this.currentPage.set(page);
			this.loadUsers();
		}
	}

	onPageSizeChange(): void {
		this.currentPage.set(0)
		this.loadUsers();
	}

	applyFilters(): void {
		this.currentPage.set(0)
		this.loadUsers();
	}

	clearFilters(): void {
		this.filters.set({});
		this.currentPage.set(0);
		this.loadUsers();
	}

	toggleFilters(): void {
		this.showFilters.update(current => !current);
	}

	viewUser(userId: number): void {
		this.router.navigate(['/admin/users', userId]).then();
	}

	createDriver(): void {
		this.router.navigate(['/admin/users/drivers/new']).then();
	}

	getPages(): number[] {
		const pages: number[] = [];
		const maxPagesToShow = 5;
		let startPage = Math.max(0, this.currentPage() - Math.floor(maxPagesToShow / 2));
		let endPage = Math.min(this.totalPages() - 1, startPage + maxPagesToShow - 1);

		if (endPage - startPage < maxPagesToShow - 1) {
			startPage = Math.max(0, endPage - maxPagesToShow + 1);
		}

		for (let i = startPage; i <= endPage; i++) {
			pages.push(i);
		}
		return pages;
	}

	getRoleBadgeClass(role: string): string {
		const classes: { [key: string]: string } = {
			ADMIN: 'badge-admin',
			CUSTOMER: 'badge-customer',
			DRIVER: 'badge-driver',
		};
		return classes[role] || '';
	}

	getStatusBadgeClass(status: string | null): string {
		if (!status) return '';
		const classes: { [key: string]: string } = {
			BUSY: 'badge-busy',
			INACTIVE: 'badge-inactive',
			AVAILABLE: 'badge-available',
		};
		return classes[status] || '';
	}
}
