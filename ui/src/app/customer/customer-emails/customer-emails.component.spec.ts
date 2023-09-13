import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerEmailsComponent } from './customer-emails.component';

describe('CustomerEmailsComponent', () => {
  let component: CustomerEmailsComponent;
  let fixture: ComponentFixture<CustomerEmailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CustomerEmailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerEmailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
