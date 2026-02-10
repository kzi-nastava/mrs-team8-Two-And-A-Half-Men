import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverActivationComponent } from './driver-activation.component';

describe('DriverActivationComponent', () => {
  let component: DriverActivationComponent;
  let fixture: ComponentFixture<DriverActivationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverActivationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverActivationComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
