import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaytronixCardExchangeComponent } from './paytronix-card-exchange.component';

describe('PaytronixCardExchangeComponent', () => {
  let component: PaytronixCardExchangeComponent;
  let fixture: ComponentFixture<PaytronixCardExchangeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaytronixCardExchangeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaytronixCardExchangeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
