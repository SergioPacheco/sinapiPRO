import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { MeasurementService } from './measurement.service';

describe('MeasurementService', () => {
  let service: MeasurementService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MeasurementService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(MeasurementService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should request memo by item', () => {
    service.getMemo('p1', 'm1', 'i1').subscribe();

    const req = httpMock.expectOne('/projects/p1/measurements/m1/items/i1/memo');
    expect(req.request.method).toBe('GET');
    req.flush({ measurementItemId: 'i1', lines: [], result: 0 });
  });

  it('should save memo payload', () => {
    const lines = [{ description: 'L1', formula: '1+1', value: 2 }];
    service.saveMemo('p1', 'm1', 'i1', lines, 2).subscribe();

    const req = httpMock.expectOne('/projects/p1/measurements/m1/items/i1/memo');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ lines, result: 2 });
    req.flush({});
  });

  it('should add extra measurement item', () => {
    const body = { description: 'Extra', quantity: 3, unitPrice: 10, contractorName: 'ABC' };
    service.addExtraItem('p1', 'm1', body).subscribe();

    const req = httpMock.expectOne('/projects/p1/measurements/m1/extra-items');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(body);
    req.flush({});
  });

  it('should load measurement history', () => {
    service.history('p1', 'm1').subscribe();

    const req = httpMock.expectOne('/projects/p1/measurements/m1/history');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});
