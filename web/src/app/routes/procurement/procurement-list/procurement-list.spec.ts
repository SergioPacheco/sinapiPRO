import { ActivatedRoute, Router } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { ProcurementListComponent } from './procurement-list';
import { ProcurementService } from '../services/procurement.service';

describe('ProcurementListComponent', () => {
  let fixture: ComponentFixture<ProcurementListComponent>;
  let component: ProcurementListComponent;
  let service: jasmine.SpyObj<ProcurementService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    service = jasmine.createSpyObj<ProcurementService>('ProcurementService', [
      'listOrders',
      'listOverdue',
      'generateFromAbc',
      'orderReportUrl',
    ]);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    service.listOrders.and.returnValue(of({ content: [], totalElements: 0 } as any));
    service.listOverdue.and.returnValue(of([]));
    service.generateFromAbc.and.returnValue(of({} as any));
    service.orderReportUrl.and.returnValue('/order.pdf');

    await TestBed.configureTestingModule({
      imports: [ProcurementListComponent],
      providers: [
        { provide: ProcurementService, useValue: service },
        { provide: Router, useValue: router },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: new Map() },
            parent: { snapshot: { paramMap: new Map([['projectId', 'p1']]) } },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ProcurementListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load orders and overdue on init', () => {
    expect(service.listOrders).toHaveBeenCalledWith('p1', 0, 20);
    expect(service.listOverdue).toHaveBeenCalledWith('p1');
  });

  it('should add abc item and generate requests', () => {
    component.abcDraft = { description: 'Cimento', quantity: 10, unit: 'SC' };
    component.addAbcItem();
    component.generateFromAbc();

    expect(service.generateFromAbc).toHaveBeenCalledWith('p1', [{ description: 'Cimento', quantity: 10, unit: 'SC' }]);
  });

  it('should navigate to quotations with order context', () => {
    component.openQuotations({ id: 'o1', number: 'PO-1' } as any);

    expect(router.navigate).toHaveBeenCalled();
    const args = router.navigate.calls.mostRecent().args;
    expect(args[0]).toEqual(['../quotations']);
    expect(args[1]?.queryParams).toEqual({ orderId: 'o1', orderNumber: 'PO-1' });
  });

  it('should open order pdf by service url', () => {
    const openSpy = spyOn(window, 'open');

    component.downloadPdf({ id: 'o1', number: 'PO-1' } as any);

    expect(service.orderReportUrl).toHaveBeenCalledWith('p1', 'o1');
    expect(openSpy).toHaveBeenCalledWith('/order.pdf', '_blank');
  });
});
