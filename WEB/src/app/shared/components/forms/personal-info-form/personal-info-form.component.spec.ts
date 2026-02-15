import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PersonalInfoFormComponent } from './personal-info-form.component';

class MockFileReader {
	onload: ((e: any) => void) | null = null;

	readAsDataURL(file: File) {
		if (this.onload) {
			this.onload({
				target: { result: 'fake-base64-image' },
			});
		}
	}
}


describe('[Student1] PersonalInfoFormComponent', () => {
	let fixture: ComponentFixture<PersonalInfoFormComponent>;
	let component: PersonalInfoFormComponent;

	const mockInfo = {
		firstName: 'John',
		lastName: 'Doe',
		phoneNumber: '123456789',
		address: 'Test Street 123',
		email: 'john@example.com',
		imgSrc: null,
	};

	beforeEach(() => {
		fixture = TestBed.configureTestingModule({
			imports: [PersonalInfoFormComponent],
		}).createComponent(PersonalInfoFormComponent);

		component = fixture.componentInstance;

		// REQUIRED because model.required()
		fixture.componentRef.setInput('personalInfo', mockInfo);

		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	/**************************   getPhotoUrl()   **************************/

	it('should return default photo when imgSrc missing', () => {
		component.personalInfo.set({ ...mockInfo, imgSrc: '' });

		expect(component.getPhotoUrl()).toBe('assets/default-profile.png');
	});

	it('should return custom photo when imgSrc exists', () => {
		component.personalInfo.set({
			...mockInfo,
			imgSrc: 'data:image/png;base64,abc',
		});

		expect(component.getPhotoUrl()).toContain('data:image');
	});

	/**************************   onPhotoUpload()   **************************/
	it('should update personalInfo and emit photo on upload', () => {
		spyOn(window as any, 'FileReader').and.returnValue(new MockFileReader());

		spyOn(component.photoChange, 'emit');

		const file = new File(['dummy'], 'test.png', { type: 'image/png' });

		const event = {
			target: {
				files: [file],
			},
		} as any;

		component.onPhotoUpload(event);

		expect(component.selectedFile).toBe(file);

		expect(component.personalInfo().imgSrc).toBe('fake-base64-image');

		expect(component.photoChange.emit).toHaveBeenCalledWith(file);
	});

	/**************************   showPhotoUpload input   **************************/

	it('should hide photo upload when showPhotoUpload is false', () => {
		fixture.componentRef.setInput('showPhotoUpload', false);
		fixture.detectChanges();

		const photo = fixture.nativeElement.querySelector('.photo-column');

		expect(photo).toBeNull();
	});

	it('should show photo upload when showPhotoUpload is true', () => {
		fixture.componentRef.setInput('showPhotoUpload', true);
		fixture.detectChanges();

		const photo = fixture.nativeElement.querySelector('.photo-column');

		expect(photo).not.toBeNull();
	});

	/**************************   readonly input   **************************/

	it('should hide upload button when readonly is true', () => {
		fixture.componentRef.setInput('readonly', true);
		fixture.detectChanges();

		const btn = fixture.nativeElement.querySelector('.upload-button');

		expect(btn).toBeNull();
	});

	/**************************   Two-way binding   **************************/
	it('should update model when input changes', () => {
		const input = fixture.nativeElement.querySelector('#firstName');

		input.value = 'Alice';
		input.dispatchEvent(new Event('input'));

		expect(component.personalInfo().firstName).toBe('Alice');
	});
});
