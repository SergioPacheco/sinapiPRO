import { ActivatedRoute } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { QuotationListComponent } from './quotation-list';
import { ProcurementService } from '../services/procurement.service';

describe('QuotationListComponent', () => {
  let fixture: ComponentFixture<QuotationListComponent>;
  let component: QuotationListComponent;
  let service: jasmine.SpyObj<ProcurementService>;

  beforeEach(async () => {
    service = jasmine.createSpyObj<ProcurementService>('ProcurementService', [
      'listQuotations',
      'sendQuotationEmail',
      'comparativeMapUrl',
    ]);

    service.listQuotations.and.returnValue(of({ content: [], totalElements: 0 } as any));
    service.sendQuotationEmail.and.returnValue(of({ sent: 1, quotationId: 'q1' }));
    service.comparativeMapUrl.and.returnValue('/map.pdf');

    await TestBed.configureTestingModule({
      imports: [QuotationListComponent],
      providers: [
        { provide: ProcurementService, useValue: service },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: new Map(),
              queryParamMap: new Map([['orderId', 'o1'], ['orderNumber', 'PO-1']]),
            },
            parent: { snapshot: { paramMap: new Map([['projectId', 'p1']]) } },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(QuotationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load quotations filtered by order id from query params', () => {
    expect(service.listQuotations).toHaveBeenCalledWith('p1', 0, 20, 'o1');
    expect(component.selectedOrderNumber).toBe('PO-1');
  });
});
