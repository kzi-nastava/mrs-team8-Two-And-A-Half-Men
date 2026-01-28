import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverHomePageComponent } from './driver-home-page.component';

describe('DriverHomePageComponent', () => {
  let component: DriverHomePageComponent;
  let fixture: ComponentFixture<DriverHomePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverHomePageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverHomePageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
