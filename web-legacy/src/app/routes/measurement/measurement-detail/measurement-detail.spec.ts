import { ActivatedRoute, Router } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { MtxDialog } from '@ng-matero/extensions/dialog';

import { MeasurementDetailComponent } from './measurement-detail';
import { MeasurementService } from '../services/measurement.service';
import { MeasurementDetail } from '../models/measurement.model';

describe('MeasurementDetailComponent', () => {
  let fixture: ComponentFixture<MeasurementDetailComponent>;
  let component: MeasurementDetailComponent;
  let service: jasmine.SpyObj<MeasurementService>;
  let matDialog: jasmine.SpyObj<MatDialog>;
  let confirm: jasmine.SpyObj<MtxDialog>;

  beforeEach(async () => {
    service = jasmine.createSpyObj<MeasurementService>('MeasurementService', [
      'detail',
      'history',
      'approve',
      'reject',
      'getMemo',
      'saveMemo',
      'addExtraItem',
    ]);
    matDialog = jasmine.createSpyObj<MatDialog>('MatDialog', ['open']);
    confirm = jasmine.createSpyObj<MtxDialog>('MtxDialog', ['confirm']);

    service.detail.and.returnValue(of({
      id: 'm1',
      number: 1,
      periodStart: '2026-01-01',
      periodEnd: '2026-01-31',
      status: 'SUBMITTED',
      retentionPct: 0.05,
      grossAmount: 100,
      netAmount: 95,
      items: [],
    } as MeasurementDetail));
    service.history.and.returnValue(of([]));
    service.approve.and.returnValue(of({} as any));
    service.reject.and.returnValue(of({} as any));
    service.getMemo.and.returnValue(of({ measurementItemId: 'i1', lines: [], result: 0 }));
    service.saveMemo.and.returnValue(of({} as any));
    service.addExtraItem.and.returnValue(of({} as any));

    await TestBed.configureTestingModule({
      imports: [MeasurementDetailComponent],
      providers: [
        { provide: MeasurementService, useValue: service },
        { provide: MatDialog, useValue: matDialog },
        { provide: MtxDialog, useValue: confirm },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: new Map([['id', 'm1']]) },
            parent: { snapshot: { paramMap: new Map([['projectId', 'p1']]) } },
          },
        },
        { provide: Router, useValue: jasmine.createSpyObj<Router>('Router', ['navigate']) },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeasurementDetailComponent);
    component = fixture.componentInstance;
  });

  it('should load detail and history on init', () => {
    fixture.detectChanges();

    expect(service.detail).toHaveBeenCalledWith('p1', 'm1');
    expect(service.history).toHaveBeenCalledWith('p1', 'm1');
  });

  it('should approve measurement after confirm callback', () => {
    confirm.confirm.and.callFake((_title: string, _message: string, callback: () => void) => callback());
    fixture.detectChanges();

    component.approve();

    expect(service.approve).toHaveBeenCalledWith('p1', 'm1');
  });

  it('should reject measurement when dialog returns reason', () => {
    matDialog.open.and.returnValue({
      afterClosed: () => of('Correção necessária'),
    } as any);
    fixture.detectChanges();

    component.reject();

    expect(service.reject).toHaveBeenCalledWith('p1', 'm1', 'Correção necessária');
  });
});
