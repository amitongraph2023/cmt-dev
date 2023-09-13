import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditCardCardComponent } from './credit-card-card.component';

describe('CreditCardCardComponent', () => {
  let component: CreditCardCardComponent;
  let fixture: ComponentFixture<CreditCardCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreditCardCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreditCardCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
