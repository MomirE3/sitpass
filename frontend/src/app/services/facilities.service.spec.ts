import { TestBed } from '@angular/core/testing';

import { HomeService } from './facilities.service';

describe('FacilitiesService', () => {
  let service: HomeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HomeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
