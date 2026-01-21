import { TestBed } from '@angular/core/testing';

import { SheredLocationsService } from './shered-locations-service';

describe('SheredLocationsService', () => {
  let service: SheredLocationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SheredLocationsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
