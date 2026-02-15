import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { UnregisteredAuthService } from './unregistered-auth.service';
import { AuthService } from '@core/services/auth.service';
import { environment } from '@environments/environment';
import { User } from '@features/unregistered/models/auth.model';

describe('[Student3] UnregisteredAuthService', () => {
	let service: UnregisteredAuthService;
	let httpMock: HttpTestingController;
	let authServiceMock: jasmine.SpyObj<AuthService>;
	beforeEach(() => {
		authServiceMock = jasmine.createSpyObj('AuthService', ['saveLogin']);
		TestBed.configureTestingModule({
			imports: [provideHttpClientTesting()],
			providers: [
				UnregisteredAuthService,
				{ provide: AuthService, useValue: authServiceMock },
			],
		});
		service = TestBed.inject(UnregisteredAuthService);
		httpMock = TestBed.inject(HttpTestingController);
	});

	afterEach(() => {
		httpMock.verify();
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	describe('register()', () => {
		it('should send POST request to register endpoint with user data', fakeAsync(() => {
			let response: { message: string } | undefined;
			// Happy path
			const user: User = {
				firstName: 'Customer',
				lastName: 'customer',
				phone: '+1234567890',
				address: '123 Main St',
				email: 'customer@test.com',
				password: 'password',
			};
			const mockResponse = { message: 'User registered successfully check your email' };

			service.register(user).subscribe((res) => {
				response = res;
			});

			const req = httpMock.expectOne(`/api/${environment.apiVersion}/users/register`);
			expect(req.request.method).toBe('POST');
			expect(req.request.body).toEqual(user);
			req.flush(mockResponse);

			tick();

			expect(response).toBeDefined();
			expect(response!.message).toBe('User registered successfully check your email');
		}));

		it('should handle error when email already exists', fakeAsync(() => {
			let errorResponse: any;

			const user: User = {
				firstName: 'Customer',
				lastName: 'customer',
				phone: '+1234567890',
				address: '123 Main St',
				email: '  customer@test.com',
				password: 'password',
			};

			service.register(user).subscribe({
				next: () => fail('should have failed'),
				error: (error) => {
					errorResponse = error;
				},
			});

			const req = httpMock.expectOne(`/api/${environment.apiVersion}/users/register`);
			req.flush(
				{ error: 'Email already in use' },
				{ status: 401, statusText: 'Unauthorized' },
			);
			tick();

			expect(errorResponse).toBeDefined();
			expect(errorResponse.status).toBe(401);
			expect(errorResponse.error.error).toBe('Email already in use');
		}));

		it('should handle validation error for invalid data', fakeAsync(() => {
			let errorResponse: any;

			const user: User = {
				firstName: '',
				lastName: '',
				phone: 'invalid',
				address: '',
				email: 'invalid-email',
				password: '123',
			};

			service.register(user).subscribe({
				next: () => fail('should have failed'),
				error: (error) => {
					errorResponse = error;
				},
			});

			const req = httpMock.expectOne(`/api/${environment.apiVersion}/users/register`);
			req.flush({ error: 'Validation failed' }, { status: 401, statusText: 'Unauthorized' });

			tick();

			expect(errorResponse).toBeDefined();
			expect(errorResponse.status).toBe(401);
		}));

		it('should handle server error during registration', fakeAsync(() => {
			let errorResponse: any;

			const user: User = {
				firstName: 'Customer',
				lastName: 'customer',
				phone: '+1234567890',
				address: 'Dragoseva 1/12',
				email: 'customer@test.com',
				password: 'password',
			};

			service.register(user).subscribe({
				next: () => fail('should have failed'),
				error: (error) => {
					errorResponse = error;
				},
			});

			const req = httpMock.expectOne(`/api/${environment.apiVersion}/users/register`);
			req.flush(
				{ error: 'Internal server error' },
				{ status: 500, statusText: 'Internal Server Error' },
			);

			tick();

			expect(errorResponse).toBeDefined();
			expect(errorResponse.status).toBe(500);
		}));
	});
});
