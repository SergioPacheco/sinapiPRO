import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { ProgressBarModule } from 'primeng/progressbar';
import { StatusTagComponent, CurrencyDisplayComponent, EmptyStateComponent } from '../../shared/components';

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, InputTextModule, DropdownModule, ProgressBarModule, StatusTagComponent, CurrencyDisplayComponent, EmptyStateComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0">Obras</h2>
      <p-button label="Nova Obra" icon="pi pi-plus" (onClick)="router.navigate(['/projects/new'])" />
    </div>

    @if (!loading() && projects().length === 0) {
      <sp-empty title="Nenhuma obra cadastrada" message="Crie sua primeira obra para começar a gerenciar" icon="building" actionLabel="Criar Primeira Obra" (action)="router.navigate(['/projects/new'])" />
    } @else {
      <p-table [value]="projects()" [loading]="loading()" [paginator]="true" [rows]="20" [rowHover]="true"
               [globalFilterFields]="['code','name','customerName']" styleClass="p-datatable-sm p-datatable-striped"
               (onRowSelect)="onSelect($event)" selectionMode="single" dataKey="id">
        <ng-template pTemplate="caption">
          <div class="flex gap-2 align-items-center">
            <span class="p-input-icon-left"><i class="pi pi-search"></i>
              <input pInputText placeholder="Buscar obra..." [(ngModel)]="search" (input)="dt.filterGlobal(search, 'contains')" />
            </span>
            <p-dropdown [options]="statusOptions" [(ngModel)]="statusFilter" placeholder="Status" [showClear]="true" (onChange)="loadData()" styleClass="w-10rem" />
          </div>
        </ng-template>
        <ng-template pTemplate="header">
          <tr>
            <th style="width:90px" pSortableColumn="code">Código</th>
            <th pSortableColumn="name">Obra</th>
            <th style="width:120px">Status</th>
            <th style="width:120px">Progresso</th>
            <th style="width:140px" class="text-right">Valor</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-p>
          <tr [pSelectableRow]="p">
            <td class="font-mono">{{ p.code }}</td>
            <td><strong>{{ p.name }}</strong><br><span class="text-muted" style="font-size:12px">{{ p.customerName }}</span></td>
            <td><sp-status [status]="p.status" /></td>
            <td><p-progressBar [value]="p.progress || 0" [showValue]="true" [style]="{'height':'16px'}" /></td>
            <td class="text-right"><sp-currency [value]="p.totalBudget" /></td>
          </tr>
        </ng-template>
      </p-table>
    }
  `,
})
export class ProjectsComponent implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  router = inject(Router);

  projects = signal<any[]>([]);
  loading = signal(true);
  search = '';
  statusFilter: string | null = null;
  dt: any;

  statusOptions = [
    { label: 'Planejamento', value: 'PLANNING' },
    { label: 'Em Execução', value: 'IN_PROGRESS' },
    { label: 'Suspensa', value: 'SUSPENDED' },
    { label: 'Concluída', value: 'COMPLETED' },
  ];

  ngOnInit() {
    this.statusFilter = this.route.snapshot.queryParamMap.get('status');
    this.loadData();
  }

  loadData() {
    const params: any = { page: 0, size: 100 };
    if (this.statusFilter) params.status = this.statusFilter;
    this.http.get<any>('/projects', { params }).subscribe({
      next: res => { this.projects.set(res.content || []); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  onSelect(event: any) { this.router.navigate(['/projects', event.data.id]); }
}
