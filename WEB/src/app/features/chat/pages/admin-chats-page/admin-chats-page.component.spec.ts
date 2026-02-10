import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminChatsPageComponent } from './admin-chats-page.component';

describe('AdminChatsPageComponent', () => {
  let component: AdminChatsPageComponent;
  let fixture: ComponentFixture<AdminChatsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminChatsPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminChatsPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
