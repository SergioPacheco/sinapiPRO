import { Component, inject, OnInit, signal } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { RegistryService } from '../services/registry.service';
import { Employee } from '../models/registry.model';

@Component({
  selector: 'app-employee-list',
  template: `
    <page-header title="Funcionários" subtitle="Funcionários e empreiteiros" />
    <mat-tab-group (selectedTabChange)="onTabChange($event.index)">
      <mat-tab label="Todos" />
      <mat-tab label="Funcionários" />
      <mat-tab label="Empreiteiros" />
    </mat-tab-group>
    <mtx-grid [columns]="columns" [data]="list()" [length]="total()" [loading]="isLoading()"
      [pageOnFront]="false" [pageIndex]="page()" [pageSize]="size()" [pageSizeOptions]="[10, 20, 50]"
      (page)="onPageChange($event)" [rowStriped]="true" />
  `,
  imports: [MatTabsModule, MtxGridModule, PageHeader],
})
export class EmployeeListComponent implements OnInit {
  private readonly service = inject(RegistryService);
  list = signal<Employee[]>([]);
  total = signal(0);
  isLoading = signal(true);
  page = signal(0);
  size = signal(20);
  private typeFilter?: string;

  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Função', field: 'role', width: '150px' },
    { header: 'Tipo', field: 'type', width: '120px', tag: { EMPLOYEE: { text: 'Funcionário', color: 'blue' }, CONTRACTOR: { text: 'Empreiteiro', color: 'orange' } } },
    { header: 'CPF', field: 'document', width: '140px' },
    { header: 'Email', field: 'email', width: '200px' },
    { header: 'Valor/Hora', field: 'hourlyRate', width: '110px', formatter: (d: Employee) => d.hourlyRate ? `R$ ${d.hourlyRate.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}` : '-' },
  ];

  ngOnInit() { this.loadData(); }

  loadData() {
    this.isLoading.set(true);
    this.service.listEmployees(this.typeFilter, this.page(), this.size()).subscribe({
      next: res => { this.list.set(res.content); this.total.set(res.totalElements); this.isLoading.set(false); },
      error: () => this.isLoading.set(false),
    });
  }

  onTabChange(index: number) {
    this.typeFilter = index === 1 ? 'EMPLOYEE' : index === 2 ? 'CONTRACTOR' : undefined;
    this.page.set(0);
    this.loadData();
  }

  onPageChange(e: any) { this.page.set(e.pageIndex); this.size.set(e.pageSize); this.loadData(); }
}
