import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EndRideBtn } from './end-ride-btn';

describe('EndRideBtn', () => {
  let component: EndRideBtn;
  let fixture: ComponentFixture<EndRideBtn>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EndRideBtn]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EndRideBtn);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
