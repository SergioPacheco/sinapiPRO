import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { RegistryService } from '../services/registry.service';
import { Client } from '../models/registry.model';

@Component({
  selector: 'app-client-list',
  template: `
    <page-header title="Clientes" subtitle="Cadastro de clientes" />
    <mtx-grid [columns]="columns" [data]="list()" [length]="total()" [loading]="isLoading()"
      [pageOnFront]="false" [pageIndex]="page()" [pageSize]="size()" [pageSizeOptions]="[10, 20, 50]"
      (page)="onPageChange($event)" [rowStriped]="true" />
  `,
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class ClientListComponent implements OnInit {
  private readonly service = inject(RegistryService);
  list = signal<Client[]>([]);
  total = signal(0);
  isLoading = signal(true);
  page = signal(0);
  size = signal(20);

  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'CPF/CNPJ', field: 'document', width: '150px' },
    { header: 'Email', field: 'email', width: '200px' },
    { header: 'Telefone', field: 'phone', width: '140px' },
    { header: 'Cidade', field: 'city', width: '140px' },
    { header: 'UF', field: 'state', width: '60px' },
  ];

  ngOnInit() { this.loadData(); }

  loadData() {
    this.isLoading.set(true);
    this.service.listClients(this.page(), this.size()).subscribe({
      next: res => { this.list.set(res.content); this.total.set(res.totalElements); this.isLoading.set(false); },
      error: () => this.isLoading.set(false),
    });
  }

  onPageChange(e: any) { this.page.set(e.pageIndex); this.size.set(e.pageSize); this.loadData(); }
}
