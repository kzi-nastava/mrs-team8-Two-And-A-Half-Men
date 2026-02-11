import { TestBed } from '@angular/core/testing';

import { FavouriteRoutesService } from './favourite-routes.service';

describe('FavouriteRoutesService', () => {
  let service: FavouriteRoutesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FavouriteRoutesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
