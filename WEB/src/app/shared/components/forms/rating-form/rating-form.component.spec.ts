import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { By } from '@angular/platform-browser';
import { RatingFormComponent, RatingFormData } from './rating-form.component';

describe('RatingFormComponent', () => {
	let component: RatingFormComponent;
	let fixture: ComponentFixture<RatingFormComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [RatingFormComponent, CommonModule, ReactiveFormsModule],
		}).compileComponents();

		fixture = TestBed.createComponent(RatingFormComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	// ─── Initialization ───────────────────────────────────────────────────────

	describe('Initialization', () => {
		it('should create component', () => {
			expect(component).toBeTruthy();
		});

		it('should initialize form with default values', () => {
			expect(component.ratingForm.value).toEqual({
				driver: 0,
				vehicle: 0,
				comment: '',
			});
		});

		it('should has array of 1 to 5 valued stars', () => {
			expect(component.stars).toEqual([1, 2, 3, 4, 5]);
		});

		it('should be invalid for initial values (driver=0 and vehicle=0 is illegal, min(1))', () => {
			expect(component.ratingForm.invalid).toBeTrue();
		});
	});

	// ─── setRating ─────────────────────────────────────────────────────────────

	describe('setRating()', () => {
		it('should set driver rating', () => {
			component.setRating('driver', 4);
			expect(component.ratingForm.value.driver).toBe(4);
		});

		it('should set vehicle rating', () => {
			component.setRating('vehicle', 3);
			expect(component.ratingForm.value.vehicle).toBe(3);
		});

		it('should override previously driver rating', () => {
			component.setRating('driver', 2);
			component.setRating('driver', 5);
			expect(component.ratingForm.value.driver).toBe(5);
		});

		it('should override previously vehicle rating', () => {
			component.setRating('vehicle', 1);
			component.setRating('vehicle', 3);
			expect(component.ratingForm.value.vehicle).toBe(3);
		});
	});

	// ─── Form validation ──────────────────────────────────────────────────────

	describe('Form validation', () => {
		it('should be valid when driver and vehicle ratings are >= 1', () => {
			component.setRating('driver', 3);
			component.setRating('vehicle', 4);
			expect(component.ratingForm.valid).toBeTrue();
		});

		it('should be invalid when driver rating is 0', () => {
			component.setRating('driver', 0);
			component.setRating('vehicle', 4);
			expect(component.ratingForm.invalid).toBeTrue();
		});

		it('should be invalid when vehicle rating is 0', () => {
			component.setRating('driver', 4);
			component.setRating('vehicle', 0);
			expect(component.ratingForm.invalid).toBeTrue();
		});

		it('should be valid instead of empty comment', () => {
			component.setRating('driver', 5);
			component.setRating('vehicle', 5);
			component.ratingForm.patchValue({ comment: '' });
			expect(component.ratingForm.valid).toBeTrue();
		});
	});

	// ─── submit() ──────────────────────────────────────────────────────────────

	describe('submit()', () => {
		it('should emmit ratingSubmitted with appropriate data when form is valid', () => {
			const emittedValues: RatingFormData[] = [];
			component.ratingSubmitted.subscribe((data) => emittedValues.push(data));

			component.setRating('driver', 4);
			component.setRating('vehicle', 3);
			component.ratingForm.patchValue({ comment: 'Excellent driver' });

			component.submit();

			expect(emittedValues.length).toBe(1);
			expect(emittedValues[0]).toEqual({
				driverRating: 4,
				vehicleRating: 3,
				comment: 'Excellent driver',
			});
		});

		it('should emmit empty string for comment if comment is not entered', () => {
			const emittedValues: RatingFormData[] = [];
			component.ratingSubmitted.subscribe((data) => emittedValues.push(data));

			component.setRating('driver', 5);
			component.setRating('vehicle', 5);
			component.ratingForm.patchValue({ comment: '' });

			component.submit();

			expect(emittedValues[0].comment).toBe('');
		});

		it('should not emmit ratingSubmitted when form is invalid', () => {
			const emittedValues: RatingFormData[] = [];
			component.ratingSubmitted.subscribe((data) => emittedValues.push(data));

			component.submit();

			expect(emittedValues.length).toBe(0);
		});

		it('should not emmit ratingSubmitted when only driver rating is provided', () => {
			const emittedValues: RatingFormData[] = [];
			component.ratingSubmitted.subscribe((data) => emittedValues.push(data));

			component.setRating('driver', 3);
			component.submit();

			expect(emittedValues.length).toBe(0);
		});
	});

	// ─── close() ───────────────────────────────────────────────────────────────

	describe('close()', () => {
		it('should emmit closePopup event', () => {
			let emitted = false;
			component.closePopup.subscribe(() => (emitted = true));

			component.close();

			expect(emitted).toBeTrue();
		});
	});

	// ─── DOM interactions ────────────────────────────────────────────────────────

	describe('DOM interactions', () => {
		it('click on close button should call close()', () => {
			spyOn(component, 'close');
			const closeBtn = fixture.debugElement.query(By.css('.close-btn'));
			closeBtn.nativeElement.click();
			expect(component.close).toHaveBeenCalled();
		});

		it('click on star should set driver rating', () => {
			spyOn(component, 'setRating').and.callThrough();

			// First section = driver, second button = star 2
			const driverStarButtons = fixture.debugElement
				.queryAll(By.css('.star-group'))[0]
				.queryAll(By.css('button'));

			driverStarButtons[1].nativeElement.click();
			fixture.detectChanges();

			expect(component.setRating).toHaveBeenCalledWith('driver', 2);
			expect(component.ratingForm.value.driver).toBe(2);
		});

		it('click on star should set vehicle rating', () => {
			spyOn(component, 'setRating').and.callThrough();

			// Second section = vehicle, forth button = star 4
			const vehicleStarButtons = fixture.debugElement
				.queryAll(By.css('.star-group'))[1]
				.queryAll(By.css('button'));

			vehicleStarButtons[3].nativeElement.click();
			fixture.detectChanges();

			expect(component.setRating).toHaveBeenCalledWith('vehicle', 4);
			expect(component.ratingForm.value.vehicle).toBe(4);
		});

		it('stars until chosen value should get filled class', () => {
			component.setRating('driver', 3);
			fixture.detectChanges();

			const driverStars = fixture.debugElement
				.queryAll(By.css('.star-group'))[0]
				.queryAll(By.css('.star'));

			expect(driverStars[0].nativeElement.classList).toContain('filled');
			expect(driverStars[1].nativeElement.classList).toContain('filled');
			expect(driverStars[2].nativeElement.classList).toContain('filled');
			expect(driverStars[3].nativeElement.classList).not.toContain('filled');
			expect(driverStars[4].nativeElement.classList).not.toContain('filled');
		});

		it('submit button should call submit()', () => {
			spyOn(component, 'submit');
			const submitBtn = fixture.debugElement.query(By.css('.confirm-btn'));
			submitBtn.nativeElement.click();
			expect(component.submit).toHaveBeenCalled();
		});

		it('textarea should be in relationship with comment', () => {
			const textarea = fixture.debugElement.query(By.css('textarea'));
			textarea.nativeElement.value = 'Test comment';
			textarea.nativeElement.dispatchEvent(new Event('input'));
			fixture.detectChanges();

			expect(component.ratingForm.value.comment).toBe('Test comment');
		});
	});
});
