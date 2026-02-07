import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavbarRenderer } from './navbar-renderer';

describe('NavbarRenderer', () => {
  let component: NavbarRenderer;
  let fixture: ComponentFixture<NavbarRenderer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavbarRenderer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NavbarRenderer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
