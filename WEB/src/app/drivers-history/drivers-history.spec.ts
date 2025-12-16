import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriversHistoryComponent } from './drivers-history';

describe('DriversHistory', () => {
  let component: DriversHistoryComponent;
  let fixture: ComponentFixture<DriversHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriversHistoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriversHistoryComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
