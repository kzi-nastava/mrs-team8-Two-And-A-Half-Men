export interface LoggedInUser {
	id: number;
	role: string;
	firstName: string;
	lastName: string;
	email: string;
	imgSrc: string | null;
}

export enum LoggedInUserRole {
	ADMIN = 'ADMIN',
	DRIVER = 'DRIVER',
	CUSTOMER = 'CUSTOMER',
}
