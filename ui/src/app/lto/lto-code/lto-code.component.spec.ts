import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LtoCodeComponent } from './lto-code.component';

describe('CodeComponent', () => {
  let component: LtoCodeComponent;
  let fixture: ComponentFixture<LtoCodeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LtoCodeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LtoCodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
