import { TestBed } from '@angular/core/testing';

import { DriverHomepageService } from './driver-homepage.service';

describe('DriverHomepageService', () => {
  let service: DriverHomepageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverHomepageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
