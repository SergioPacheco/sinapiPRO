import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { InventoryService } from './inventory.service';

describe('InventoryService', () => {
  let service: InventoryService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [InventoryService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(InventoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should list stock items', () => {
    service.listItems('p1').subscribe();

    const req = httpMock.expectOne('/projects/p1/inventory/items');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should list below minimum items', () => {
    service.listBelowMinimum('p1').subscribe();

    const req = httpMock.expectOne('/projects/p1/inventory/items/below-minimum');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});
