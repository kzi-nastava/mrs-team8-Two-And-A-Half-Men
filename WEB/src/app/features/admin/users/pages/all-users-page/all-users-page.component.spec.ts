import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllUsersPageComponent } from './all-users-page.component';

describe('AllUsersPageComponent', () => {
  let component: AllUsersPageComponent;
  let fixture: ComponentFixture<AllUsersPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllUsersPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllUsersPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
