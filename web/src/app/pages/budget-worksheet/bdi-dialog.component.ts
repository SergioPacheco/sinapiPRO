import { Component, inject, input, output, signal, effect } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { DialogModule } from 'primeng/dialog';
import { InputNumberModule } from 'primeng/inputnumber';
import { TabViewModule } from 'primeng/tabview';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';

/**
 * BDI Dialog — Fórmula TCU:
 * BDI = [(1+AC+S+R+G) × (1+DF) × (1+L)] / (1-I) - 1
 */
@Component({
  selector: 'app-bdi-dialog',
  standalone: true,
  imports: [DecimalPipe, FormsModule, DialogModule, InputNumberModule, TabViewModule, ButtonModule],
  template: `
    <p-dialog header="BDI — Fórmula TCU" [visible]="visible()" (visibleChange)="visibleChange.emit($event)" [style]="{width:'600px'}" [modal]="true">
      <p class="text-muted text-sm mb-3">BDI = [(1+AC+S+R+G) × (1+DF) × (1+L)] / (1-I) - 1</p>
      <p-tabView>
        @for (tab of tabs; track tab.type) {
          <p-tabPanel [header]="tab.label">
            <div class="flex flex-column gap-2">
              <div class="grid align-items-center"><div class="col-8">Administração Central (AC)</div><div class="col-4"><p-inputNumber [(ngModel)]="tab.data.ac" [maxFractionDigits]="4" suffix="%" styleClass="w-full" (onInput)="recalc(tab)" /></div></div>
              <div class="grid align-items-center"><div class="col-8">Seguros (S)</div><div class="col-4"><p-inputNumber [(ngModel)]="tab.data.s" [maxFractionDigits]="4" suffix="%" styleClass="w-full" (onInput)="recalc(tab)" /></div></div>
              <div class="grid align-items-center"><div class="col-8">Riscos (R)</div><div class="col-4"><p-inputNumber [(ngModel)]="tab.data.r" [maxFractionDigits]="4" suffix="%" styleClass="w-full" (onInput)="recalc(tab)" /></div></div>
              <div class="grid align-items-center"><div class="col-8">Garantias (G)</div><div class="col-4"><p-inputNumber [(ngModel)]="tab.data.g" [maxFractionDigits]="4" suffix="%" styleClass="w-full" (onInput)="recalc(tab)" /></div></div>
              <div class="grid align-items-center"><div class="col-8">Despesas Financeiras (DF)</div><div class="col-4"><p-inputNumber [(ngModel)]="tab.data.df" [maxFractionDigits]="4" suffix="%" styleClass="w-full" (onInput)="recalc(tab)" /></div></div>
              <div class="grid align-items-center"><div class="col-8">Lucro (L)</div><div class="col-4"><p-inputNumber [(ngModel)]="tab.data.l" [maxFractionDigits]="4" suffix="%" styleClass="w-full" (onInput)="recalc(tab)" /></div></div>
              <div class="grid align-items-center"><div class="col-8">Tributos — I (ISS+PIS+COFINS+CPRB)</div><div class="col-4"><p-inputNumber [(ngModel)]="tab.data.i" [maxFractionDigits]="4" suffix="%" styleClass="w-full" (onInput)="recalc(tab)" /></div></div>
              <div class="border-top-1 surface-border pt-2 mt-2 flex justify-content-between"><strong>BDI Calculado:</strong><strong class="text-primary text-lg">{{ tab.bdi | number:'1.4-4' }}%</strong></div>
            </div>
          </p-tabPanel>
        }
      </p-tabView>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="visibleChange.emit(false)" />
        <p-button label="Salvar" icon="pi pi-check" (onClick)="save()" [loading]="saving()" />
      </ng-template>
    </p-dialog>
  `,
})
export class BdiDialogComponent {
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  budgetId = input.required<string>();
  visible = input(false);
  visibleChange = output<boolean>();
  saved = output<void>();
  saving = signal(false);

  tabs = [
    { type: 'ALL', label: 'Geral', data: { ac: 0, s: 0, r: 0, g: 0, df: 0, l: 0, i: 0 }, bdi: 0 },
    { type: 'MATERIAL', label: 'Material', data: { ac: 0, s: 0, r: 0, g: 0, df: 0, l: 0, i: 0 }, bdi: 0 },
    { type: 'LABOR', label: 'Mão de Obra', data: { ac: 0, s: 0, r: 0, g: 0, df: 0, l: 0, i: 0 }, bdi: 0 },
    { type: 'EQUIPMENT', label: 'Equipamento', data: { ac: 0, s: 0, r: 0, g: 0, df: 0, l: 0, i: 0 }, bdi: 0 },
  ];

  constructor() {
    effect(() => { if (this.visible()) this.loadBdi(); });
  }

  loadBdi() {
    for (const tab of this.tabs) {
      this.http.get<any>(`/budgets/${this.budgetId()}/bdi?itemType=${tab.type}`).subscribe(b => {
        tab.data = { ac: b.administration || 0, s: b.socialCharges || 0, r: b.risks || 0, g: 0, df: b.financialExpenses || 0, l: b.profit || 0, i: b.taxes || 0 };
        this.recalc(tab);
      });
    }
  }

  recalc(tab: any) {
    const d = tab.data;
    const ac = (d.ac || 0) / 100, s = (d.s || 0) / 100, r = (d.r || 0) / 100, g = (d.g || 0) / 100;
    const df = (d.df || 0) / 100, l = (d.l || 0) / 100, i = (d.i || 0) / 100;
    tab.bdi = i >= 1 ? 0 : (((1 + ac + s + r + g) * (1 + df) * (1 + l)) / (1 - i) - 1) * 100;
  }

  save() {
    this.saving.set(true);
    const batch = this.tabs.map(t => ({ itemType: t.type, administration: t.data.ac, profit: t.data.l, taxes: t.data.i, socialCharges: t.data.s, financialExpenses: t.data.df, risks: t.data.r }));
    this.http.put(`/budgets/${this.budgetId()}/bdi/batch`, batch).subscribe({
      next: () => { this.saving.set(false); this.messages.add({ severity: 'success', summary: 'BDI salvo' }); this.saved.emit(); this.visibleChange.emit(false); },
      error: () => this.saving.set(false),
    });
  }
}
