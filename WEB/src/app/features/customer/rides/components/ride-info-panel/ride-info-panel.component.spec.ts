import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideInfoPanelComponent } from './ride-info-panel.component';

describe('RideInfoPanel', () => {
  let component: RideInfoPanelComponent;
  let fixture: ComponentFixture<RideInfoPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideInfoPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideInfoPanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
