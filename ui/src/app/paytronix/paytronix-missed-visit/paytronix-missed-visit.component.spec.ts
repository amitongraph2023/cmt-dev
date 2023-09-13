import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaytronixMissedVisitComponent } from './paytronix-missed-visit.component';

describe('PaytronixMissedVisitComponent', () => {
  let component: PaytronixMissedVisitComponent;
  let fixture: ComponentFixture<PaytronixMissedVisitComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaytronixMissedVisitComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaytronixMissedVisitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
