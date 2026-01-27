import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehicleInfoFormComponent } from './vehicle-info-form.component';

describe('VehicleInfoFormComponent', () => {
  let component: VehicleInfoFormComponent;
  let fixture: ComponentFixture<VehicleInfoFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehicleInfoFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VehicleInfoFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
