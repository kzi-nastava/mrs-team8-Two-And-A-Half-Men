import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancellationButton } from './cancellation-button';

describe('CancellationButton', () => {
  let component: CancellationButton;
  let fixture: ComponentFixture<CancellationButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancellationButton]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CancellationButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
