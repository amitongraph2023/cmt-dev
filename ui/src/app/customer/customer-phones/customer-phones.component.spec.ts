import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerPhonesComponent } from './customer-phones.component';

describe('CustomerPhonesComponent', () => {
  let component: CustomerPhonesComponent;
  let fixture: ComponentFixture<CustomerPhonesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CustomerPhonesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerPhonesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
