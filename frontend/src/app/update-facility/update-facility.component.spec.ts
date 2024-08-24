import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateFacilityComponent } from './update-facility.component';

describe('UpdateFacilityComponent', () => {
  let component: UpdateFacilityComponent;
  let fixture: ComponentFixture<UpdateFacilityComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateFacilityComponent]
    });
    fixture = TestBed.createComponent(UpdateFacilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
