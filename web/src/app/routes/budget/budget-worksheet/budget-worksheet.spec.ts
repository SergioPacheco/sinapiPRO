import { ActivatedRoute } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';

import { BudgetWorksheetComponent } from './budget-worksheet';
import { BudgetWorksheetService } from '../services/budget-worksheet.service';

describe('BudgetWorksheetComponent', () => {
  let fixture: ComponentFixture<BudgetWorksheetComponent>;
  let component: BudgetWorksheetComponent;
  let worksheetService: jasmine.SpyObj<BudgetWorksheetService>;
  let dialog: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    worksheetService = jasmine.createSpyObj<BudgetWorksheetService>('BudgetWorksheetService', [
      'worksheet',
      'serviceAbcCurve',
      'getBdi',
      'updateBaseDate',
      'getItemMemo',
      'saveItemMemo',
      'worksheetReportUrl',
      'serviceAbcReportUrl',
      'analyticalReportUrl',
    ]);
    dialog = jasmine.createSpyObj<MatDialog>('MatDialog', ['open']);

    worksheetService.worksheet.and.returnValue(of({
      stages: [],
      directCost: 1000,
      bdiPct: 0.2,
      bdiAmount: 200,
      total: 1200,
    } as any));
    worksheetService.serviceAbcCurve.and.returnValue(of([]));
    worksheetService.getBdi.and.callFake((_, itemType: string) => of({
      itemType,
      administration: 0.05,
      profit: 0.1,
      taxes: 0.08,
      socialCharges: 0.02,
      financialExpenses: 0.01,
      risks: 0.01,
      totalBdi: 0.27,
    } as any));
    worksheetService.updateBaseDate.and.returnValue(of({} as any));
    worksheetService.getItemMemo.and.returnValue(of({
      budgetItemId: 'i1',
      lines: [{ description: 'Linha 1', formula: '1+1', value: 2 }],
      result: 2,
    } as any));
    worksheetService.saveItemMemo.and.returnValue(of({} as any));

    await TestBed.configureTestingModule({
      imports: [BudgetWorksheetComponent],
      providers: [
        { provide: BudgetWorksheetService, useValue: worksheetService },
        { provide: MatDialog, useValue: dialog },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: new Map([['id', 'b1']]),
              params: { id: 'b1' },
            },
            parent: { snapshot: { paramMap: new Map() } },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BudgetWorksheetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load worksheet, bdi and abc on init', () => {
    expect(worksheetService.worksheet).toHaveBeenCalledWith('b1');
    expect(worksheetService.getBdi).toHaveBeenCalledWith('b1', 'ALL');
    expect(worksheetService.getBdi).toHaveBeenCalledWith('b1', 'MATERIAL');
    expect(worksheetService.getBdi).toHaveBeenCalledWith('b1', 'LABOR');
    expect(worksheetService.getBdi).toHaveBeenCalledWith('b1', 'EQUIPMENT');
    expect(worksheetService.getBdi).toHaveBeenCalledWith('b1', 'SERVICE');
    expect(worksheetService.serviceAbcCurve).toHaveBeenCalledWith('b1');
    expect(component.worksheet?.total).toBe(1200);
    expect(component.bdiForm.ALL.profit).toBe(10);
  });

  it('should update base date and reload worksheet data', () => {
    dialog.open.and.returnValue({
      afterClosed: () => of({ referenceDate: '2026-05-01', state: 'SP' }),
    } as any);

    component.openBaseDateDialog();

    expect(worksheetService.updateBaseDate).toHaveBeenCalledWith('b1', {
      referenceDate: '2026-05-01',
      state: 'SP',
    });
    expect(worksheetService.worksheet).toHaveBeenCalledTimes(2);
    expect(worksheetService.serviceAbcCurve).toHaveBeenCalledTimes(2);
  });

  it('should append memo line and persist accumulated result', () => {
    dialog.open.and.returnValue({
      afterClosed: () => of({ description: 'Linha 2', formula: '2+2', value: 4 }),
    } as any);

    component.openMemo({
      id: 'i1',
      code: '1.1',
      description: 'Item',
      unit: 'UN',
      quantity: 1,
      unitCost: 10,
      totalCost: 10,
      origin: 'SINAPI',
    });

    expect(worksheetService.saveItemMemo).toHaveBeenCalledWith('b1', 'i1', [
      { description: 'Linha 1', formula: '1+1', value: 2 },
      { description: 'Linha 2', formula: '2+2', value: 4 },
    ], 6, null);
  });
});
