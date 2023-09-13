import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerSubscriptionsComponent } from './customer-subscriptions.component';

describe('CustomerSubscriptionsComponent', () => {
  let component: CustomerSubscriptionsComponent;
  let fixture: ComponentFixture<CustomerSubscriptionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CustomerSubscriptionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerSubscriptionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
