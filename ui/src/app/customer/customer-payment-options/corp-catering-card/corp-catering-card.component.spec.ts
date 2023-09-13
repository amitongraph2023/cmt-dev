import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CorpCateringCardComponent } from './corp-catering-card.component';

describe('CorpCateringCardComponent', () => {
  let component: CorpCateringCardComponent;
  let fixture: ComponentFixture<CorpCateringCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CorpCateringCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CorpCateringCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
