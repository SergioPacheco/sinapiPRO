import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { CalendarModule } from 'primeng/calendar';
import { InputNumberModule } from 'primeng/inputnumber';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { CheckboxModule } from 'primeng/checkbox';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-measurement-form',
  standalone: true,
  imports: [FormsModule, InputTextModule, CalendarModule, InputNumberModule, ButtonModule, TableModule, CheckboxModule],
  template: `
    <h3 style="margin:0 0 1rem">Nova Medição</h3>
    <div class="grid">
      <div class="col-12 md:col-3"><label>Número</label><p-inputNumber [(ngModel)]="form.number" styleClass="w-full" /></div>
      <div class="col-12 md:col-3"><label>Início Período</label><p-calendar [(ngModel)]="form.periodStart" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
      <div class="col-12 md:col-3"><label>Fim Período</label><p-calendar [(ngModel)]="form.periodEnd" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
      <div class="col-12 md:col-3"><label>Retenção (%)</label><p-inputNumber [(ngModel)]="form.retentionPct" suffix="%" styleClass="w-full" /></div>
    </div>
    <h4>Itens Disponíveis</h4>
    <p-table [value]="availableItems()" [loading]="loading()" styleClass="p-datatable-sm" dataKey="budgetItemId">
      <ng-template pTemplate="header"><tr><th style="width:50px"></th><th>Item</th><th style="width:80px">Unid.</th><th style="width:120px">Qtd.</th></tr></ng-template>
      <ng-template pTemplate="body" let-item let-i="rowIndex">
        <tr>
          <td><p-checkbox [(ngModel)]="item.selected" [binary]="true" /></td>
          <td>{{ item.description }}</td>
          <td>{{ item.unit }}</td>
          <td><p-inputNumber [(ngModel)]="item.quantity" styleClass="w-full" [minFractionDigits]="2" /></td>
        </tr>
      </ng-template>
    </p-table>
    <div class="flex gap-2 mt-3">
      <p-button label="Salvar" icon="pi pi-check" (onClick)="save()" [loading]="saving()" />
      <p-button label="Cancelar" severity="secondary" (onClick)="cancel()" />
    </div>
  `,
})
export class MeasurementFormComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private router = inject(Router);
  private messages = inject(MessageService);
  loading = signal(true);
  saving = signal(false);
  availableItems = signal<any[]>([]);
  form: any = { retentionPct: 5 };

  ngOnInit() {
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.get<any[]>(`/projects/${id}/measurements/available-items`).subscribe({
      next: items => { this.availableItems.set(items.map(i => ({ ...i, selected: false, quantity: 0 }))); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  save() {
    this.saving.set(true);
    const id = this.route.parent?.snapshot.paramMap.get('id');
    const payload = { ...this.form, items: this.availableItems().filter(i => i.selected).map(i => ({ budgetItemId: i.budgetItemId, quantity: i.quantity })) };
    this.http.post(`/projects/${id}/measurements`, payload).subscribe({
      next: () => { this.messages.add({ severity: 'success', summary: 'Medição criada' }); this.router.navigate(['..'], { relativeTo: this.route }); },
      error: () => this.saving.set(false),
    });
  }

  cancel() { this.router.navigate(['..'], { relativeTo: this.route }); }
}
