import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ProcurementService } from './procurement.service';

describe('ProcurementService', () => {
  let service: ProcurementService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ProcurementService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ProcurementService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should list overdue orders', () => {
    service.listOverdue('p1').subscribe();

    const req = httpMock.expectOne('/projects/p1/procurement/orders/overdue');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should send quotation email', () => {
    service.sendQuotationEmail('p1', 'q1').subscribe();

    const req = httpMock.expectOne('/projects/p1/procurement/quotations/q1/send-email');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    req.flush({ sent: 1, quotationId: 'q1' });
  });

  it('should generate procurement from abc items', () => {
    const items = [{ description: 'Aco', quantity: 10, unit: 'kg' }];
    service.generateFromAbc('p1', items).subscribe();

    const req = httpMock.expectOne('/projects/p1/procurement/from-abc');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ items });
    req.flush({});
  });

  it('should return comparative map url', () => {
    expect(service.comparativeMapUrl('p1', 'q1')).toBe('/api/v1/projects/p1/procurement/quotations/q1/reports/comparative-map.pdf');
  });

  it('should return order report url', () => {
    expect(service.orderReportUrl('p1', 'o1')).toBe('/api/v1/projects/p1/procurement/orders/o1/reports/order.pdf');
  });

  it('should include orderId when listing quotations by order', () => {
    service.listQuotations('p1', 0, 20, 'o1').subscribe();

    const req = httpMock.expectOne(r =>
      r.url === '/projects/p1/procurement/quotations' &&
      r.params.get('orderId') === 'o1'
    );
    expect(req.request.method).toBe('GET');
    req.flush({ content: [], totalElements: 0 });
  });
});
