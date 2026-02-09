import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveRidesPageComponent } from './active-rides-page.component';

describe('ActiveRidesPageComponent', () => {
  let component: ActiveRidesPageComponent;
  let fixture: ComponentFixture<ActiveRidesPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActiveRidesPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActiveRidesPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
