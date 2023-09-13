import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerAddressesComponent } from './customer-addresses.component';

describe('CustomerAddressesComponent', () => {
  let component: CustomerAddressesComponent;
  let fixture: ComponentFixture<CustomerAddressesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CustomerAddressesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerAddressesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
