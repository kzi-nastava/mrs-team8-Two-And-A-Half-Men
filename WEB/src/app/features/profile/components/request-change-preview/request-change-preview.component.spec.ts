import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestChangePreviewComponent } from './request-change-preview.component';

describe('RequestChangePreviewComponent', () => {
  let component: RequestChangePreviewComponent;
  let fixture: ComponentFixture<RequestChangePreviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestChangePreviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RequestChangePreviewComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
