import { Component, inject, OnInit, signal, input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatChipsModule } from '@angular/material/chips';
import { MtxGridColumn, MtxGridModule } from '@ng-matero/extensions/grid';
import { PageHeader } from '@shared';
import { CommercialService } from '../services/commercial.service';
import { DevelopmentUnit } from '../models/commercial.model';

@Component({
  selector: 'app-unit-list',
  template: `
    <page-header title="Tabela de Preços" subtitle="Unidades do empreendimento" />
    <mat-chip-listbox (change)="onFilter($event.value)">
      <mat-chip-option value="all" selected>Todas</mat-chip-option>
      <mat-chip-option value="available">Disponíveis</mat-chip-option>
    </mat-chip-listbox>
    <mtx-grid [columns]="columns" [data]="list()" [loading]="isLoading()" [rowStriped]="true"
      [pageOnFront]="true" [showPaginator]="true" [pageSize]="20" />
  `,
  imports: [MatChipsModule, MtxGridModule, PageHeader],
})
export class UnitListComponent implements OnInit {
  private readonly service = inject(CommercialService);
  private readonly route = inject(ActivatedRoute);
  private devId = '';

  list = signal<DevelopmentUnit[]>([]);
  isLoading = signal(true);

  columns: MtxGridColumn[] = [
    { header: 'Código', field: 'code', width: '100px', sortable: true },
    { header: 'Tipo', field: 'type', width: '120px' },
    { header: 'Área (m²)', field: 'area', width: '100px' },
    { header: 'Andar', field: 'floor', width: '80px' },
    { header: 'Quartos', field: 'bedrooms', width: '80px' },
    { header: 'Preço', field: 'price', width: '140px', formatter: (d: DevelopmentUnit) => `R$ ${d.price?.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}` },
    { header: 'Status', field: 'status', width: '120px', tag: { AVAILABLE: { text: 'Disponível', color: 'green' }, RESERVED: { text: 'Reservado', color: 'orange' }, SOLD: { text: 'Vendido', color: 'red' } } },
  ];

  ngOnInit() {
    this.devId = this.route.snapshot.paramMap.get('devId') || '';
    this.loadAll();
  }

  loadAll() {
    this.isLoading.set(true);
    this.service.listUnits(this.devId).subscribe({ next: r => { this.list.set(r); this.isLoading.set(false); }, error: () => this.isLoading.set(false) });
  }

  onFilter(v: string) {
    this.isLoading.set(true);
    if (v === 'available') {
      this.service.availableUnits(this.devId).subscribe({ next: r => { this.list.set(r); this.isLoading.set(false); } });
    } else { this.loadAll(); }
  }
}
