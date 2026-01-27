import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideDetailsPageComponent } from './ride-details-page.component';

describe('RideDetailsPageComponent', () => {
  let component: RideDetailsPageComponent;
  let fixture: ComponentFixture<RideDetailsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideDetailsPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideDetailsPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
