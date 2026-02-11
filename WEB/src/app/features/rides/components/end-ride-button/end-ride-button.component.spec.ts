import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EndRideButtonComponent } from './end-ride-button.component';

describe('EndRideButtonComponent', () => {
  let component: EndRideButtonComponent;
  let fixture: ComponentFixture<EndRideButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EndRideButtonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EndRideButtonComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
