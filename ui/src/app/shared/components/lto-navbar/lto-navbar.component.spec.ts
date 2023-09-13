import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LtoNavbarComponent } from './lto-navbar.component';

describe('LtoNavbarComponent', () => {
  let component: LtoNavbarComponent;
  let fixture: ComponentFixture<LtoNavbarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LtoNavbarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LtoNavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
