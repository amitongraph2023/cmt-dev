import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GiftCardCardComponent } from './gift-card-card.component';

describe('GiftCardCardComponent', () => {
  let component: GiftCardCardComponent;
  let fixture: ComponentFixture<GiftCardCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GiftCardCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GiftCardCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
