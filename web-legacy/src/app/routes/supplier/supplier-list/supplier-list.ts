import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { MtxDialog } from '@ng-matero/extensions/dialog';
import { PageHeader } from '@shared';
import { SupplierService } from '../services/supplier.service';
import { Supplier } from '../models/supplier.model';

@Component({
  selector: 'app-supplier-list',
  templateUrl: './supplier-list.html',
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader],
})
export class SupplierListComponent implements OnInit {
  private readonly service = inject(SupplierService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MtxDialog);

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'code', width: '100px' },
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Categoria', field: 'category', width: '130px' },
    {
      header: 'Homologação',
      field: 'qualificationStatus',
      width: '140px',
      tag: {
        APPROVED: { text: 'Homologado', color: 'green' },
        UNDER_REVIEW: { text: 'Em revisão', color: 'orange' },
        BLOCKED: { text: 'Bloqueado', color: 'red' },
        PROSPECT: { text: 'Prospect', color: 'blue' },
      },
    },
    { header: 'CNPJ/CPF', field: 'taxId', width: '160px' },
    { header: 'Contato', field: 'contactName', width: '180px' },
    { header: 'E-mail', field: 'email' },
    { header: 'Telefone', field: 'phone', width: '140px' },
    { header: 'Cidade/UF', field: 'city', width: '150px', formatter: (data: Supplier) => [data.city, data.state].filter(Boolean).join('/') || '-' },
    { header: 'Pgto (dias)', field: 'paymentTermDays', width: '110px' },
    { header: 'Entrega (dias)', field: 'leadTimeDays', width: '120px' },
    { header: 'Avaliação', field: 'rating', width: '90px' },
    {
      header: 'Ativo',
      field: 'active',
      width: '80px',
      formatter: (data: Supplier) => data.active ? 'Sim' : 'Não',
    },
    {
      header: 'Ações',
      field: 'actions',
      width: '120px',
      pinned: 'right',
      type: 'button',
      buttons: [
        { type: 'icon', icon: 'edit', tooltip: 'Editar', click: (r: Supplier) => this.router.navigate(['/suppliers', r.id, 'edit']) },
        { type: 'icon', icon: 'delete', tooltip: 'Excluir', color: 'warn', click: (r: Supplier) => this.confirmDelete(r) },
      ],
    },
  ];

  list: Supplier[] = [];
  total = 0;
  isLoading = true;
  query = { page: 0, size: 20 };

  ngOnInit() { this.loadData(); }

  loadData() {
    this.isLoading = true;
    this.service.list(this.query.page, this.query.size).subscribe({
      next: res => { this.list = res.content; this.total = res.totalElements; this.isLoading = false; },
      error: () => { this.isLoading = false; },
    });
  }

  onPageChange(event: any) {
    this.query.page = event.pageIndex;
    this.query.size = event.pageSize;
    this.loadData();
  }

  confirmDelete(supplier: Supplier) {
    this.dialog.confirm('Confirmar exclusão', `Excluir "${supplier.name}"?`, () =>
      this.service.delete(supplier.id).subscribe(() => this.loadData())
    );
  }

  create() { this.router.navigate(['/suppliers/new']); }
}
