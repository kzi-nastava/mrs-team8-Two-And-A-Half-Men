import { TestBed } from '@angular/core/testing';

import { UnregisteredAuthService } from './unregistered-auth.service';

describe('UnregisteredAuthService', () => {
  let service: UnregisteredAuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UnregisteredAuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
