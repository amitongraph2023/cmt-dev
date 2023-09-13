import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplepayCardComponent } from './applepay-card.component';

describe('ApplepayCardComponent', () => {
  let component: ApplepayCardComponent;
  let fixture: ComponentFixture<ApplepayCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApplepayCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplepayCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
