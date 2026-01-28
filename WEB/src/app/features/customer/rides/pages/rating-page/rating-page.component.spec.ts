import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RatingPageComponent } from './rating-page.component';

describe('RatingPageComponent', () => {
  let component: RatingPageComponent;
  let fixture: ComponentFixture<RatingPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RatingPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RatingPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
