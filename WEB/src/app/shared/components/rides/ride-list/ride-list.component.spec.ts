import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RidesListComponent } from './ride-list.component';

describe('RidesListComponent', () => {
  let component: RidesListComponent;
  let fixture: ComponentFixture<RidesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RidesListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RidesListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
