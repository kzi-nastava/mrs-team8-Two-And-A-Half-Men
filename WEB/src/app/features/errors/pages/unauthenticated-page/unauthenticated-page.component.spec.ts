import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnauthenticatedPageComponent } from './unauthenticated-page.component';

describe('UnauthenticatedPageComponent', () => {
  let component: UnauthenticatedPageComponent;
  let fixture: ComponentFixture<UnauthenticatedPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnauthenticatedPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UnauthenticatedPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
