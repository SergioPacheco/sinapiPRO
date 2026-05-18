import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { MtxDialog } from '@ng-matero/extensions/dialog';
import { PageHeader } from '@shared';
import { RegistryService } from '../services/registry.service';
import { Client } from '../models/registry.model';

@Component({
  selector: 'app-client-list',
  template: `
    <page-header title="Clientes" subtitle="Cadastro de clientes">
      <button mat-flat-button color="primary" (click)="create()"><mat-icon>add</mat-icon> Novo cliente</button>
    </page-header>
    <mtx-grid [columns]="columns" [data]="list()" [length]="total()" [loading]="isLoading()"
      [pageOnFront]="false" [pageIndex]="page()" [pageSize]="size()" [pageSizeOptions]="[10, 20, 50]"
      (page)="onPageChange($event)" [rowStriped]="true" />
  `,
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class ClientListComponent implements OnInit {
  private readonly service = inject(RegistryService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MtxDialog);
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
    {
      header: 'Ações',
      field: 'actions',
      width: '100px',
      pinned: 'right',
      type: 'button',
      buttons: [
        { type: 'icon', icon: 'edit', tooltip: 'Editar', click: (row: Client) => this.router.navigate(['/registry/clients', row.id, 'edit']) },
        { type: 'icon', icon: 'delete', tooltip: 'Excluir', color: 'warn', click: (row: Client) => this.confirmDelete(row) },
      ],
    },
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

  create() { this.router.navigate(['/registry/clients/new']); }

  confirmDelete(client: Client) {
    this.dialog.confirm('Confirmar exclusão', `Excluir "${client.name}"?`, () =>
      this.service.deleteClient(client.id).subscribe(() => this.loadData())
    );
  }
}
