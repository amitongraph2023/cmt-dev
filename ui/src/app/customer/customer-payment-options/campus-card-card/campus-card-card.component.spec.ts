import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CampusCardCardComponent } from './campus-card-card.component';

describe('CampusCardCardComponent', () => {
  let component: CampusCardCardComponent;
  let fixture: ComponentFixture<CampusCardCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CampusCardCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CampusCardCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
