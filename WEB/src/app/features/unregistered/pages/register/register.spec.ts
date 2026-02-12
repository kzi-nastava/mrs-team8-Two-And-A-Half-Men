import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { Register } from './register';
import { UnregisteredAuthService } from '@features/unregistered/services/unregistered-auth.service';
import { PopupsService } from '@shared/services/popups/popups.service';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router'; 

describe('Register', () => {
let component: Register;
  let fixture: ComponentFixture<Register>;
  let unregisteredAuthService: jasmine.SpyObj<UnregisteredAuthService>;
  let router: jasmine.SpyObj<Router>;
  let popupsService: jasmine.SpyObj<PopupsService>;
  const mockSuccessResponse = { message: 'User registered successfully' };
  const mockErrorResponseServer = { error: { error: 'Server error occurred' } };
  const mockErrorResponse = { error: { error: 'Email already exists' } };

  beforeEach(async () => {
    const unregisteredAuthServiceSpy = jasmine.createSpyObj('UnregisteredAuthService', ['register']);
    unregisteredAuthServiceSpy.register.and.returnValue(of(mockSuccessResponse));

    const popupsServiceSpy = jasmine.createSpyObj('PopupsService', ['success']);

    await TestBed.configureTestingModule({
      imports: [Register,ReactiveFormsModule],
      providers: [
        { provide: UnregisteredAuthService, useValue: unregisteredAuthServiceSpy },
        provideRouter([]), //https://angular.dev/guide/routing/testing
        { provide: PopupsService, useValue: popupsServiceSpy },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    unregisteredAuthService = TestBed.inject(UnregisteredAuthService) as jasmine.SpyObj<UnregisteredAuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    popupsService = TestBed.inject(PopupsService) as jasmine.SpyObj<PopupsService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should initialize with step 1 and empty form', () => {
    expect(component.step).toBe(1);
    expect(component.registerForm.get('firstName')?.value).toBe('');
    expect(component.registerForm.get('lastName')?.value).toBe('');
    expect(component.registerForm.get('phoneNumber')?.value).toBe('');
    expect(component.registerForm.get('address')?.value).toBe('');
    expect(component.registerForm.get('email')?.value).toBe('');
    expect(component.registerForm.get('password')?.value).toBe('');
    expect(component.registerForm.get('confirmPassword')?.value).toBe('');
  });
   it('should display step 1 fields and hide step 2 fields', fakeAsync(() => {
    component.step = 1;
    fixture.detectChanges();
    tick();

    const firstNameInput = fixture.debugElement.query(By.css('#firstName'));
    const lastNameInput = fixture.debugElement.query(By.css('#lastName'));
    const phoneNumberInput = fixture.debugElement.query(By.css('#phoneNumber'));
    const addressInput = fixture.debugElement.query(By.css('#address'));

    expect(firstNameInput).toBeTruthy();
    expect(lastNameInput).toBeTruthy();
    expect(phoneNumberInput).toBeTruthy();
    expect(addressInput).toBeTruthy();

    const emailInput = fixture.debugElement.query(By.css('#email'));
    const passwordInput = fixture.debugElement.query(By.css('#password'));
    const confirmPasswordInput = fixture.debugElement.query(By.css('#confirmPassword'));

    expect(emailInput).toBeFalsy();
    expect(passwordInput).toBeFalsy();
    expect(confirmPasswordInput).toBeFalsy();
  }));
   it('should display validation error for required firstName', fakeAsync(() => {
    component.step = 1;
    fixture.detectChanges();
    tick();

    component.registerForm.get('firstName')?.setValue('');
    component.registerForm.get('firstName')?.markAsTouched();

    fixture.detectChanges();
    tick();

    const errorElements: DebugElement[] = fixture.debugElement.queryAll(By.css('small'));
    const firstNameError = errorElements.find(el => 
      el.nativeElement.textContent.includes('First Name is required')
    );

    expect(firstNameError).toBeTruthy();
  }));

  it('should display validation error for invalid phone number pattern', fakeAsync(() => {
    component.step = 1;
    fixture.detectChanges();
    tick();

    component.registerForm.get('phoneNumber')?.setValue('abc');
    component.registerForm.get('phoneNumber')?.markAsTouched();

    fixture.detectChanges();
    tick();

    const errorElements: DebugElement[] = fixture.debugElement.queryAll(By.css('small'));
    const phoneError = errorElements.find(el => 
      el.nativeElement.textContent.includes('Phone number format is invalid')
    );

    expect(phoneError).toBeTruthy();
  }));


  it('should bind data from step 1 input fields to form controls', fakeAsync(() => {
    component.step = 1;
    fixture.detectChanges();
    tick();
    const firstNameInput = fixture.debugElement.query(By.css('#firstName')).nativeElement;
    firstNameInput.value = 'Petar';
    firstNameInput.dispatchEvent(new Event('input')); 
    const lastNameInput = fixture.debugElement.query(By.css('#lastName')).nativeElement;
    lastNameInput.value = 'Popovic';
    lastNameInput.dispatchEvent(new Event('input'));
    const phoneNumberInput = fixture.debugElement.query(By.css('#phoneNumber')).nativeElement;
    phoneNumberInput.value = '381628361185';
    phoneNumberInput.dispatchEvent(new Event('input'));
    const addressInput = fixture.debugElement.query(By.css('#address')).nativeElement;
    addressInput.value = 'Dragoseva 1/12';
    addressInput.dispatchEvent(new Event('input'));
    expect(component.registerForm.get('firstName')?.value).toBe('Petar');
    expect(component.registerForm.get('lastName')?.value).toBe('Popovic');
    expect(component.registerForm.get('phoneNumber')?.value).toBe('381628361185');
    expect(component.registerForm.get('address')?.value).toBe('Dragoseva 1/12');
  }));

  it('should be abele to press next button if step 1 fields are valid', fakeAsync(() => {
  component.step = 1;
  fixture.detectChanges();
  tick();
  const firstNameInput = fixture.debugElement.query(By.css('#firstName')).nativeElement;
  firstNameInput.value = 'Petar';
  firstNameInput.dispatchEvent(new Event('input'));   
  const lastNameInput = fixture.debugElement.query(By.css('#lastName')).nativeElement;
  lastNameInput.value = 'Popovic';
  lastNameInput.dispatchEvent(new Event('input'));
  const phoneNumberInput = fixture.debugElement.query(By.css('#phoneNumber')).nativeElement;
  phoneNumberInput.value = '381628361185';
  phoneNumberInput.dispatchEvent(new Event('input'));
  const addressInput = fixture.debugElement.query(By.css('#address')).nativeElement;
  addressInput.value = 'Dragoseva 1/12';
  addressInput.dispatchEvent(new Event('input'));
  const nextButton = fixture.debugElement.query(By.css('button')).nativeElement;
  nextButton.click();
  fixture.detectChanges();
  tick();
  expect(component.step).toBe(2);
}));  
it('should display step 2 fields and hide step 2 fields', fakeAsync(() => {
    component.step = 2;
    fixture.detectChanges();
    tick();

    const firstNameInput = fixture.debugElement.query(By.css('#firstName'));
    const lastNameInput = fixture.debugElement.query(By.css('#lastName'));
    const phoneNumberInput = fixture.debugElement.query(By.css('#phoneNumber'));
    const addressInput = fixture.debugElement.query(By.css('#address'));

    expect(firstNameInput).toBeFalsy();
    expect(lastNameInput).toBeFalsy();
    expect(phoneNumberInput).toBeFalsy();
    expect(addressInput).toBeFalsy();
    const emailInput = fixture.debugElement.query(By.css('#email'));
    const passwordInput = fixture.debugElement.query(By.css('#password'));
    const confirmPasswordInput = fixture.debugElement.query(By.css('#confirmPassword'));

    expect(emailInput).toBeTruthy();
    expect(passwordInput).toBeTruthy();
    expect(confirmPasswordInput).toBeTruthy();
  }));
  it('should display validation error for required email', fakeAsync(() => {
    component.step = 2;
    fixture.detectChanges();
    tick();
    const emailInput = fixture.debugElement.query(By.css('#email')).nativeElement;
    emailInput.value = '';
    emailInput.dispatchEvent(new Event('input'));
    emailInput.dispatchEvent(new Event('blur'));
    fixture.detectChanges();
    tick();
    const emailError = fixture.debugElement.query(By.css('#emailError'));
    expect(emailError).toBeTruthy();
    expect(emailError.nativeElement.textContent).toContain('Email is required');
  }));
  it('should display validation error for invalid email format', fakeAsync(() => {
    component.step = 2;
    fixture.detectChanges();
    tick();
    const emailInput = fixture.debugElement.query(By.css('#email')).nativeElement;
    emailInput.value = 'invalid-email-format';
    emailInput.dispatchEvent(new Event('input'));
    emailInput.dispatchEvent(new Event('blur'));
    fixture.detectChanges();
    tick();
    const emailError = fixture.debugElement.query(By.css('#emailError'));
    expect(emailError).toBeTruthy();
    expect(emailError.nativeElement.textContent).toContain('Please enter a valid email address');
  }));

  it('should display validation error for password less than 6 characters', fakeAsync(() => {
    component.step = 2;
    fixture.detectChanges();
    tick();
    const passwordInput = fixture.debugElement.query(By.css('#password')).nativeElement;
    passwordInput.value = '123';
    passwordInput.dispatchEvent(new Event('input'));
    passwordInput.dispatchEvent(new Event('blur'));
    fixture.detectChanges();
    tick();
    const passwordError = fixture.debugElement.query(By.css('#passwordError'));
    expect(passwordError).toBeTruthy();
    expect(passwordError.nativeElement.textContent).toContain('Password must be at least 6 characters');
  }));

  it("should display validation error when passwords do not match", fakeAsync(() => {
    component.step = 2;
    fixture.detectChanges();
    tick();

   component.registerForm.get('firstName')?.setValue('Petar');
   component.registerForm.get('lastName')?.setValue('Popovic');
   component.registerForm.get('phoneNumber')?.setValue('381628361185');
   component.registerForm.get('address')?.setValue('Dragoseva 1/12');
   component.registerForm.get('email')?.setValue('customer@test.com');
    const passwordInput = fixture.debugElement.query(By.css('#password')).nativeElement;
    passwordInput.value = 'password123';
    passwordInput.dispatchEvent(new Event('input'));
    passwordInput.dispatchEvent(new Event('blur'));
    fixture.detectChanges();
    const confirmPasswordInput = fixture.debugElement.query(By.css('#confirmPassword')).nativeElement;
    confirmPasswordInput.value = 'password11';
    confirmPasswordInput.dispatchEvent(new Event('input'));
    confirmPasswordInput.dispatchEvent(new Event('blur'));
    fixture.detectChanges();
    const submitBtn = fixture.debugElement.query(By.css('button[type="submit"]')).nativeElement;
    submitBtn.click();
    fixture.detectChanges();
    tick();
    const errorMessage = fixture.debugElement.query(By.css('#error-register'));
    expect(component.errorMessage()).toBe('Passwords do not match');  
    expect(errorMessage).toBeTruthy();
    expect(errorMessage.nativeElement.textContent).toContain('Passwords do not match');
  }));
  

  

});
