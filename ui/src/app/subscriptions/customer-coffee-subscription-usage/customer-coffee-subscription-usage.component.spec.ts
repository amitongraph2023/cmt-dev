import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerCoffeeSubscriptionUsageComponent } from './customer-coffee-subscription-usage.component';

describe('CustomerCoffeeSubscriptionUsageComponent', () => {
  let component: CustomerCoffeeSubscriptionUsageComponent;
  let fixture: ComponentFixture<CustomerCoffeeSubscriptionUsageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CustomerCoffeeSubscriptionUsageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerCoffeeSubscriptionUsageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
