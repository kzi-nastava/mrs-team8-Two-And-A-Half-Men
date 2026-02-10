import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationsSidebarComponent } from './notifications-sidebar.component';

describe('NotificationsSidebarComponent', () => {
  let component: NotificationsSidebarComponent;
  let fixture: ComponentFixture<NotificationsSidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationsSidebarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotificationsSidebarComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
