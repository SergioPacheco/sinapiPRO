import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatTreeModule, MatTreeNestedDataSource } from '@angular/material/tree';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { NestedTreeControl } from '@angular/cdk/tree';
import { PageHeader } from '@shared';
import { AddItemDialogComponent } from './add-item-dialog';
import { BaseDateDialogComponent, UpdateBaseDateResult } from './base-date-dialog';
import { BudgetMemoDialogComponent } from './budget-memo-dialog';
import {
  BudgetBdiConfig,
  BudgetMemoLine,
  BudgetServiceAbcEntry,
  BudgetWorksheet,
  BudgetWorksheetItem,
  BudgetWorksheetStage,
} from '../models/budget.model';
import { BudgetService } from '../services/budget.service';
import { BudgetWorksheetService } from '../services/budget-worksheet.service';

interface BdiValues {
  administration: number;
  profit: number;
  taxes: number;
  socialCharges: number;
  financialExpenses: number;
  risks: number;
}

@Component({
  selector: 'app-budget-worksheet',
  templateUrl: './budget-worksheet.html',
  styleUrl: './budget-worksheet.scss',
  imports: [FormsModule, DecimalPipe, MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, MatIconModule, MatSelectModule, MatTreeModule, MatDialogModule, PageHeader],
})
export class BudgetWorksheetComponent implements OnInit {
  private readonly bdiTypes = ['ALL', 'MATERIAL', 'LABOR', 'EQUIPMENT', 'SERVICE'] as const;
  private readonly worksheetService = inject(BudgetWorksheetService);
  private readonly route = inject(ActivatedRoute);
  private readonly dialog = inject(MatDialog);

  budgetId = '';
  worksheet: BudgetWorksheet | null = null;
  serviceAbc: BudgetServiceAbcEntry[] = [];
  newStageName = '';
  bdiForm: Record<string, BdiValues> = {
    ALL: { administration: 0, profit: 0, taxes: 0, socialCharges: 0, financialExpenses: 0, risks: 0 },
    MATERIAL: { administration: 0, profit: 0, taxes: 0, socialCharges: 0, financialExpenses: 0, risks: 0 },
    LABOR: { administration: 0, profit: 0, taxes: 0, socialCharges: 0, financialExpenses: 0, risks: 0 },
    EQUIPMENT: { administration: 0, profit: 0, taxes: 0, socialCharges: 0, financialExpenses: 0, risks: 0 },
    SERVICE: { administration: 0, profit: 0, taxes: 0, socialCharges: 0, financialExpenses: 0, risks: 0 },
  };
  adjustment = { type: 'PERCENTAGE', percentage: 0, value: 0 };

  treeControl = new NestedTreeControl<BudgetWorksheetStage>(node => node.children);
  dataSource = new MatTreeNestedDataSource<BudgetWorksheetStage>();

  hasChild = (_: number, node: BudgetWorksheetStage) => node.children && node.children.length > 0;

  ngOnInit() {
    this.budgetId = this.route.snapshot.paramMap.get('id') || this.route.parent!.snapshot.paramMap.get('id') || '';
    if (!this.budgetId) {
      // Try getting from parent route params (nested under project workspace)
      this.budgetId = this.route.snapshot.params['id'] || '';
    }
    this.loadWorksheet();
    this.loadBdi();
    this.loadServiceAbc();
  }

  loadWorksheet() {
    this.worksheetService.worksheet(this.budgetId).subscribe(ws => {
      this.worksheet = ws;
      this.dataSource.data = ws.stages;
    });
  }

  loadServiceAbc() {
    this.worksheetService.serviceAbcCurve(this.budgetId)
      .subscribe(entries => this.serviceAbc = entries.slice(0, 10));
  }

  openWorksheetReport() {
    window.open(this.worksheetService.worksheetReportUrl(this.budgetId), '_blank');
  }

  openServiceAbcReport() {
    window.open(this.worksheetService.serviceAbcReportUrl(this.budgetId), '_blank');
  }

  openAnalyticalReport() {
    window.open(this.worksheetService.analyticalReportUrl(this.budgetId), '_blank');
  }

  openBaseDateDialog() {
    const dialogRef = this.dialog.open(BaseDateDialogComponent, { width: '420px' });
    dialogRef.afterClosed().subscribe((result?: UpdateBaseDateResult) => {
      if (!result) return;
      this.worksheetService.updateBaseDate(this.budgetId, result).subscribe(() => {
        this.loadWorksheet();
        this.loadServiceAbc();
      });
    });
  }

  addStage() {
    if (!this.newStageName.trim()) return;
    const sortOrder = (this.worksheet?.stages.length || 0) + 1;
    this.worksheetService.createStageEntry(this.budgetId, { name: this.newStageName, sortOrder }).subscribe(() => {
      this.newStageName = '';
      this.loadWorksheet();
    });
  }

  loadBdi() {
    this.bdiTypes.forEach(type => {
      this.worksheetService.getBdi(this.budgetId, type).subscribe((bdi: BudgetBdiConfig) => {
        const target = (bdi.itemType || type) as keyof typeof this.bdiForm;
        this.bdiForm[target] = {
          administration: this.toPct(bdi.administration),
          profit: this.toPct(bdi.profit),
          taxes: this.toPct(bdi.taxes),
          socialCharges: this.toPct(bdi.socialCharges),
          financialExpenses: this.toPct(bdi.financialExpenses),
          risks: this.toPct(bdi.risks),
        };
      });
    });
  }

  saveBdi(itemType: string = 'ALL') {
    const config = this.bdiForm[itemType];
    this.worksheetService.setBdi(this.budgetId, {
      itemType,
      administration: this.fromPct(config.administration),
      profit: this.fromPct(config.profit),
      taxes: this.fromPct(config.taxes),
      socialCharges: this.fromPct(config.socialCharges),
      financialExpenses: this.fromPct(config.financialExpenses),
      risks: this.fromPct(config.risks),
    }).subscribe(() => {
      this.loadBdi();
      this.loadWorksheet();
      this.loadServiceAbc();
    });
  }

  applyAdjustment() {
    const body = this.adjustment.type === 'PERCENTAGE'
      ? { type: 'PERCENTAGE', percentage: this.fromPct(this.adjustment.percentage) }
      : { type: 'VALUE', value: this.adjustment.value };
    this.worksheetService.updatePrices(this.budgetId, body).subscribe(() => {
      this.loadWorksheet();
      this.loadServiceAbc();
    });
  }

  addItem(stage: BudgetWorksheetStage) {
    const dialogRef = this.dialog.open(AddItemDialogComponent, { width: '700px' });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.worksheetService.addStageItem(this.budgetId, stage.id, {
          compositionId: result.compositionId,
          quantity: result.quantity,
        }).subscribe(() => {
          this.loadWorksheet();
          this.loadServiceAbc();
        });
      }
    });
  }

  openMemo(item: BudgetWorksheetItem) {
    this.worksheetService.getItemMemo(this.budgetId, item.id)
      .subscribe({
        next: memo => this.openMemoDialog(item.id, memo.lines || []),
        error: () => this.openMemoDialog(item.id, []),
      });
  }

  private openMemoDialog(itemId: string, existingLines: BudgetMemoLine[]) {
    const seed = existingLines.length > 0 ? existingLines[existingLines.length - 1] : null;
    const dialogRef = this.dialog.open(BudgetMemoDialogComponent, { width: '520px', data: seed });
    dialogRef.afterClosed().subscribe((result?: BudgetMemoLine) => {
      if (!result) return;
      const lines = [...existingLines, result];
      const total = lines.reduce((sum, line) => sum + (line.value || 0), 0);
      this.worksheetService.saveItemMemo(this.budgetId, itemId, lines, total, null).subscribe();
    });
  }

  deleteStage(stage: BudgetWorksheetStage) {
    if (confirm(`Excluir etapa "${stage.name}" e todos os seus itens?`)) {
      this.worksheetService.deleteStageEntry(this.budgetId, stage.id).subscribe(() => {
        this.loadWorksheet();
        this.loadServiceAbc();
      });
    }
  }

  deleteItem(item: BudgetWorksheetItem) {
    if (confirm('Excluir este item?')) {
      this.worksheetService.deleteItemEntry(this.budgetId, item.id).subscribe(() => {
        this.loadWorksheet();
        this.loadServiceAbc();
      });
    }
  }

  private toPct(value: number) {
    return Number(((value || 0) * 100).toFixed(4));
  }

  private fromPct(value: number) {
    return Number(((value || 0) / 100).toFixed(6));
  }
}
