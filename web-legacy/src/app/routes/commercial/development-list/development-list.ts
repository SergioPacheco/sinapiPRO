import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { CommercialService } from '../services/commercial.service';
import { Development } from '../models/commercial.model';

@Component({
  selector: 'app-development-list',
  template: `
    <page-header title="Empreendimentos" subtitle="Incorporações e lançamentos imobiliários">
      <button mat-flat-button routerLink="new"><mat-icon>add</mat-icon> Novo Empreendimento</button>
    </page-header>
    <mtx-grid [columns]="columns" [data]="list()" [length]="total()" [loading]="isLoading()"
      [pageOnFront]="false" [pageIndex]="page()" [pageSize]="size()" [pageSizeOptions]="[10, 20, 50]"
      (page)="onPageChange($event)" [rowStriped]="true" />
  `,
  imports: [MatButtonModule, MatIconModule, MtxGridModule, PageHeader, RouterLink],
})
export class DevelopmentListComponent implements OnInit {
  private readonly service = inject(CommercialService);
  list = signal<Development[]>([]);
  total = signal(0);
  isLoading = signal(true);
  page = signal(0);
  size = signal(20);

  columns: MtxGridColumn[] = [
    { header: 'Nome', field: 'name', sortable: true },
    { header: 'Cidade', field: 'city', width: '140px' },
    { header: 'UF', field: 'state', width: '60px' },
    { header: 'Unidades', field: 'totalUnits', width: '100px' },
    { header: 'Lançamento', field: 'launchDate', width: '120px' },
    { header: 'Status', field: 'status', width: '120px', tag: { PLANNING: { text: 'Planejamento', color: 'blue' }, LAUNCHED: { text: 'Lançado', color: 'green' }, SOLD_OUT: { text: 'Esgotado', color: '' } } },
  ];

  ngOnInit() { this.loadData(); }

  loadData() {
    this.isLoading.set(true);
    this.service.listDevelopments(this.page(), this.size()).subscribe({
      next: res => { this.list.set(res.content); this.total.set(res.totalElements); this.isLoading.set(false); },
      error: () => this.isLoading.set(false),
    });
  }

  onPageChange(e: any) { this.page.set(e.pageIndex); this.size.set(e.pageSize); this.loadData(); }
}
