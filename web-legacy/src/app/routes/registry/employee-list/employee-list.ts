import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { MtxDialog } from '@ng-matero/extensions/dialog';
import { PageHeader } from '@shared';
import { RegistryService } from '../services/registry.service';
import { Employee } from '../models/registry.model';

@Component({
  selector: 'app-employee-list',
  template: `
    <page-header title="Funcionários" subtitle="Funcionários e empreiteiros">
      <button mat-flat-button color="primary" (click)="create()"><mat-icon>add</mat-icon> Novo cadastro</button>
    </page-header>
    <mat-tab-group (selectedTabChange)="onTabChange($event.index)">
      <mat-tab label="Todos" />
      <mat-tab label="Funcionários" />
      <mat-tab label="Empreiteiros" />
    </mat-tab-group>
    <mtx-grid [columns]="columns" [data]="list()" [length]="total()" [loading]="isLoading()"
      [pageOnFront]="false" [pageIndex]="page()" [pageSize]="size()" [pageSizeOptions]="[10, 20, 50]"
      (page)="onPageChange($event)" [rowStriped]="true" />
  `,
  imports: [MatButtonModule, MatIconModule, MatTabsModule, MtxGridModule, PageHeader],
})
export class EmployeeListComponent implements OnInit {
  private readonly service = inject(RegistryService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MtxDialog);
  list = signal<Employee[]>([]);
  total = signal(0);
  isLoading = signal(true);
  page = signal(0);
  size = signal(20);
  private typeFilter?: string;

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'employeeCode', width: '110px' },
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Função', field: 'role', width: '150px' },
    { header: 'Especialidade', field: 'specialty', width: '180px' },
    { header: 'Tipo', field: 'type', width: '120px', tag: { EMPLOYEE: { text: 'Funcionário', color: 'blue' }, CONTRACTOR: { text: 'Empreiteiro', color: 'orange' } } },
    { header: 'Status', field: 'employmentStatus', width: '120px', tag: { ACTIVE: { text: 'Ativo', color: 'green' }, ON_LEAVE: { text: 'Afastado', color: 'orange' }, INACTIVE: { text: 'Inativo', color: 'gray' } } },
    { header: 'CPF/CNPJ', field: 'document', width: '140px' },
    { header: 'Cidade/UF', field: 'city', width: '150px', formatter: (d: Employee) => [d.city, d.state].filter(Boolean).join('/') || '-' },
    { header: 'Empresa', field: 'companyName', width: '180px' },
    { header: 'Valor/Hora', field: 'hourlyRate', width: '110px', formatter: (d: Employee) => d.hourlyRate ? `R$ ${d.hourlyRate.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}` : '-' },
    {
      header: 'Ações',
      field: 'actions',
      width: '100px',
      pinned: 'right',
      type: 'button',
      buttons: [
        { type: 'icon', icon: 'edit', tooltip: 'Editar', click: (row: Employee) => this.router.navigate(['/registry/employees', row.id, 'edit']) },
        { type: 'icon', icon: 'delete', tooltip: 'Inativar', color: 'warn', click: (row: Employee) => this.confirmDeactivate(row) },
      ],
    },
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

  create() { this.router.navigate(['/registry/employees/new']); }

  confirmDeactivate(employee: Employee) {
    this.dialog.confirm('Confirmar inativação', `Inativar "${employee.name}"?`, () =>
      this.service.deleteEmployee(employee.id).subscribe(() => this.loadData())
    );
  }
}
