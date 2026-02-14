import { TestBed } from '@angular/core/testing';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';

import { AdminUserService } from './admin-user.service';
import { environment } from '@environments/environment';

describe('[Student1] AdminUserService', () => {
	let service: AdminUserService;
	let httpMock: HttpTestingController;

	const baseUrl = `/api/${environment.apiVersion}`;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [provideHttpClientTesting()],
		});

		service = TestBed.inject(AdminUserService);
		httpMock = TestBed.inject(HttpTestingController);
	});

	afterEach(() => {
		httpMock.verify();
	});

	/**************************   Service creation   **************************/

	it('should create', () => {
		expect(service).toBeTruthy();
	});

	/**************************   registerDriver   **************************/

	it('should POST register driver request', () => {
		const payload = {
			personalInfo: { firstName: 'John' },
			vehicleInfo: { model: 'Prius' },
		} as any;

		service.registerDriver(payload).subscribe();

		const req = httpMock.expectOne(`${baseUrl}/register/drivers`);

		expect(req.request.method).toBe('POST');
		expect(req.request.body).toEqual(payload);

		req.flush({});
	});

	/**************************   getUsers - basic paging   **************************/

	it('should GET users with default paging params', () => {
		service.getUsers().subscribe();

		const req = httpMock.expectOne((r) => r.url === `${baseUrl}/users`);

		expect(req.request.method).toBe('GET');
		expect(req.request.params.get('page')).toBe('0');
		expect(req.request.params.get('size')).toBe('10');

		req.flush({});
	});

	/**************************   getUsers - sorting   **************************/

	it('should include sorting params when provided', () => {
		service.getUsers(1, 20, 'email', 'DESC').subscribe();

		const req = httpMock.expectOne((r) => r.url === `${baseUrl}/users`);

		expect(req.request.params.get('page')).toBe('1');
		expect(req.request.params.get('size')).toBe('20');
		expect(req.request.params.get('sortBy')).toBe('email');
		expect(req.request.params.get('sortDirection')).toBe('DESC');

		req.flush({});
	});

	/**************************   getUsers - filters   **************************/

	it('should include non-empty filters', () => {
		service
			.getUsers(0, 10, undefined, 'ASC', {
				role: 'DRIVER',
				isBlocked: true,
				email: '',
				firstName: null as any,
			})
			.subscribe();

		const req = httpMock.expectOne((r) => r.url === `${baseUrl}/users`);

		expect(req.request.params.get('role')).toBe('DRIVER');
		expect(req.request.params.get('isBlocked')).toBe('true');

		// should NOT include empty values
		expect(req.request.params.has('email')).toBeFalse();
		expect(req.request.params.has('firstName')).toBeFalse();

		req.flush({});
	});

	/**************************   getUserDetails   **************************/

	it('should GET user details', () => {
		service.getUserDetails(42).subscribe();

		const req = httpMock.expectOne(`${baseUrl}/users/42`);

		expect(req.request.method).toBe('GET');

		req.flush({});
	});

	/**************************   blockUser   **************************/

	it('should PATCH block user with reason', () => {
		service.blockUser(5, 'Violation').subscribe();

		const req = httpMock.expectOne(`${baseUrl}/users/5/block`);

		expect(req.request.method).toBe('PATCH');
		expect(req.request.body).toEqual({ reason: 'Violation' });

		req.flush({});
	});

	/**************************   unblockUser   **************************/

	it('should PATCH unblock user', () => {
		service.unblockUser(5).subscribe();

		const req = httpMock.expectOne(`${baseUrl}/users/5/unblock`);

		expect(req.request.method).toBe('PATCH');
		expect(req.request.body).toEqual({});

		req.flush({});
	});

	/**************************   approveChangeRequest   **************************/

	it('should POST approve change request', () => {
		service.approveChangeRequest(7).subscribe();

		const req = httpMock.expectOne(`${baseUrl}/profile-update-requests/7/approve`);

		expect(req.request.method).toBe('POST');
		expect(req.request.body).toEqual({});

		req.flush({});
	});

	/**************************   rejectChangeRequest   **************************/

	it('should POST reject change request', () => {
		service.rejectChangeRequest(8).subscribe();

		const req = httpMock.expectOne(`${baseUrl}/profile-update-requests/8/reject`);

		expect(req.request.method).toBe('POST');
		expect(req.request.body).toEqual({});

		req.flush({});
	});
});
