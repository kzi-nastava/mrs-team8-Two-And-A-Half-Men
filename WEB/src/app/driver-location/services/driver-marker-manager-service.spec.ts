import { TestBed } from '@angular/core/testing';

import { DriverMarkerManagerService } from './driver-marker-manager-service';

describe('DriverMarkerManagerService', () => {
  let service: DriverMarkerManagerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverMarkerManagerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
