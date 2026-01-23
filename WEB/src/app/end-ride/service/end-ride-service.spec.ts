import { TestBed } from '@angular/core/testing';

import { EndRideService } from './end-ride-service';

describe('EndRideService', () => {
  let service: EndRideService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EndRideService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
