import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { signal } from '@angular/core';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

import { RideDetailsComponent } from './ride-details-page.component';
import { RideService } from '@features/rides/services/ride.service';
import { PopupsService } from '@shared/services/popups/popups.service';
import { AuthService } from '@core/services/auth.service';
import { ConfigService } from '@features/rides/services/config.service';
import { RatingFormData } from '@shared/components/forms/rating-form/rating-form.component';
import { Ride, RideStatus } from '@shared/models/ride.model';

// ─── Helpers ──────────────────────────────────────────────────────────────────

const mockRide: Ride = {
	id: 42,
	routeId: 7,

	startTime: new Date('2025-01-01T10:00:00'),
	endTime: new Date('2025-01-01T10:30:00'),
	scheduledTime: new Date('2025-01-01T09:55:00'),

	driverName: 'Marko Marković',
	rideOwnerName: 'Ana Anić',
	rideOwnerId: 1,

	status: RideStatus.FINISHED,
	path: 'nusbdzifbvsdigfxnsficgknsdkj',

	cancellationReason: '',

	price: 800,
	totalCost: 950,

	additionalServices: [],

	locations: [
		{
			geoHash: 'u2e1x',
			address: 'Trg slobode 1, Novi Sad',
			latitude: 45.25,
			longitude: 19.83,
		},
		{
			geoHash: 'u2e1y',
			address: 'Bulevar Oslobođenja 10, Novi Sad',
			latitude: 45.26,
			longitude: 19.84,
		},
	],

	passengers: [
		{
			email: 'ana@test.com',
			driverRating: undefined,
			vehicleRating: undefined,
			comment: undefined,
			inconsistencyNote: undefined,
		},
	],

	favourite: false,
};

// ─── Stubs of service ──────────────────────────────────────────────────────────

const rideServiceStub = {
	getRide: jasmine.createSpy('getRide').and.returnValue(of(mockRide)),
	submitRating: jasmine.createSpy('submitRating').and.returnValue(of({})),
};

const popupServiceStub = {
	success: jasmine.createSpy('success'),
	error: jasmine.createSpy('error'),
	confirm: jasmine.createSpy('confirm'),
};

const authServiceStub = {
	user: signal({ role: 'CUSTOMER', email: 'ana@test.com' }),
};

const actionsConfigStub = {
	getActions: jasmine.createSpy('getActions').and.returnValue({
		rate: true,
	}),
};

const activatedRouteStub = {
	paramMap: of({ get: (key: string) => (key === 'id' ? '42' : null) }),
	snapshot: {
		queryParams: {},
	},
};

// ─── Test Suite ───────────────────────────────────────────────────────────────

describe('RideDetailsComponent — Rating form', () => {
	let component: RideDetailsComponent;
	let fixture: ComponentFixture<RideDetailsComponent>;
	let routerSpy: jasmine.SpyObj<Router>;
	let locationSpy: jasmine.SpyObj<Location>;

	beforeEach(async () => {
		routerSpy = jasmine.createSpyObj('Router', ['navigate']);
		locationSpy = jasmine.createSpyObj('Location', ['back']);

		// Reset spies before each test
		rideServiceStub.getRide.calls.reset();
		rideServiceStub.submitRating.calls.reset();
		popupServiceStub.success.calls.reset();
		popupServiceStub.error.calls.reset();
		actionsConfigStub.getActions.calls.reset();

		await TestBed.configureTestingModule({
			imports: [RideDetailsComponent],
			providers: [
				{ provide: ActivatedRoute, useValue: activatedRouteStub },
				{ provide: Router, useValue: routerSpy },
				{ provide: Location, useValue: locationSpy },
				{ provide: RideService, useValue: rideServiceStub },
				{ provide: PopupsService, useValue: popupServiceStub },
				{ provide: AuthService, useValue: authServiceStub },
				{ provide: ConfigService, useValue: actionsConfigStub },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(RideDetailsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	// ─── showRatingPopup signal ──────────────────────────────────────────────

	describe('showRatingPopup signal', () => {
		it('should be false in the beginning', () => {
			expect(component.showRatingPopup()).toBeFalse();
		});

		it('openRatingPopup() should set showRatingPopup on true', () => {
			component.openRatingPopup();
			expect(component.showRatingPopup()).toBeTrue();
		});

		it('closeRatingPopup() should set showRatingPopup on false', () => {
			component.openRatingPopup();
			component.closeRatingPopup();
			expect(component.showRatingPopup()).toBeFalse();
		});
	});

	// ─── queryParam view=rate ────────────────────────────────────────────────

	describe('Automatic opening of popup with query param view=rate', () => {
		it('should set showRatingPopup on true if view=rate query param is presented', async () => {
			const fixture2 = TestBed.createComponent(RideDetailsComponent);
			const component2 = fixture2.componentInstance;

			// Simulating ngOnInit handling queryParams
			(component2 as any).shouldOpenRatingPopup = true;
			(component2 as any).ride.set(mockRide);

			// Directly calling private logic
			(component2 as any).loadingDetails.set(false);
			component2.showRatingPopup.set(true);

			fixture2.detectChanges();
			expect(component2.showRatingPopup()).toBeTrue();
		});

		it('should leave showRatingPopup false if view query param is not presented', () => {
			// activatedRouteStub.snapshot.queryParams doesn't have 'view' key
			expect(component.showRatingPopup()).toBeFalse();
		});
	});

	// ─── onRatingSubmitted() ─────────────────────────────────────────────────

	describe('onRatingSubmitted()', () => {
		const ratingData: RatingFormData = {
			driverRating: 5,
			vehicleRating: 4,
			comment: 'Excellent driver',
		};

		beforeEach(() => {
			// Set ride directly before each test
			(component as any).ride.set(mockRide);
		});

		it('should close popup before calling service', () => {
			component.openRatingPopup();
			component.onRatingSubmitted(ratingData);
			expect(component.showRatingPopup()).toBeFalse();
		});

		it('should call rideService.submitRating with appropriate arguments', () => {
			rideServiceStub.submitRating.and.returnValue(of({}));
			component.onRatingSubmitted(ratingData);

			expect(rideServiceStub.submitRating).toHaveBeenCalledWith(
				mockRide.id,
				ratingData,
				null, // accessToken is null because there is no queryParam
			);
		});

		it('should show success popup when submit is successful', () => {
			rideServiceStub.submitRating.and.returnValue(of({}));
			component.onRatingSubmitted(ratingData);

			expect(popupServiceStub.success).toHaveBeenCalledWith(
				'Thank you!',
				'Your rating has been submitted.',
			);
		});

		it('should reload ride details after successful rating', () => {
			rideServiceStub.submitRating.and.returnValue(of({}));
			rideServiceStub.getRide.calls.reset();

			component.onRatingSubmitted(ratingData);

			// getRide should be recalled
			expect(rideServiceStub.getRide).toHaveBeenCalledWith(mockRide.id);
		});

		it('should show error popup when submitRating fails, with message from server', () => {
			const serverError = { error: { message: 'Ride is already rated.' } };
			rideServiceStub.submitRating.and.returnValue(throwError(() => serverError));

			component.onRatingSubmitted(ratingData);

			expect(popupServiceStub.error).toHaveBeenCalledWith(
				'Error',
				'Failed to submit rating. Ride is already rated.',
			);
		});

		it('should show fallback error message when there is no servers message', () => {
			rideServiceStub.submitRating.and.returnValue(throwError(() => ({})));

			component.onRatingSubmitted(ratingData);

			expect(popupServiceStub.error).toHaveBeenCalledWith(
				'Error',
				'Failed to submit rating. Please try again later.',
			);
		});

		it('should not call submitRating if ride() is not set', () => {
			(component as any).ride.set(null);
			rideServiceStub.submitRating.calls.reset();

			component.onRatingSubmitted(ratingData);

			expect(rideServiceStub.submitRating).not.toHaveBeenCalled();
		});

		it('should send accessToken if it is presented', () => {
			const token = 'test-access-token-123';
			(component as any).accessToken = token;
			rideServiceStub.submitRating.and.returnValue(of({}));

			component.onRatingSubmitted(ratingData);

			expect(rideServiceStub.submitRating).toHaveBeenCalledWith(
				mockRide.id,
				ratingData,
				token,
			);
		});
	});

	// ─── DOM — rating form popup ────────────────────────────────

	describe('DOM — show popup with rating form', () => {
		beforeEach(() => {
			(component as any).ride.set(mockRide);
			(component as any).loadingDetails.set(false);
			fixture.detectChanges();
		});

		it('popup-overlay should not be shown when showRatingPopup is false', () => {
			component.showRatingPopup.set(false);
			fixture.detectChanges();

			const overlay = fixture.debugElement.query(By.css('.popup-overlay'));
			expect(overlay).toBeNull();
		});

		it('popup-overlay should be presented when showRatingPopup is true', () => {
			component.showRatingPopup.set(true);
			fixture.detectChanges();

			const overlay = fixture.debugElement.query(By.css('.popup-overlay'));
			expect(overlay).not.toBeNull();
		});

		it('app-rating-form should be rendered inside popup-overlay', () => {
			component.showRatingPopup.set(true);
			fixture.detectChanges();

			const ratingForm = fixture.debugElement.query(By.css('app-rating-form'));
			expect(ratingForm).not.toBeNull();
		});

		it('click outside .popup-overlay should close popup', () => {
			component.showRatingPopup.set(true);
			fixture.detectChanges();

			const overlay = fixture.debugElement.query(By.css('.popup-overlay'));
			overlay.nativeElement.click();
			fixture.detectChanges();

			expect(component.showRatingPopup()).toBeFalse();
		});

		it('click inside .popup-content should not close popup (stopPropagation)', () => {
			component.showRatingPopup.set(true);
			fixture.detectChanges();

			const popupContent = fixture.debugElement.query(By.css('.popup-content'));
			const event = new MouseEvent('click', { bubbles: true });
			spyOn(event, 'stopPropagation').and.callThrough();
			popupContent.nativeElement.dispatchEvent(event);
			fixture.detectChanges();

			expect(component.showRatingPopup()).toBeTrue();
		});
	});

	// ─── DOM — Rate Ride button ───────────────────────────────────────────────

	describe('DOM — Rate Ride button', () => {
		beforeEach(() => {
			(component as any).ride.set(mockRide);
			(component as any).loadingDetails.set(false);
			actionsConfigStub.getActions.and.returnValue({
				favourite: false,
				rate: true,
				rebook: false,
				note: false,
				cancel: false,
				start: false,
				end: false,
				panic: false,
			});
			fixture.detectChanges();
		});

		it('Rate ride button should be shown for FINISHED ride', () => {
			(component as any).ride.set({ ...mockRide, status: RideStatus.FINISHED });
			fixture.detectChanges();

			const rateBtn = fixture.debugElement.query(By.css('.btn-rate'));
			expect(rateBtn).not.toBeNull();
		});

		it('Rate ride button should be shown for PANICKED ride', () => {
			(component as any).ride.set({ ...mockRide, status: RideStatus.PANICKED });
			fixture.detectChanges();

			const rateBtn = fixture.debugElement.query(By.css('.btn-rate'));
			expect(rateBtn).not.toBeNull();
		});

		it('Rate ride button should be shown for INTERRUPTED ride', () => {
			(component as any).ride.set({ ...mockRide, status: RideStatus.INTERRUPTED });
			fixture.detectChanges();

			const rateBtn = fixture.debugElement.query(By.css('.btn-rate'));
			expect(rateBtn).not.toBeNull();
		});

		it('Rate ride button should not be shown for PENDING ride', () => {
			(component as any).ride.set({ ...mockRide, status: RideStatus.PENDING });
			fixture.detectChanges();

			const rateBtn = fixture.debugElement.query(By.css('.btn-rate'));
			expect(rateBtn).toBeNull();
		});

		it('click on Rate ride button should open popup', () => {
			const rateBtn = fixture.debugElement.query(By.css('.btn-rate'));
			rateBtn.nativeElement.click();
			fixture.detectChanges();

			expect(component.showRatingPopup()).toBeTrue();
		});
	});
});
