import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewDriverPageComponent } from './new-driver-page.component';

describe('NewDriverPageComponent', () => {
  let component: NewDriverPageComponent;
  let fixture: ComponentFixture<NewDriverPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewDriverPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewDriverPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
