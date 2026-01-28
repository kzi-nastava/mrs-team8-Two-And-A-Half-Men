import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StartRideButtonComponent } from './start-ride-button.component';

describe('StartRideButtonComponent', () => {
  let component: StartRideButtonComponent;
  let fixture: ComponentFixture<StartRideButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StartRideButtonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StartRideButtonComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
