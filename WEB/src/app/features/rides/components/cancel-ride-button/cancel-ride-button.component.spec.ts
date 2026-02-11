import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelRideButtonComponent } from './cancel-ride-button.component';

describe('CancelRideButtonComponent', () => {
  let component: CancelRideButtonComponent;
  let fixture: ComponentFixture<CancelRideButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancelRideButtonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CancelRideButtonComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
