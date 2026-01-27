import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelRideButton } from './cancel-ride-button';

describe('CancelRideButton', () => {
  let component: CancelRideButton;
  let fixture: ComponentFixture<CancelRideButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancelRideButton]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CancelRideButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
