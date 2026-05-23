import { Component, inject, OnInit, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { AfterSalesService } from '../services/aftersales.service';
import { ServiceTicket, TicketSummary } from '../models/aftersales.model';

@Component({
  selector: 'app-ticket-list',
  template: `
    <page-header title="Pós-Venda" subtitle="Chamados de assistência técnica" />

    <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
      <mat-card><mat-card-content><p class="text-center"><span class="text-2xl font-bold text-orange-600">{{ summary()?.open }}</span><br>Abertos</p></mat-card-content></mat-card>
      <mat-card><mat-card-content><p class="text-center"><span class="text-2xl font-bold text-blue-600">{{ summary()?.inProgress }}</span><br>Em Andamento</p></mat-card-content></mat-card>
      <mat-card><mat-card-content><p class="text-center"><span class="text-2xl font-bold text-green-600">{{ summary()?.resolved }}</span><br>Resolvidos</p></mat-card-content></mat-card>
      <mat-card><mat-card-content><p class="text-center"><span class="text-2xl font-bold">{{ summary()?.closed }}</span><br>Fechados</p></mat-card-content></mat-card>
    </div>

    <mat-tab-group (selectedTabChange)="onTabChange($event.index)">
      <mat-tab label="Abertos" />
      <mat-tab label="Em Andamento" />
      <mat-tab label="Todos" />
    </mat-tab-group>

    <mtx-grid [columns]="columns" [data]="list()" [length]="total()" [loading]="isLoading()"
      [pageOnFront]="false" [pageIndex]="page()" [pageSize]="size()" [pageSizeOptions]="[10, 20, 50]"
      (page)="onPageChange($event)" [rowStriped]="true" />
  `,
  imports: [MatCardModule, MatTabsModule, MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class TicketListComponent implements OnInit {
  private readonly service = inject(AfterSalesService);

  list = signal<ServiceTicket[]>([]);
  total = signal(0);
  isLoading = signal(true);
  page = signal(0);
  size = signal(20);
  summary = signal<TicketSummary | null>(null);
  private statusFilter: string | undefined = 'OPEN';

  columns: MtxGridColumn[] = [
    { header: 'Cliente', field: 'clientName', sortable: true },
    { header: 'Categoria', field: 'category', width: '130px' },
    { header: 'Descrição', field: 'description' },
    { header: 'Prioridade', field: 'priority', width: '110px', tag: { LOW: { text: 'Baixa', color: '' }, MEDIUM: { text: 'Média', color: 'orange' }, HIGH: { text: 'Alta', color: 'red' }, CRITICAL: { text: 'Crítica', color: 'red' } } },
    { header: 'Responsável', field: 'assignedTo', width: '140px' },
    { header: 'Prazo', field: 'dueDate', width: '110px' },
    { header: 'Status', field: 'status', width: '130px', tag: { OPEN: { text: 'Aberto', color: 'orange' }, IN_PROGRESS: { text: 'Em Andamento', color: 'blue' }, RESOLVED: { text: 'Resolvido', color: 'green' }, CLOSED: { text: 'Fechado', color: '' } } },
  ];

  ngOnInit() {
    this.service.summary().subscribe(s => this.summary.set(s));
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);
    this.service.list(this.statusFilter, this.page(), this.size()).subscribe({
      next: res => { this.list.set(res.content); this.total.set(res.totalElements); this.isLoading.set(false); },
      error: () => this.isLoading.set(false),
    });
  }

  onTabChange(index: number) {
    this.statusFilter = index === 0 ? 'OPEN' : index === 1 ? 'IN_PROGRESS' : undefined;
    this.page.set(0);
    this.loadData();
  }

  onPageChange(e: any) { this.page.set(e.pageIndex); this.size.set(e.pageSize); this.loadData(); }
}
