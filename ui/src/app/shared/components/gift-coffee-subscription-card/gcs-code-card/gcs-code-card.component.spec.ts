import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GcsCodeCardComponent } from './gcs-code-card.component';

describe('GcsCodeCardComponent', () => {
  let component: GcsCodeCardComponent;
  let fixture: ComponentFixture<GcsCodeCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GcsCodeCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GcsCodeCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
