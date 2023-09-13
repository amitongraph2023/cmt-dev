import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CateringRedirectComponent } from './catering-redirect.component';

describe('CateringRedirectComponent', () => {
  let component: CateringRedirectComponent;
  let fixture: ComponentFixture<CateringRedirectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CateringRedirectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CateringRedirectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
