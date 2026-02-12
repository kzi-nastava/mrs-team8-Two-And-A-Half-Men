export interface User {
	id: number;
	email: string;
	firstName: string;
	lastName: string;
	role: 'ADMIN' | 'CUSTOMER' | 'DRIVER';
	isBlocked: boolean;
	driverStatus: 'BUSY' | 'INACTIVE' | 'AVAILABLE' | null;
	hasPendingRequests: boolean | null;
}

export interface PageableSort {
	empty: boolean;
	sorted: boolean;
	unsorted: boolean;
}

export interface Pageable {
	offset: number;
	pageNumber: number;
	pageSize: number;
	paged: boolean;
	sort: PageableSort;
	unpaged: boolean;
}

export interface UserPageResponse {
	content: User[];
	empty: boolean;
	first: boolean;
	last: boolean;
	number: number;
	numberOfElements: number;
	pageable: Pageable;
	size: number;
	sort: PageableSort;
	totalElements: number;
	totalPages: number;
}

export interface UserFilters {
	email?: string;
	firstName?: string;
	lastName?: string;
	role?: string;
	isBlocked?: boolean | null;
	driverStatus?: string;
	hasPendingRequests?: boolean | null;
}

/* User details */

export interface PersonalInfo {
	id: number;
	firstName: string;
	lastName: string;
	email: string;
	phoneNumber: string;
	address: string;
	role: 'ADMIN' | 'CUSTOMER' | 'DRIVER';
	blocked: boolean;
	blockReason: string | null;
	imgSrc: string | null;
}

export interface VehicleInfo {
	id: number;
	type: string;
	model: string;
	licensePlate: string;
	numberOfSeats: number;
	additionalServices: string[];
}

export interface PendingChangeRequest {
	id: number;
	firstName: string;
	lastName: string;
	email: string;
	phoneNumber: string;
	address: string;
	imgSrc: string | null;
	vehicleType: string;
	model: string;
	licensePlate: string;
	numberOfSeats: number;
	additionalServices: string[];
}

export interface UserDetailResponse {
	personalInfo: PersonalInfo;
	vehicleInfo?: VehicleInfo;
	pendingChangeRequest?: PendingChangeRequest;
}
