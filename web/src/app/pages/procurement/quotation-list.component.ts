import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-quotation-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Cotações</h2>
      <p-button label="Nova Cotação" icon="pi pi-plus" size="small" (onClick)="showNew = true" />
    </div>

    <p-table [value]="quotations()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
      <ng-template pTemplate="header">
        <tr>
          <th>Material/Serviço</th>
          <th style="width:80px" class="text-right">Qtd</th>
          <th style="width:100px">Fornecedor 1</th>
          <th style="width:100px">Fornecedor 2</th>
          <th style="width:100px">Fornecedor 3</th>
          <th style="width:100px">Melhor Preço</th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-q>
        <tr>
          <td>{{ q.description }}</td>
          <td class="text-right font-mono">{{ q.quantity }}</td>
          <td class="text-right font-mono">{{ q.price1 | number:'1.2-2' }}</td>
          <td class="text-right font-mono">{{ q.price2 | number:'1.2-2' }}</td>
          <td class="text-right font-mono">{{ q.price3 | number:'1.2-2' }}</td>
          <td class="text-right font-mono" style="font-weight:700;color:var(--sp-primary)">{{ bestPrice(q) | number:'1.2-2' }}</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="6" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Nenhuma cotação</td></tr></ng-template>
    </p-table>

    <!-- Nova Cotação -->
    <p-dialog header="Nova Cotação" [(visible)]="showNew" [style]="{width:'500px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Material/Serviço</label><input pInputText [(ngModel)]="form.description" class="w-full" /></div>
        <div class="grid">
          <div class="col-3"><label>Quantidade</label><p-inputNumber [(ngModel)]="form.quantity" styleClass="w-full" /></div>
          <div class="col-3"><label>Fornecedor 1</label><p-inputNumber [(ngModel)]="form.price1" mode="currency" currency="BRL" styleClass="w-full" /></div>
          <div class="col-3"><label>Fornecedor 2</label><p-inputNumber [(ngModel)]="form.price2" mode="currency" currency="BRL" styleClass="w-full" /></div>
          <div class="col-3"><label>Fornecedor 3</label><p-inputNumber [(ngModel)]="form.price3" mode="currency" currency="BRL" styleClass="w-full" /></div>
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showNew = false" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="create()" />
      </ng-template>
    </p-dialog>
  `,
})
export class QuotationListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  quotations = signal<any[]>([]);
  loading = signal(true);
  showNew = false;
  form: any = {};

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/procurement/quotations`).subscribe({ next: r => { this.quotations.set(r.content || r || []); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  bestPrice(q: any): number { return Math.min(...[q.price1, q.price2, q.price3].filter(p => p > 0)); }

  create() {
    this.quotations.update(list => [...list, { ...this.form }]);
    this.showNew = false;
    this.messages.add({ severity: 'success', summary: 'Cotação adicionada' });
  }
}
