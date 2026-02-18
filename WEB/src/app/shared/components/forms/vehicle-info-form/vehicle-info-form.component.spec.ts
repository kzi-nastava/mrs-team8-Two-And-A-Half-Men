import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { VehicleInfoFormComponent } from './vehicle-info-form.component';

describe('[Student1] VehicleInfoFormComponent', () => {
	let fixture: ComponentFixture<VehicleInfoFormComponent>;
	let component: VehicleInfoFormComponent;

	const mockVehicleInfo = {
		type: 'Sedan',
		model: 'Toyota Prius',
		licensePlate: 'NS-123-AB',
		numberOfSeats: 4,
		additionalServices: ['WiFi'],
	};

	const mockVehicleTypes = [
		{ id: 1, typeName: 'Sedan', description: 'Standard car' },
		{ id: 2, typeName: 'SUV', description: 'Large vehicle' },
	];

	const mockServices = [
		{ id: 1, name: 'WiFi', description: 'Internet access' },
		{ id: 2, name: 'Child Seat', description: 'Seat for children' },
	];

	beforeEach(() => {
		fixture = TestBed.configureTestingModule({
			imports: [VehicleInfoFormComponent],
		}).createComponent(VehicleInfoFormComponent);

		component = fixture.componentInstance;

		// required signal inputs
		fixture.componentRef.setInput('vehicleInfo', mockVehicleInfo);
		fixture.componentRef.setInput('vehicleTypes', mockVehicleTypes);
		fixture.componentRef.setInput('availableServices', mockServices);

		fixture.detectChanges();
	});

	/**************************   Component creation   **************************/

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	/**************************   Additional services logic   **************************/

	it('should return true when service is enabled', () => {
		expect(component.isServiceEnabled('WiFi')).toBeTrue();
	});

	it('should return false when service is not enabled', () => {
		expect(component.isServiceEnabled('Child Seat')).toBeFalse();
	});

	it('should add service when toggled on', () => {
		component.toggleService('Child Seat');

		expect(component.vehicleInfo().additionalServices).toContain('Child Seat');
		expect(component.isServiceEnabled('Child Seat')).toBeTrue();
	});

	it('should remove service when toggled off', () => {
		component.toggleService('WiFi');

		expect(component.vehicleInfo().additionalServices).not.toContain('WiFi');
		expect(component.isServiceEnabled('WiFi')).toBeFalse();
	});

	it('should not modify services when readonly', () => {
		fixture.componentRef.setInput('readonly', true);
		fixture.detectChanges();

		component.toggleService('Child Seat');

		expect(component.vehicleInfo().additionalServices).not.toContain('Child Seat');
	});

	/**************************   Vehicle type mapping   **************************/

	it('should return vehicle type id for selected type', () => {
		expect(component.getVehicleTypeId()).toBe(1);
	});

	it('should return description for selected type', () => {
		expect(component.getVehicleDescription()).toBe('Standard car');
	});

	it('should update vehicle type when selection changes', () => {
		component.onVehicleTypeChange('2');

		expect(component.vehicleInfo().type).toBe('SUV');
	});

	it('should not update vehicle type when id is invalid', () => {
		component.onVehicleTypeChange('999');

		expect(component.vehicleInfo().type).toBe('Sedan');
	});

	/**************************   Two-way binding   **************************/

	it('should update model when model input changes', () => {
		const input = fixture.nativeElement.querySelector('#model');

		input.value = 'Tesla Model 3';
		input.dispatchEvent(new Event('input'));

		expect(component.vehicleInfo().model).toBe('Tesla Model 3');
	});

	/**************************   Template / readonly behavior   **************************/

	it('should disable controls when readonly', fakeAsync(() => {
		fixture.componentRef.setInput('readonly', true);
		fixture.detectChanges();
		tick();

		const select = fixture.nativeElement.querySelector('#vehicleType');
		const modelInput = fixture.nativeElement.querySelector('#model');

		expect(select.disabled).toBeTrue();
		expect(modelInput.disabled).toBeTrue();
	}));

	it('should reflect enabled service in checkbox state', () => {
		const checkbox = fixture.nativeElement.querySelector('input[type="checkbox"]');

		expect(checkbox.checked).toBeTrue();
	});
});
