import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationSidebarComponent } from './notifications-sidebar.component';

describe('NotificationSidebarComponent', () => {
  let component: NotificationSidebarComponent;
  let fixture: ComponentFixture<NotificationSidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationSidebarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotificationSidebarComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
