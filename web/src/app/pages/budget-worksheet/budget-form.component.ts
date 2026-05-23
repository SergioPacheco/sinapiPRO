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
      <div class="col-12 md:col-4"><label>Código</label><input pInputText [(ngModel)]="form.code" class="w-full" /></div>
      <div class="col-12 md:col-8"><label>Título</label><input pInputText [(ngModel)]="form.title" class="w-full" /></div>
      <div class="col-12 md:col-6"><label>Cliente</label><input pInputText [(ngModel)]="form.customerName" class="w-full" /></div>
      <div class="col-12 md:col-6"><label>Valor Total</label><p-inputNumber [(ngModel)]="form.totalAmount" mode="currency" currency="BRL" styleClass="w-full" /></div>
      <div class="col-12 md:col-6"><label>Início</label><p-calendar [(ngModel)]="form.startDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
      <div class="col-12 md:col-6"><label>Término</label><p-calendar [(ngModel)]="form.endDate" dateFormat="dd/mm/yy" styleClass="w-full" /></div>
      <div class="col-12 md:col-6"><label>Método de Arredondamento</label><p-dropdown [(ngModel)]="form.roundingMethod" [options]="roundingOptions" styleClass="w-full" /></div>
      <div class="col-12 md:col-6"><label>Casas Decimais</label><p-dropdown [(ngModel)]="form.decimalPlaces" [options]="decimalOptions" styleClass="w-full" /></div>
      <div class="col-12 flex gap-2">
        <p-button label="Salvar" icon="pi pi-check" (onClick)="save()" [loading]="saving()" />
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
  form: any = { status: 'DRAFT', roundingMethod: 'TRUNCATE', decimalPlaces: 4 };
  roundingOptions = [
    { label: 'Truncamento (TCU)', value: 'TRUNCATE' },
    { label: 'Arredondamento ABNT', value: 'ROUND_ABNT' },
    { label: 'Arredondamento Simples', value: 'ROUND_SIMPLE' },
  ];
  decimalOptions = [
    { label: '2 casas', value: 2 },
    { label: '4 casas', value: 4 },
    { label: '6 casas', value: 6 },
  ];

  save() {
    this.saving.set(true);
    const id = this.route.parent?.snapshot.paramMap.get('id');
    this.http.post(`/projects/${id}/budgets`, this.form).subscribe({
      next: () => { this.messages.add({ severity: 'success', summary: 'Orçamento criado' }); this.router.navigate(['..'], { relativeTo: this.route }); },
      error: () => this.saving.set(false),
    });
  }

  cancel() { this.router.navigate(['..'], { relativeTo: this.route }); }
}
