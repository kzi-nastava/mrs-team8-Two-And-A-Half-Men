import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverRideInfoPanelComponent } from './driver-ride-info-panel';

describe('DriverRideInfoPanel', () => {
  let component: DriverRideInfoPanelComponent;
  let fixture: ComponentFixture<DriverRideInfoPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverRideInfoPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverRideInfoPanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
