import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GiftCoffeeSubscriptionComponent } from './gift-coffee-subscription.component';

describe('GiftCoffeeSubscriptionComponent', () => {
  let component: GiftCoffeeSubscriptionComponent;
  let fixture: ComponentFixture<GiftCoffeeSubscriptionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GiftCoffeeSubscriptionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GiftCoffeeSubscriptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
