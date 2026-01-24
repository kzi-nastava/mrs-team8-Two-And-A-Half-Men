import { TestBed } from '@angular/core/testing';

import { DriverLocationWebsocketService } from './driver-location-websocket-service';

describe('DriverLocationWebsocketService', () => {
  let service: DriverLocationWebsocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverLocationWebsocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
