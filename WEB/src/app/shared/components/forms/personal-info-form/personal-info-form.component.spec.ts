import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalInfoFormComponent } from './personal-info-form.component';

describe('PersonalInfoFormComponent', () => {
  let component: PersonalInfoFormComponent;
  let fixture: ComponentFixture<PersonalInfoFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonalInfoFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PersonalInfoFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
