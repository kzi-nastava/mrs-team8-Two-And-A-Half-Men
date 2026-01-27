import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EstimateForm } from './estimate-form';

describe('EstimateForm', () => {
  let component: EstimateForm;
  let fixture: ComponentFixture<EstimateForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EstimateForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EstimateForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
