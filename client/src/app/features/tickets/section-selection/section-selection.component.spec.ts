import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SectionSelectionComponent } from './section-selection.component';

describe('SectionSelectionComponent', () => {
  let component: SectionSelectionComponent;
  let fixture: ComponentFixture<SectionSelectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectionSelectionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SectionSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
