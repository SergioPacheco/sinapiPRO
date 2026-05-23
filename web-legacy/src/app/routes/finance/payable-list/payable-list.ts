import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { FinanceService } from '../services/finance.service';
import { Payable } from '../models/finance.model';

@Component({
  selector: 'app-payable-list',
  template: `
    <page-header title="Contas a Pagar" subtitle="Gestão de despesas e pagamentos" />
    <mat-chip-listbox (change)="onFilterChange($event.value)" aria-label="Filtro de status">
      <mat-chip-option value="">Todos</mat-chip-option>
      <mat-chip-option value="overdue" selected>Vencidos</mat-chip-option>
      <mat-chip-option value="pending">Pendentes</mat-chip-option>
    </mat-chip-listbox>
    <mtx-grid
      [columns]="columns"
      [data]="list()"
      [length]="total()"
      [loading]="isLoading()"
      [pageOnFront]="false"
      [pageIndex]="page()"
      [pageSize]="size()"
      [pageSizeOptions]="[10, 20, 50]"
      (page)="onPageChange($event)"
      [rowStriped]="true"
    />
  `,
  imports: [MatButtonModule, MatIconModule, MatTabsModule, MatChipsModule, MtxGridModule, PageHeader],
})
export class PayableListComponent implements OnInit {
  private readonly service = inject(FinanceService);
  private readonly route = inject(ActivatedRoute);
  private projectId = '';

  list = signal<Payable[]>([]);
  total = signal(0);
  isLoading = signal(true);
  page = signal(0);
  size = signal(20);

  columns: MtxGridColumn[] = [
    { header: 'Descrição', field: 'description', sortable: true },
    { header: 'Valor', field: 'amount', width: '130px', formatter: (d: Payable) => `R$ ${d.amount?.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}` },
    { header: 'Vencimento', field: 'dueDate', width: '120px', sortable: true },
    { header: 'Categoria', field: 'category', width: '120px' },
    { header: 'Status', field: 'status', width: '120px', tag: { PENDING: { text: 'Pendente', color: 'orange' }, PAID: { text: 'Pago', color: 'green' }, OVERDUE: { text: 'Vencido', color: 'red' }, CANCELLED: { text: 'Cancelado', color: '' } } },
  ];

  ngOnInit() {
    let r = this.route.snapshot; while (r.parent && !r.paramMap.get('projectId')) r = r.parent;
    this.projectId = r.paramMap.get('projectId') || '';
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);
    this.service.listPayables(this.projectId, this.page(), this.size()).subscribe({
      next: res => { this.list.set(res.content); this.total.set(res.totalElements); this.isLoading.set(false); },
      error: () => this.isLoading.set(false),
    });
  }

  onFilterChange(filter: string) {
    if (filter === 'overdue') {
      this.isLoading.set(true);
      this.service.overduePayables(this.projectId).subscribe({ next: res => { this.list.set(res); this.total.set(res.length); this.isLoading.set(false); } });
    } else { this.loadData(); }
  }

  onPageChange(e: any) { this.page.set(e.pageIndex); this.size.set(e.pageSize); this.loadData(); }
}
