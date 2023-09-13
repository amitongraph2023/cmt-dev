import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaytronixNavbarComponent } from './paytronix-navbar.component';

describe('PaytronixNavbarComponent', () => {
  let component: PaytronixNavbarComponent;
  let fixture: ComponentFixture<PaytronixNavbarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaytronixNavbarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaytronixNavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
