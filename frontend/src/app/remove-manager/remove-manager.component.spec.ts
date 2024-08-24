import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RemoveManagerComponent } from './remove-manager.component';

describe('RemoveManagerComponent', () => {
  let component: RemoveManagerComponent;
  let fixture: ComponentFixture<RemoveManagerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RemoveManagerComponent]
    });
    fixture = TestBed.createComponent(RemoveManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
