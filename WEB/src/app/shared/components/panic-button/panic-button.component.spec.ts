import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanicButtonComponent } from './panic-button.component';

describe('PanicButtonComponent', () => {
  let component: PanicButtonComponent;
  let fixture: ComponentFixture<PanicButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PanicButtonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PanicButtonComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
