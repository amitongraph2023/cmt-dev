import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SubscriptionNavbarComponent } from './subscription-navbar.component';

describe('SubscriptionNavbarComponent', () => {
  let component: SubscriptionNavbarComponent;
  let fixture: ComponentFixture<SubscriptionNavbarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SubscriptionNavbarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SubscriptionNavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
