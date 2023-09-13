import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerGiftCoffeeSubscriptionsComponent } from './customer-gift-coffee-subscriptions.component';

describe('CustomerGiftCoffeeSubscriptionsComponent', () => {
  let component: CustomerGiftCoffeeSubscriptionsComponent;
  let fixture: ComponentFixture<CustomerGiftCoffeeSubscriptionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CustomerGiftCoffeeSubscriptionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerGiftCoffeeSubscriptionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
