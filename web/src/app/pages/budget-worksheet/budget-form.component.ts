import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { CalendarModule } from 'primeng/calendar';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-budget-form',
  standalone: true,
  imports: [FormsModule, InputTextModule, CalendarModule, InputNumberModule, DropdownModule, ButtonModule],
  template: `
    <h3 style="margin:0 0 1rem">Novo Orçamento</h3>
    <div class="grid">
      <div class="col-12 md:col-4"><label>Código *</label><input pInputText [(ngModel)]="form.code" class="w-full" placeholder="ORC-001" /></div>
      <div class="col-12 md:col-8"><label>Título *</label><input pInputText [(ngModel)]="form.title" class="w-full" placeholder="Orçamento da Obra X" /></div>
      <div class="col-12 md:col-6"><label>Cliente *</label><input pInputText [(ngModel)]="form.customerName" class="w-full" /></div>
      <div class="col-12 md:col-6"><label>Valor Estimado</label><p-inputNumber [(ngModel)]="form.totalAmount" mode="currency" currency="BRL" styleClass="w-full" /></div>
      <div class="col-12 md:col-4"><label>Início *</label><p-calendar [(ngModel)]="form.startDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
      <div class="col-12 md:col-4"><label>Término</label><p-calendar [(ngModel)]="form.endDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
      <div class="col-12 md:col-4"><label>Arredondamento</label><p-dropdown [(ngModel)]="form.roundingMethod" [options]="roundingOptions" styleClass="w-full" /></div>
      <div class="col-12 flex gap-2 mt-2">
        <p-button label="Criar e Abrir Planilha" icon="pi pi-check" (onClick)="save()" [loading]="saving()" [disabled]="!form.code || !form.title || !form.customerName || !form.startDate" />
        <p-button label="Cancelar" severity="secondary" (onClick)="cancel()" />
      </div>
    </div>
  `,
})
export class BudgetFormComponent {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private router = inject(Router);
  private messages = inject(MessageService);
  saving = signal(false);
  form: any = { status: 'DRAFT', roundingMethod: 'TRUNCATE', decimalPlaces: 4, totalAmount: 0 };
  roundingOptions = [
    { label: 'Truncamento (TCU)', value: 'TRUNCATE' },
    { label: 'Arredondamento ABNT', value: 'ROUND_ABNT' },
    { label: 'Arredondamento Simples', value: 'ROUND_SIMPLE' },
  ];

  save() {
    this.saving.set(true);
    const id = this.route.parent?.snapshot.paramMap.get('id');
    const body = {
      ...this.form,
      startDate: this.form.startDate instanceof Date ? this.form.startDate.toISOString().slice(0, 10) : this.form.startDate,
      endDate: this.form.endDate instanceof Date ? this.form.endDate.toISOString().slice(0, 10) : this.form.endDate,
    };
    this.http.post<any>(`/projects/${id}/budgets`, body).subscribe({
      next: (res) => {
        this.messages.add({ severity: 'success', summary: 'Orçamento criado' });
        this.router.navigate(['..', res.id], { relativeTo: this.route });
      },
      error: () => this.saving.set(false),
    });
  }

  cancel() { this.router.navigate(['..'], { relativeTo: this.route }); }
}
