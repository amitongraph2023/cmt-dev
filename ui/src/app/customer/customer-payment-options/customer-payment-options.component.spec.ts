import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerPaymentOptionsComponent } from './customer-payment-options.component';

describe('CustomerPaymentOptionsComponent', () => {
  let component: CustomerPaymentOptionsComponent;
  let fixture: ComponentFixture<CustomerPaymentOptionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CustomerPaymentOptionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerPaymentOptionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
