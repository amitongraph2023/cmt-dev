import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GiftCoffeeComponent } from './gift-coffee.component';

describe('GiftCoffeeComponent', () => {
  let component: GiftCoffeeComponent;
  let fixture: ComponentFixture<GiftCoffeeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GiftCoffeeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GiftCoffeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
''
