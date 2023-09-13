import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BonusCardCardComponent } from './bonus-card-card.component';

describe('BonusCardCardComponent', () => {
  let component: BonusCardCardComponent;
  let fixture: ComponentFixture<BonusCardCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BonusCardCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BonusCardCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
