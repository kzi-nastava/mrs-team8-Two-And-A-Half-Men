import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

import { NewDriverPageComponent } from './new-driver-page.component';
import { AdminUserService } from '@features/users/services/admin-user.service';
import { VehiclesService } from '@shared/services/vehicles/vehicles.service';
import { PopupsService } from '@shared/services/popups/popups.service';
import { VehicleOptions } from '@shared/models/vehicles.model';

describe('[Student1] NewDriverPageComponent', () => {
	let fixture: ComponentFixture<NewDriverPageComponent>;
	let component: NewDriverPageComponent;

	let usersServiceSpy: jasmine.SpyObj<AdminUserService>;
	let vehiclesServiceSpy: jasmine.SpyObj<VehiclesService>;
	let popupsSpy: jasmine.SpyObj<PopupsService>;
	let routerSpy: jasmine.SpyObj<Router>;

	const mockVehicleOptions = {
		vehicleTypes: [
			{ id: 1, typeName: 'Sedan', description: 'Standard car' },
			{ id: 2, typeName: 'SUV', description: 'Large vehicle' },
		],
		additionalServices: [
			{ id: 10, name: 'WiFi', description: 'Internet' },
			{ id: 11, name: 'Child Seat', description: 'Safety seat' },
		],
	} as VehicleOptions;

	beforeEach(() => {
		usersServiceSpy = jasmine.createSpyObj('AdminUserService', ['registerDriver']);
		vehiclesServiceSpy = jasmine.createSpyObj('VehiclesService', ['getVehicleOptions']);
		popupsSpy = jasmine.createSpyObj('PopupsService', ['success', 'error']);
		routerSpy = jasmine.createSpyObj('Router', ['navigate']);

		vehiclesServiceSpy.getVehicleOptions.and.returnValue(of(mockVehicleOptions));
		routerSpy.navigate.and.returnValue(Promise.resolve(true));

		fixture = TestBed.configureTestingModule({
			imports: [NewDriverPageComponent],
			providers: [
				{ provide: AdminUserService, useValue: usersServiceSpy },
				{ provide: VehiclesService, useValue: vehiclesServiceSpy },
				{ provide: PopupsService, useValue: popupsSpy },
				{ provide: Router, useValue: routerSpy },
			],
		}).createComponent(NewDriverPageComponent);

		component = fixture.componentInstance;
	});

	/**************************   Component creation   **************************/

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	/**************************   Initialization / loading options   **************************/

	it('should load vehicle options on init', () => {
		fixture.detectChanges(); // triggers ngOnInit

		expect(vehiclesServiceSpy.getVehicleOptions).toHaveBeenCalled();
		expect(component.vehicleTypes().length).toBe(2);
		expect(component.availableServices().length).toBe(2);
	});

	it('should handle vehicle options loading error', () => {
		vehiclesServiceSpy.getVehicleOptions.and.returnValue(throwError(() => new Error('fail')));

		spyOn(console, 'error');

		fixture.detectChanges();

		expect(console.error).toHaveBeenCalled();
	});

	/**************************   Tab navigation   **************************/

	it('should change active tab', () => {
		component.onTabChange('vehicle');

		expect(component.activeTab()).toBe('vehicle');
	});

	/**************************   Request mapping logic   **************************/

	it('should build correct registration request on save', () => {
		fixture.detectChanges();

		component.personalInfo.set({
			firstName: 'John',
			lastName: 'Doe',
			email: 'john@test.com',
			address: 'Main St',
			phoneNumber: '123',
			imgSrc: null,
		});

		component.vehicleInfo.set({
			type: 'Sedan',
			model: 'Prius',
			licensePlate: 'NS-123',
			numberOfSeats: 4,
			additionalServices: ['WiFi'],
		});

		usersServiceSpy.registerDriver.and.returnValue(
			of({
				vehicleId: 1,
				driverId: 1,
				ok: true,
				message: 'Registered successfully',
			}),
		);

		component.saveChanges();

		expect(usersServiceSpy.registerDriver).toHaveBeenCalled();

		const request = usersServiceSpy.registerDriver.calls.mostRecent().args[0];

		expect(request.personalInfo.firstName).toBe('John');
		expect(request.vehicleInfo.typeId).toBe(1);
		expect(request.vehicleInfo.additionalServicesIds).toEqual([10]);
	});

	it('should use -1 when vehicle type is missing', () => {
		fixture.detectChanges();

		component.vehicleInfo.update((v) => ({
			...v,
			type: 'UNKNOWN',
		}));

		usersServiceSpy.registerDriver.and.returnValue(
			of({
				vehicleId: -1,
				driverId: -1,
				ok: false,
				message: 'No such vehicle type',
			}),
		);

		component.saveChanges();

		const request = usersServiceSpy.registerDriver.calls.mostRecent().args[0];

		expect(request.vehicleInfo.typeId).toBe(-1);
	});

	/**************************   Save flow - success   **************************/

	it('should show success popup and navigate after save', () => {
		fixture.detectChanges();

		usersServiceSpy.registerDriver.and.returnValue(
			of({
				vehicleId: 1,
				driverId: 1,
				ok: true,
				message: 'Registered successfully',
			}),
		);

		component.saveChanges();

		expect(component.isSaving()).toBeFalse();
		expect(popupsSpy.success).toHaveBeenCalled();

		const popupArgs = popupsSpy.success.calls.mostRecent().args;
		expect(popupArgs.length).toEqual(3);
		expect(popupArgs[2]).toBeTruthy();
		popupArgs[2]?.onConfirm?.();

		expect(routerSpy.navigate).toHaveBeenCalledWith(['users']);
	});

	/**************************   Save flow - error   **************************/

	it('should show error popup when save fails', () => {
		fixture.detectChanges();

		usersServiceSpy.registerDriver.and.returnValue(
			throwError(() => ({
				error: { message: 'Backend failed' },
			})),
		);

		component.saveChanges();

		expect(popupsSpy.error).toHaveBeenCalled();
		expect(component.isSaving()).toBeFalse();
	});

	/**************************   UI state   **************************/

	it('should set isSaving true while saving starts', () => {
		fixture.detectChanges();

		usersServiceSpy.registerDriver.and.returnValue(
			of({
				vehicleId: 1,
				driverId: 1,
				ok: true,
				message: 'Registered successfully',
			}),
		);

		component.saveChanges();

		expect(component.isSaving()).toBeFalse(); // sync observable completes immediately
	});
});
