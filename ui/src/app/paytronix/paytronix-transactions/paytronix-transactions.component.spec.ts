import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaytronixTransactionsComponent } from './paytronix-transactions.component';

describe('PatronixTransactionsComponent', () => {
  let component: PaytronixTransactionsComponent;
  let fixture: ComponentFixture<PaytronixTransactionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaytronixTransactionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaytronixTransactionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
