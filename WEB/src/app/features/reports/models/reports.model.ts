export interface DailyRideStats {
	date: string;
	numberOfRides: number;
	totalDistance: number;
	totalAmount: number;
}

export interface RideReportDTO {
	dailyStats: DailyRideStats[];
	totalRides: number;
	totalDistance: number;
	totalAmount: number;
	averageRidesPerDay: number;
	averageDistancePerDay: number;
	averageAmountPerDay: number;
	averageDistancePerRide: number;
	averageAmountPerRide: number;
}

export interface AggregatedUserReportDTO {
	userId: number;
	userName: string;
	userEmail: string;
	report: RideReportDTO;
}

export interface AggregatedReportDTO {
	userReports: AggregatedUserReportDTO[];
	combinedStats: RideReportDTO;
}

export type UserRole = 'ADMIN' | 'DRIVER' | 'CUSTOMER';
