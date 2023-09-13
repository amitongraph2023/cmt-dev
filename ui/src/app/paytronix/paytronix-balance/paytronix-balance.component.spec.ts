import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaytronixBalanceComponent } from './paytronix-balance.component';

describe('PaytronixBalanceComponent', () => {
  let component: PaytronixBalanceComponent;
  let fixture: ComponentFixture<PaytronixBalanceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaytronixBalanceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaytronixBalanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
