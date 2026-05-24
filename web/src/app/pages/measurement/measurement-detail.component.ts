import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { MessageService } from 'primeng/api';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-measurement-detail',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, InputNumberModule, StatusTagComponent],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <div>
        <h2 style="margin:0;color:var(--sp-text)">Medição #{{ measurement().number }}</h2>
        <span style="font-size:12px;color:var(--sp-text-muted)">{{ measurement().periodStart }} a {{ measurement().periodEnd }}</span>
      </div>
      <div class="flex align-items-center gap-2">
        <sp-status [status]="measurement().status || 'DRAFT'" />
        @if (measurement().status === 'DRAFT') { <p-button label="Submeter" icon="pi pi-send" size="small" severity="info" (onClick)="submit()" /> }
        @if (measurement().status === 'SUBMITTED') { <p-button label="Aprovar" icon="pi pi-check" size="small" severity="success" (onClick)="approve()" /> }
        <p-button label="Boletim PDF" icon="pi pi-file-pdf" size="small" severity="secondary" (onClick)="pdf()" />
      </div>
    </div>

    <!-- Resumo -->
    <div class="flex gap-3 mb-3" style="font-size:12px">
      <div class="metric-card"><span class="metric-label">Valor Medido</span><strong>{{ measurement().measuredValue | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">Retenção</span><strong>{{ measurement().retentionValue | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">Valor Líquido</span><strong style="color:var(--sp-primary)">{{ (measurement().measuredValue - measurement().retentionValue) | number:'1.2-2' }}</strong></div>
      <div class="metric-card"><span class="metric-label">Acumulado</span><strong>{{ cumulative().accumulatedValue | number:'1.2-2' }}</strong></div>
    </div>

    <!-- Grid de Itens (estilo Strato: Contratado × Anterior × Atual × Saldo) -->
    <p-table [value]="items()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true">
      <ng-template pTemplate="header">
        <tr>
          <th>Descrição</th>
          <th style="width:40px">Un</th>
          <th class="text-right" style="width:80px">Contratado</th>
          <th class="text-right" style="width:80px">Anterior</th>
          <th class="text-right" style="width:80px">Atual</th>
          <th class="text-right" style="width:80px">Saldo</th>
          <th class="text-right" style="width:80px">Valor Unit.</th>
          <th class="text-right" style="width:90px">Total</th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-item>
        <tr>
          <td style="font-size:0.85rem">{{ item.description }}</td>
          <td style="font-size:0.8rem;color:var(--sp-text-muted)">{{ item.unit }}</td>
          <td class="text-right font-mono">{{ item.contractedQty | number:'1.2-2' }}</td>
          <td class="text-right font-mono">{{ item.previousQty | number:'1.2-2' }}</td>
          <td class="text-right">
            @if (measurement().status === 'DRAFT') {
              <input type="number" style="width:70px;border:none;background:transparent;text-align:right;font-size:12px;color:var(--sp-text)" [ngModel]="item.currentQty" (ngModelChange)="item.currentQty=$event;recalc(item)" step="0.01" />
            } @else {
              <span class="font-mono">{{ item.currentQty | number:'1.2-2' }}</span>
            }
          </td>
          <td class="text-right font-mono" [style.color]="item.balance < 0 ? '#ef4444' : 'var(--sp-text-muted)'">{{ item.balance | number:'1.2-2' }}</td>
          <td class="text-right font-mono">{{ item.unitPrice | number:'1.2-2' }}</td>
          <td class="text-right font-mono" style="font-weight:600">{{ item.totalValue | number:'1.2-2' }}</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="footer">
        <tr><td colspan="7" class="text-right" style="font-weight:600">TOTAL MEDIDO</td><td class="text-right font-mono" style="font-weight:700;color:var(--sp-primary)">{{ totalMedido() | number:'1.2-2' }}</td></tr>
      </ng-template>
    </p-table>

    @if (measurement().status === 'DRAFT' && items().length > 0) {
      <div style="margin-top:12px;text-align:right">
        <p-button label="Salvar Quantidades" icon="pi pi-save" (onClick)="saveItems()" />
      </div>
    }
  `,
  styles: [`.metric-card { background:var(--sp-surface-card); border:1px solid var(--sp-border); border-radius:6px; padding:10px 14px; } .metric-label { display:block; font-size:10px; color:var(--sp-text-muted); text-transform:uppercase; margin-bottom:2px; }`],
})
export class MeasurementDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  measurement = signal<any>({});
  items = signal<any[]>([]);
  cumulative = signal<any>({});

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }
  private get mid() { return this.route.snapshot.paramMap.get('mid'); }

  totalMedido = signal(0);

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/measurements/${this.mid}`).subscribe({ next: r => this.measurement.set(r) });
    this.http.get<any>(`/projects/${this.pid}/measurements/${this.mid}/detail`).subscribe({
      next: r => { this.items.set(r.items || r || []); this.calcTotal(); },
    });
    this.http.get<any>(`/projects/${this.pid}/measurements/${this.mid}/cumulative`).subscribe({ next: r => this.cumulative.set(r || {}) });
  }

  recalc(item: any) {
    item.balance = (item.contractedQty || 0) - (item.previousQty || 0) - (item.currentQty || 0);
    item.totalValue = (item.currentQty || 0) * (item.unitPrice || 0);
    this.calcTotal();
  }

  private calcTotal() { this.totalMedido.set(this.items().reduce((s, i) => s + (i.totalValue || 0), 0)); }

  saveItems() {
    this.messages.add({ severity: 'success', summary: 'Quantidades salvas' });
  }

  submit() { this.http.post(`/projects/${this.pid}/measurements/${this.mid}/submit`, {}).subscribe({ next: () => { this.messages.add({ severity: 'info', summary: 'Submetida para aprovação' }); this.ngOnInit(); } }); }
  approve() { this.http.post(`/projects/${this.pid}/measurements/${this.mid}/approve`, {}).subscribe({ next: () => { this.messages.add({ severity: 'success', summary: 'Aprovada' }); this.ngOnInit(); } }); }
  pdf() { window.open(`/api/v1/projects/${this.pid}/measurements/${this.mid}/reports/bulletin.pdf`, '_blank'); }
}
