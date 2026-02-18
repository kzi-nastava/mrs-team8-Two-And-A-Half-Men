import { Component, computed, effect, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BoxDirective } from '@shared/directives/box/box.directive';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '@core/services/auth.service';
import { AggregatedReportDTO, RideReportDTO } from '@features/reports/models/reports.model';
import { Chart, registerables } from 'chart.js';
import { environment } from '@environments/environment';

Chart.register(...registerables);

@Component({
	selector: 'app-report-page',
	imports: [CommonModule, FormsModule, BoxDirective],
	templateUrl: './report-page.component.html',
	styleUrl: './report-page.component.css',
})
export class ReportPageComponent implements OnInit, OnDestroy {
	private http = inject(HttpClient);
	private authService = inject(AuthService);

	// Signals
	currentUser = this.authService.user;
	startDate = signal<string>(this.getDefaultStartDate());
	endDate = signal<string>(this.getDefaultEndDate());
	selectedUserType = signal<'DRIVER' | 'PASSENGER'>('DRIVER');
	selectedUserId = signal<number | null>(null);
	loading = signal<boolean>(false);
	error = signal<string | null>(null);

	// Report data
	myReport = signal<RideReportDTO | null>(null);
	aggregatedReport = signal<AggregatedReportDTO | null>(null);

	// Charts
	private ridesChart: Chart | null = null;
	private distanceChart: Chart | null = null;
	private amountChart: Chart | null = null;

	// Computed values
	isAdmin = computed(() => this.currentUser()?.role === 'ADMIN');

	userTypeLabel = computed(() => (this.currentUser()?.role === 'DRIVER' ? 'earned' : 'spent'));

	constructor() {
		// Autoload data when dates change
		effect(() => {
			const start = this.startDate();
			const end = this.endDate();
			if (start && end && this.currentUser()) {
				this.loadData();
			}
		});
	}

	ngOnInit() {
		this.loadData();
	}

	ngOnDestroy() {
		this.destroyCharts();
	}

	private getDefaultStartDate(): string {
		const date = new Date();
		date.setDate(1); // First day of current month
		return date.toISOString().split('T')[0];
	}

	private getDefaultEndDate(): string {
		return new Date().toISOString().split('T')[0];
	}

	loadData() {
		if (!this.currentUser()) return;

		this.loading.set(true);
		this.error.set(null);

		if (this.isAdmin()) {
			this.loadAggregatedReport();
		} else {
			this.loadMyReport();
		}
	}

	private loadMyReport() {
		const url = `/api/${environment.apiVersion}/reports/rides/my-report`;
		const params = {
			startDate: this.startDate(),
			endDate: this.endDate(),
		};

		this.http.get<RideReportDTO>(url, { params }).subscribe({
			next: (data) => {
				this.myReport.set(data);
				this.loading.set(false);
				setTimeout(() => this.renderCharts(data), 100);
			},
			error: (err) => {
				this.error.set('Failed to load report data');
				this.loading.set(false);
				console.error('Error loading report:', err);
			},
		});
	}

	private loadAggregatedReport() {
		const url = `/api/${environment.apiVersion}/reports/rides/aggregated`;
		const params = {
			startDate: this.startDate(),
			endDate: this.endDate(),
			userType: this.selectedUserType(),
		};

		this.http.get<AggregatedReportDTO>(url, { params }).subscribe({
			next: (data) => {
				this.aggregatedReport.set(data);
				this.loading.set(false);
				setTimeout(() => this.renderCharts(data.combinedStats), 100);
			},
			error: (err) => {
				this.error.set('Failed to load aggregated report data');
				this.loading.set(false);
				console.error('Error loading aggregated report:', err);
			},
		});
	}

	loadSpecificUserReport(userId: number) {
		this.loading.set(true);
		this.error.set(null);

		const url = `/api/${environment.apiVersion}/reports/rides/user/${userId}`;
		const params = {
			startDate: this.startDate(),
			endDate: this.endDate(),
		};

		this.http.get<RideReportDTO>(url, { params }).subscribe({
			next: (data) => {
				this.myReport.set(data);
				this.selectedUserId.set(userId);
				this.loading.set(false);
				setTimeout(() => this.renderCharts(data), 100);
			},
			error: (err) => {
				this.error.set('Failed to load user report data');
				this.loading.set(false);
				console.error('Error loading user report:', err);
			},
		});
	}

	viewCombinedStats() {
		this.selectedUserId.set(null);
		const data = this.aggregatedReport()?.combinedStats;
		if (data) {
			setTimeout(() => this.renderCharts(data), 100);
		}
	}

	private renderCharts(data: RideReportDTO) {
		this.destroyCharts();

		const labels = data.dailyStats.map((stat) => this.formatDate(stat.date));
		const ridesData = data.dailyStats.map((stat) => stat.numberOfRides);
		const distanceData = data.dailyStats.map((stat) => stat.totalDistance);
		const amountData = data.dailyStats.map((stat) => stat.totalAmount);

		// Rides Chart
		const ridesCanvas = document.getElementById('ridesChart') as HTMLCanvasElement;
		if (ridesCanvas) {
			this.ridesChart = new Chart(ridesCanvas, {
				type: 'bar',
				data: {
					labels: labels,
					datasets: [
						{
							label: 'Number of Rides',
							data: ridesData,
							backgroundColor: '#D4AF37',
							borderColor: '#D4AF37',
							borderWidth: 1,
						},
					],
				},
				options: {
					responsive: true,
					maintainAspectRatio: false,
					plugins: {
						legend: {
							labels: {
								color: '#FFFFFF',
							},
						},
					},
					scales: {
						y: {
							beginAtZero: true,
							ticks: {
								color: '#FFFFFF',
								precision: 0,
							},
							grid: {
								color: 'rgba(255, 255, 255, 0.1)',
							},
						},
						x: {
							ticks: {
								color: '#FFFFFF',
							},
							grid: {
								color: 'rgba(255, 255, 255, 0.1)',
							},
						},
					},
				},
			});
		}

		// Distance Chart
		const distanceCanvas = document.getElementById('distanceChart') as HTMLCanvasElement;
		if (distanceCanvas) {
			this.distanceChart = new Chart(distanceCanvas, {
				type: 'line',
				data: {
					labels: labels,
					datasets: [
						{
							label: 'Distance (km)',
							data: distanceData,
							backgroundColor: 'rgba(212, 175, 55, 0.2)',
							borderColor: '#D4AF37',
							borderWidth: 2,
							fill: true,
							tension: 0.4,
						},
					],
				},
				options: {
					responsive: true,
					maintainAspectRatio: false,
					plugins: {
						legend: {
							labels: {
								color: '#FFFFFF',
							},
						},
					},
					scales: {
						y: {
							beginAtZero: true,
							ticks: {
								color: '#FFFFFF',
							},
							grid: {
								color: 'rgba(255, 255, 255, 0.1)',
							},
						},
						x: {
							ticks: {
								color: '#FFFFFF',
							},
							grid: {
								color: 'rgba(255, 255, 255, 0.1)',
							},
						},
					},
				},
			});
		}

		// Amount Chart
		const amountCanvas = document.getElementById('amountChart') as HTMLCanvasElement;
		if (amountCanvas) {
			this.amountChart = new Chart(amountCanvas, {
				type: 'line',
				data: {
					labels: labels,
					datasets: [
						{
							label: `Amount ${this.userTypeLabel()} (RSD)`,
							data: amountData,
							backgroundColor: 'rgba(46, 125, 50, 0.2)',
							borderColor: '#2E7D32',
							borderWidth: 2,
							fill: true,
							tension: 0.4,
						},
					],
				},
				options: {
					responsive: true,
					maintainAspectRatio: false,
					plugins: {
						legend: {
							labels: {
								color: '#FFFFFF',
							},
						},
					},
					scales: {
						y: {
							beginAtZero: true,
							ticks: {
								color: '#FFFFFF',
							},
							grid: {
								color: 'rgba(255, 255, 255, 0.1)',
							},
						},
						x: {
							ticks: {
								color: '#FFFFFF',
							},
							grid: {
								color: 'rgba(255, 255, 255, 0.1)',
							},
						},
					},
				},
			});
		}
	}

	private destroyCharts() {
		if (this.ridesChart) {
			this.ridesChart.destroy();
			this.ridesChart = null;
		}
		if (this.distanceChart) {
			this.distanceChart.destroy();
			this.distanceChart = null;
		}
		if (this.amountChart) {
			this.amountChart.destroy();
			this.amountChart = null;
		}
	}

	private formatDate(dateStr: string): string {
		const date = new Date(dateStr);
		return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
	}

	setDateRange(range: 'week' | 'month' | 'quarter' | 'year') {
		const end = new Date();
		const start = new Date();

		switch (range) {
			case 'week':
				start.setDate(end.getDate() - 7);
				break;
			case 'month':
				start.setMonth(end.getMonth() - 1);
				break;
			case 'quarter':
				start.setMonth(end.getMonth() - 3);
				break;
			case 'year':
				start.setFullYear(end.getFullYear() - 1);
				break;
		}

		this.startDate.set(start.toISOString().split('T')[0]);
		this.endDate.set(end.toISOString().split('T')[0]);
	}

	onUserTypeChange() {
		this.selectedUserId.set(null);
		this.loadData();
	}

	getCurrentReportData(): RideReportDTO | null {
		if (this.selectedUserId() && this.aggregatedReport()) {
			const userReport = this.aggregatedReport()!.userReports.find(
				(r) => r.userId === this.selectedUserId(),
			);
			return userReport?.report || null;
		}

		if (this.isAdmin() && !this.selectedUserId()) {
			return this.aggregatedReport()?.combinedStats || null;
		}

		return this.myReport();
	}
}
