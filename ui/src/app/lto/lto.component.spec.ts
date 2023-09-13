import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LtoComponent } from './lto.component';

describe('LtoComponent', () => {
  let component: LtoComponent;
  let fixture: ComponentFixture<LtoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LtoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LtoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
