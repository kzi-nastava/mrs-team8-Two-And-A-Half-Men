import { TestBed } from '@angular/core/testing';

import { SharedLocationsService } from './shared-locations.service';

describe('SheredLocationsService', () => {
  let service: SharedLocationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SharedLocationsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
