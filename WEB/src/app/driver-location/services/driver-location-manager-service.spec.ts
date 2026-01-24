import { TestBed } from '@angular/core/testing';

import { DriverLocationManagerService } from './driver-location-manager-service';

describe('DriverLocationManagerService', () => {
  let service: DriverLocationManagerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverLocationManagerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
