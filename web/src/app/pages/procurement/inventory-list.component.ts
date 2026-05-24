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
import { DropdownModule } from 'primeng/dropdown';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule, InputNumberModule, DropdownModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-3">
      <h2 style="margin:0;color:var(--sp-text)">Estoque / Requisição de Insumos</h2>
      <div class="flex gap-2">
        <p-button label="Entrada" icon="pi pi-arrow-down" size="small" severity="success" (onClick)="openMov('ENTRADA')" />
        <p-button label="Saída" icon="pi pi-arrow-up" size="small" severity="warn" (onClick)="openMov('SAIDA')" />
        <p-button label="Transferência" icon="pi pi-arrows-h" size="small" severity="secondary" (onClick)="openMov('TRANSFERENCIA')" />
      </div>
    </div>

    <!-- Saldo em Estoque -->
    <p-table [value]="stock()" [loading]="loading()" styleClass="p-datatable-sm p-datatable-gridlines" [rowHover]="true" [paginator]="true" [rows]="20">
      <ng-template pTemplate="header">
        <tr>
          <th>Insumo</th>
          <th style="width:50px">Un</th>
          <th class="text-right" style="width:90px">Saldo</th>
          <th class="text-right" style="width:90px">Valor Médio</th>
          <th class="text-right" style="width:100px">Valor Total</th>
        </tr>
      </ng-template>
      <ng-template pTemplate="body" let-s>
        <tr>
          <td>{{ s.materialName || s.description }}</td>
          <td style="color:var(--sp-text-muted)">{{ s.unit }}</td>
          <td class="text-right font-mono" [style.color]="s.balance <= 0 ? '#ef4444' : 'var(--sp-text)'">{{ s.balance | number:'1.2-2' }}</td>
          <td class="text-right font-mono">{{ s.averageCost | number:'1.2-2' }}</td>
          <td class="text-right font-mono" style="font-weight:600">{{ s.totalValue | number:'1.2-2' }}</td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage"><tr><td colspan="5" class="text-center" style="padding:2rem;color:var(--sp-text-muted)">Estoque vazio</td></tr></ng-template>
    </p-table>

    <!-- Movimentação -->
    <p-dialog [header]="'Movimentação: ' + movType" [(visible)]="showMov" [style]="{width:'420px'}" [modal]="true">
      <div class="flex flex-column gap-3" style="font-size:12px">
        <div><label>Insumo</label><input pInputText [(ngModel)]="movForm.description" class="w-full" placeholder="Descrição do material" /></div>
        <div class="grid">
          <div class="col-4"><label>Quantidade</label><p-inputNumber [(ngModel)]="movForm.quantity" styleClass="w-full" /></div>
          <div class="col-4"><label>Unidade</label><input pInputText [(ngModel)]="movForm.unit" class="w-full" /></div>
          <div class="col-4"><label>Valor Unit.</label><p-inputNumber [(ngModel)]="movForm.unitCost" mode="currency" currency="BRL" styleClass="w-full" /></div>
        </div>
        <div><label>Observação</label><input pInputText [(ngModel)]="movForm.notes" class="w-full" /></div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showMov = false" />
        <p-button label="Confirmar" icon="pi pi-check" (onClick)="saveMov()" />
      </ng-template>
    </p-dialog>
  `,
})
export class InventoryListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  stock = signal<any[]>([]);
  loading = signal(true);
  showMov = false;
  movType = 'ENTRADA';
  movForm: any = {};

  private get pid() { return this.route.parent?.snapshot.paramMap.get('id'); }

  ngOnInit() {
    this.http.get<any>(`/projects/${this.pid}/inventory`).subscribe({ next: r => { this.stock.set(r.content || r || []); this.loading.set(false); }, error: () => this.loading.set(false) });
  }

  openMov(type: string) { this.movType = type; this.movForm = { type }; this.showMov = true; }

  saveMov() {
    this.http.post(`/projects/${this.pid}/inventory`, { ...this.movForm, type: this.movType }).subscribe({
      next: () => { this.showMov = false; this.messages.add({ severity: 'success', summary: `${this.movType} registrada` }); this.ngOnInit(); },
    });
  }
}
