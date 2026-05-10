import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
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
import { environment } from '@env/environment';
import { AddItemDialogComponent } from './add-item-dialog';

interface StageNode {
  id: string; name: string; sortOrder: number;
  items: ItemNode[]; children: StageNode[]; subtotal: number;
}
interface ItemNode {
  id: string; code: string; description: string; unit: string;
  quantity: number; unitCost: number; totalCost: number; origin: string;
}
interface Worksheet {
  stages: StageNode[]; directCost: number; bdiPct: number; bdiAmount: number; total: number;
}
interface ServiceAbcEntry {
  itemId: string; serviceCode: string; description: string; unit: string;
  quantity: number; unitCost: number; cost: number; percentage: number;
  cumulativePercentage: number; classification: string;
}
interface BdiConfig {
  administration: number; profit: number; taxes: number;
  socialCharges: number; financialExpenses: number; risks: number; totalBdi: number;
}

@Component({
  selector: 'app-budget-worksheet',
  templateUrl: './budget-worksheet.html',
  styleUrl: './budget-worksheet.scss',
  imports: [FormsModule, DecimalPipe, MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, MatIconModule, MatSelectModule, MatTreeModule, MatDialogModule, PageHeader],
})
export class BudgetWorksheetComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly dialog = inject(MatDialog);

  budgetId = '';
  worksheet: Worksheet | null = null;
  serviceAbc: ServiceAbcEntry[] = [];
  newStageName = '';
  bdiForm = {
    administration: 0,
    profit: 0,
    taxes: 0,
    socialCharges: 0,
    financialExpenses: 0,
    risks: 0,
  };
  adjustment = { type: 'PERCENTAGE', percentage: 0, value: 0 };

  treeControl = new NestedTreeControl<StageNode>(node => node.children);
  dataSource = new MatTreeNestedDataSource<StageNode>();

  hasChild = (_: number, node: StageNode) => node.children && node.children.length > 0;

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
    this.http.get<Worksheet>(`/budgets/${this.budgetId}/worksheet`).subscribe(ws => {
      this.worksheet = ws;
      this.dataSource.data = ws.stages;
    });
  }

  loadServiceAbc() {
    this.http.get<ServiceAbcEntry[]>(`/budgets/${this.budgetId}/abc-curve/services`)
      .subscribe(entries => this.serviceAbc = entries.slice(0, 10));
  }

  openWorksheetReport() {
    window.open(`${environment.baseUrl}/budgets/${this.budgetId}/reports/worksheet.pdf`, '_blank');
  }

  openServiceAbcReport() {
    window.open(`${environment.baseUrl}/budgets/${this.budgetId}/reports/abc-services.pdf`, '_blank');
  }

  addStage() {
    if (!this.newStageName.trim()) return;
    const sortOrder = (this.worksheet?.stages.length || 0) + 1;
    this.http.post(`/budgets/${this.budgetId}/stages`, { name: this.newStageName, sortOrder }).subscribe(() => {
      this.newStageName = '';
      this.loadWorksheet();
    });
  }

  loadBdi() {
    this.http.get<BdiConfig>(`/budgets/${this.budgetId}/bdi`).subscribe(bdi => {
      this.bdiForm = {
        administration: this.toPct(bdi.administration),
        profit: this.toPct(bdi.profit),
        taxes: this.toPct(bdi.taxes),
        socialCharges: this.toPct(bdi.socialCharges),
        financialExpenses: this.toPct(bdi.financialExpenses),
        risks: this.toPct(bdi.risks),
      };
    });
  }

  saveBdi() {
    this.http.put(`/budgets/${this.budgetId}/bdi`, {
      administration: this.fromPct(this.bdiForm.administration),
      profit: this.fromPct(this.bdiForm.profit),
      taxes: this.fromPct(this.bdiForm.taxes),
      socialCharges: this.fromPct(this.bdiForm.socialCharges),
      financialExpenses: this.fromPct(this.bdiForm.financialExpenses),
      risks: this.fromPct(this.bdiForm.risks),
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
    this.http.post(`/budgets/${this.budgetId}/price-adjustment`, body).subscribe(() => {
      this.loadWorksheet();
      this.loadServiceAbc();
    });
  }

  addItem(stage: StageNode) {
    const dialogRef = this.dialog.open(AddItemDialogComponent, { width: '700px' });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.http.post(`/budgets/${this.budgetId}/stages/${stage.id}/items`, {
          compositionId: result.compositionId,
          quantity: result.quantity,
        }).subscribe(() => {
          this.loadWorksheet();
          this.loadServiceAbc();
        });
      }
    });
  }

  deleteStage(stage: StageNode) {
    if (confirm(`Excluir etapa "${stage.name}" e todos os seus itens?`)) {
      this.http.delete(`/budgets/${this.budgetId}/stages/${stage.id}`).subscribe(() => {
        this.loadWorksheet();
        this.loadServiceAbc();
      });
    }
  }

  deleteItem(item: ItemNode) {
    if (confirm('Excluir este item?')) {
      this.http.delete(`/budgets/${this.budgetId}/items/${item.id}`).subscribe(() => {
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
