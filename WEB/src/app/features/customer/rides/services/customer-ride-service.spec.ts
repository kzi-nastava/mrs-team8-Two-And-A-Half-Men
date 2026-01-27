import { TestBed } from '@angular/core/testing';

import { CustomerRideService } from './customer-ride-service';

describe('CustomerRideService', () => {
  let service: CustomerRideService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomerRideService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
