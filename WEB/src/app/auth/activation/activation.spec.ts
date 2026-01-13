import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivationComponent } from './activation';

describe('ActivationComponent', () => {
  let component: ActivationComponent;
  let fixture: ComponentFixture<ActivationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivationComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
