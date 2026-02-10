import { LoggedInUserRole } from '@core/models/loggedInUser.model';

export interface RideHistoryConfig {
	showDriverInfo: boolean;
	showPassengerInfo: boolean;
	showPanicButton: boolean;
	showInconsistencyReports: boolean;
	showRatings: boolean;
	showReorderOption: boolean;
	canViewDetails: boolean;
	showUserFilters: boolean;
}

export const RIDE_HISTORY_CONFIGS: Record<LoggedInUserRole, RideHistoryConfig> = {
	[LoggedInUserRole.CUSTOMER]: {
		showDriverInfo: true,
		showPassengerInfo: false,
		showPanicButton: false,
		showInconsistencyReports: true,
		showRatings: true,
		showReorderOption: true,
		canViewDetails: true,
		showUserFilters: false,
	},
	[LoggedInUserRole.DRIVER]: {
		showDriverInfo: false,
		showPassengerInfo: true,
		showPanicButton: true,
		showInconsistencyReports: false,
		showRatings: false,
		showReorderOption: false,
		canViewDetails: true,
		showUserFilters: false,
	},
	[LoggedInUserRole.ADMIN]: {
		showDriverInfo: true,
		showPassengerInfo: true,
		showPanicButton: true,
		showInconsistencyReports: true,
		showRatings: true,
		showReorderOption: true,
		canViewDetails: true,
		showUserFilters: true,
	},
};

export const PAGE_SIZE_OPTIONS = [5, 10, 20, 50] as const;

export type PageSizeOption = (typeof PAGE_SIZE_OPTIONS)[number];

export type SortField = 'scheduledTime' | 'startTime' | 'endTime' | 'totalCost' | 'status';
export type SortDirection = 'ASC' | 'DESC';

export interface SortOption {
	field: SortField;
	label: string;
}

export const SORT_OPTIONS: SortOption[] = [
	{ field: 'scheduledTime', label: 'Scheduled Time' },
	{ field: 'startTime', label: 'Start Time' },
	{ field: 'endTime', label: 'End Time' },
	{ field: 'totalCost', label: 'Total Cost' },
	{ field: 'status', label: 'Status' },
];

export const SORT_OPTIONS_BY_ROLE: Record<LoggedInUserRole, SortOption[]> = {
	[LoggedInUserRole.CUSTOMER]: [
		{ field: 'scheduledTime', label: 'Scheduled Time' },
		{ field: 'startTime', label: 'Start Time' },
		{ field: 'totalCost', label: 'Total Cost' },
	],
	[LoggedInUserRole.DRIVER]: [
		{ field: 'scheduledTime', label: 'Scheduled Time' },
		{ field: 'startTime', label: 'Start Time' },
		{ field: 'endTime', label: 'End Time' },
	],
	[LoggedInUserRole.ADMIN]: SORT_OPTIONS,
};
