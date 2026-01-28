import { TestBed } from '@angular/core/testing';

import { DriverMarkerService } from './driver-marker.service';

describe('DriverMarkerService', () => {
  let service: DriverMarkerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverMarkerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
