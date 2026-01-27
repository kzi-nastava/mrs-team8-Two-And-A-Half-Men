import { TestBed } from '@angular/core/testing';

import { LocationPinService } from '../services/location-pin-service';

describe('LocationPinService', () => {
  let service: LocationPinService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LocationPinService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
