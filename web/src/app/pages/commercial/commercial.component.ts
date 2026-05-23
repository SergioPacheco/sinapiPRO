import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { TabViewModule } from 'primeng/tabview';
import { StatusTagComponent, CurrencyDisplayComponent } from '../../shared/components';

@Component({
  selector: 'app-commercial',
  standalone: true,
  imports: [TableModule, TabViewModule, StatusTagComponent, CurrencyDisplayComponent],
  template: `
    <h3 style="margin:0 0 1rem">Comercial</h3>
    <p-tabView>
      <p-tabPanel header="Propostas">
        <p-table [value]="proposals()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
          <ng-template pTemplate="header"><tr><th style="width:80px">#</th><th>Cliente</th><th class="text-right" style="width:140px">Valor</th><th style="width:120px">Status</th></tr></ng-template>
          <ng-template pTemplate="body" let-r>
            <tr><td>{{ r.number }}</td><td>{{ r.client }}</td><td class="text-right"><sp-currency [value]="r.value" /></td><td><sp-status [status]="r.status" /></td></tr>
          </ng-template>
          <ng-template pTemplate="emptymessage"><tr><td colspan="4" class="text-center text-muted p-3">Nenhuma proposta</td></tr></ng-template>
        </p-table>
      </p-tabPanel>
      <p-tabPanel header="Empreendimentos">
        <p-table [value]="developments()" styleClass="p-datatable-sm" [paginator]="true" [rows]="10">
          <ng-template pTemplate="header"><tr><th>Nome</th><th style="width:100px">Unidades</th><th style="width:100px">Vendidas</th></tr></ng-template>
          <ng-template pTemplate="body" let-r>
            <tr><td>{{ r.name }}</td><td>{{ r.units }}</td><td>{{ r.soldUnits }}</td></tr>
          </ng-template>
          <ng-template pTemplate="emptymessage"><tr><td colspan="3" class="text-center text-muted p-3">Nenhum empreendimento</td></tr></ng-template>
        </p-table>
      </p-tabPanel>
    </p-tabView>
  `,
})
export class CommercialComponent implements OnInit {
  private http = inject(HttpClient);
  proposals = signal<any[]>([]);
  developments = signal<any[]>([]);

  ngOnInit() {
    this.http.get<any>('/commercial/proposals').subscribe(res => this.proposals.set(res.content || res));
    this.http.get<any>('/commercial/developments').subscribe(res => this.developments.set(res.content || res));
  }
}
