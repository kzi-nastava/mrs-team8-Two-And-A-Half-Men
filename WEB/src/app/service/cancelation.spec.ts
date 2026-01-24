import { TestBed } from '@angular/core/testing';

import { Cancelation } from './cancelation';

describe('Cancelation', () => {
  let service: Cancelation;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Cancelation);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
