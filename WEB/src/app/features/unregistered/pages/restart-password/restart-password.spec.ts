import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestartPassword } from './restart-password';

describe('RestartPassword', () => {
  let component: RestartPassword;
  let fixture: ComponentFixture<RestartPassword>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestartPassword]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestartPassword);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
