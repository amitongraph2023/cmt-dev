import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaytronixComponent } from './paytronix.component';

describe('PaytronixComponent', () => {
  let component: PaytronixComponent;
  let fixture: ComponentFixture<PaytronixComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaytronixComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaytronixComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
