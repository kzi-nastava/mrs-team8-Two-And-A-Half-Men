import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoryRidePageComponent } from './history-ride-page.component';

describe('HistoryRidePageComponent', () => {
  let component: HistoryRidePageComponent;
  let fixture: ComponentFixture<HistoryRidePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoryRidePageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoryRidePageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
