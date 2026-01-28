import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavbarConfig } from './navbar-config';

describe('NavbarConfig', () => {
  let component: NavbarConfig;
  let fixture: ComponentFixture<NavbarConfig>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavbarConfig]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavbarConfig);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
