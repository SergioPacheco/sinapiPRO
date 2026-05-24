import { Component, inject, input, output, signal, computed } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { MessageService } from 'primeng/api';

interface SpreadsheetRow {
  idx: number;
  code: string;
  description: string;
  unit: string;
  quantity: number | null;
  unitCost: number | null;
  total: number;
  compositionId: string | null;
  stageId: string | null;
  status: 'empty' | 'editing' | 'resolved' | 'error';
  suggestions?: any[];
}

/**
 * Spreadsheet Editor — Digitação Rápida estilo Strato/Excel
 *
 * O usuário digita código ou descrição na célula → autocomplete busca composições
 * → ao selecionar, preenche unidade e custo automaticamente
 * → usuário informa quantidade → total calcula
 * → Enter avança para próxima linha
 * → Botão "Salvar Tudo" envia batch para o backend
 */
@Component({
  selector: 'app-spreadsheet-editor',
  standalone: true,
  imports: [DecimalPipe, FormsModule, TableModule, InputTextModule, InputNumberModule, AutoCompleteModule, DropdownModule, ButtonModule, TooltipModule],
  template: `
    <div class="flex align-items-center justify-content-between mb-2">
      <div class="flex align-items-center gap-2">
        <span class="text-muted text-sm">Etapa:</span>
        <p-dropdown [(ngModel)]="selectedStageId" [options]="stages()" optionLabel="label" optionValue="value" placeholder="Selecione" styleClass="w-15rem" (onChange)="onStageChange()" />
      </div>
      <div class="flex gap-2">
        <span class="text-muted text-sm">{{ resolvedCount() }} itens prontos</span>
        <p-button label="Salvar Tudo" icon="pi pi-save" size="small" (onClick)="saveAll()" [loading]="saving()" [disabled]="resolvedCount() === 0" />
        <p-button icon="pi pi-plus" pTooltip="Adicionar 10 linhas" size="small" severity="secondary" [text]="true" (onClick)="addRows(10)" />
      </div>
    </div>

    <div class="spreadsheet-container">
      <table class="spreadsheet">
        <thead>
          <tr>
            <th class="row-num">#</th>
            <th class="col-code">Código</th>
            <th class="col-desc">Descrição / Busca</th>
            <th class="col-unit">Un.</th>
            <th class="col-qty">Quantidade</th>
            <th class="col-cost">Custo Unit.</th>
            <th class="col-total">Total</th>
            <th class="col-action"></th>
          </tr>
        </thead>
        <tbody>
          @for (row of rows; track row.idx) {
            <tr [class.row-resolved]="row.status === 'resolved'" [class.row-error]="row.status === 'error'">
              <td class="row-num">{{ row.idx + 1 }}</td>
              <td class="col-code">
                <input class="cell-input font-mono" [(ngModel)]="row.code" (keydown.enter)="onCodeEnter(row)" (blur)="onCodeBlur(row)" placeholder="Código..." [disabled]="row.status === 'resolved'" />
              </td>
              <td class="col-desc">
                <p-autoComplete [(ngModel)]="row.description" [suggestions]="row.suggestions || []" (completeMethod)="searchComp(row, $event)" (onSelect)="onCompSelect(row, $event)" field="description" styleClass="w-full cell-autocomplete" [inputStyleClass]="'cell-input'" placeholder="Buscar composição..." [disabled]="row.status === 'resolved'" [forceSelection]="false" />
              </td>
              <td class="col-unit"><input class="cell-input" [(ngModel)]="row.unit" [disabled]="true" /></td>
              <td class="col-qty">
                <input type="number" class="cell-input text-right" [(ngModel)]="row.quantity" (input)="calcTotal(row)" (keydown.enter)="advanceRow(row)" step="0.01" />
              </td>
              <td class="col-cost">
                <input type="number" class="cell-input text-right" [(ngModel)]="row.unitCost" (input)="calcTotal(row)" step="0.01" />
              </td>
              <td class="col-total text-right font-mono">{{ row.total | number:'1.2-2' }}</td>
              <td class="col-action">
                @if (row.status === 'resolved') {
                  <i class="pi pi-check-circle text-green-500" style="font-size:0.9rem"></i>
                }
                @if (row.compositionId) {
                  <i class="pi pi-times cursor-pointer text-red-400" style="font-size:0.8rem" pTooltip="Limpar" (click)="clearRow(row)"></i>
                }
              </td>
            </tr>
          }
        </tbody>
        <tfoot>
          <tr>
            <td colspan="6" class="text-right"><strong>Total Planilha:</strong></td>
            <td class="text-right font-mono"><strong>{{ grandTotal() | number:'1.2-2' }}</strong></td>
            <td></td>
          </tr>
        </tfoot>
      </table>
    </div>
  `,
  styles: [`
    .spreadsheet-container { overflow: auto; max-height: calc(100vh - 260px); border: 1px solid var(--surface-border); border-radius: 6px; }
    .spreadsheet { width: 100%; border-collapse: collapse; font-size: 0.85rem; }
    .spreadsheet th { background: var(--surface-100); padding: 6px 8px; text-align: left; border-bottom: 2px solid var(--surface-border); position: sticky; top: 0; z-index: 1; }
    .spreadsheet td { padding: 2px 4px; border-bottom: 1px solid var(--surface-border); }
    .spreadsheet tr:hover { background: var(--surface-50); }
    .row-resolved { background: var(--green-50) !important; }
    .row-error { background: var(--red-50) !important; }
    .row-num { width: 35px; text-align: center; color: var(--text-color-secondary); font-size: 0.75rem; }
    .col-code { width: 110px; }
    .col-desc { min-width: 250px; }
    .col-unit { width: 55px; }
    .col-qty { width: 100px; }
    .col-cost { width: 110px; }
    .col-total { width: 120px; }
    .col-action { width: 40px; text-align: center; }
    .cell-input { width: 100%; border: none; background: transparent; padding: 4px 6px; outline: none; font-size: 0.85rem; }
    .cell-input:focus { background: var(--surface-0); box-shadow: inset 0 0 0 1px var(--primary-color); border-radius: 3px; }
    .cell-input:disabled { color: var(--text-color-secondary); }
    :host ::ng-deep .cell-autocomplete { width: 100%; }
    :host ::ng-deep .cell-autocomplete input { border: none !important; background: transparent !important; padding: 4px 6px !important; font-size: 0.85rem !important; box-shadow: none !important; }
    :host ::ng-deep .cell-autocomplete input:focus { background: var(--surface-0) !important; box-shadow: inset 0 0 0 1px var(--primary-color) !important; border-radius: 3px !important; }
  `],
})
export class SpreadsheetEditorComponent {
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  budgetId = input.required<string>();
  stages = input.required<{ label: string; value: string }[]>();
  saved = output<void>();

  selectedStageId: string | null = null;
  saving = signal(false);
  rows: SpreadsheetRow[] = [];

  resolvedCount = computed(() => this.rows.filter(r => r.status === 'resolved' && r.compositionId && r.quantity).length);
  grandTotal = computed(() => this.rows.reduce((sum, r) => sum + (r.total || 0), 0));

  constructor() {
    this.rows = this.createRows(20);
  }

  onStageChange() { /* stage selected, ready to add items */ }

  // --- Busca por código (Enter na célula código) ---
  onCodeEnter(row: SpreadsheetRow) {
    if (!row.code) return;
    this.http.get<any>(`/compositions?search=${encodeURIComponent(row.code)}&size=1`).subscribe({
      next: res => {
        const items = res.content || res;
        if (items.length > 0) this.fillRow(row, items[0]);
        else row.status = 'error';
      },
    });
  }

  onCodeBlur(row: SpreadsheetRow) {
    if (row.code && !row.compositionId) this.onCodeEnter(row);
  }

  // --- Busca por descrição (autocomplete) ---
  searchComp(row: SpreadsheetRow, event: any) {
    this.http.get<any>(`/compositions?search=${encodeURIComponent(event.query)}&size=10`).subscribe({
      next: res => { row.suggestions = res.content || res; },
    });
  }

  onCompSelect(row: SpreadsheetRow, event: any) {
    this.fillRow(row, event);
  }

  // --- Preencher linha com composição selecionada ---
  private fillRow(row: SpreadsheetRow, comp: any) {
    row.compositionId = comp.id;
    row.code = comp.sinapiCode;
    row.description = comp.description;
    row.unit = comp.unit;
    row.status = 'resolved';
    row.stageId = this.selectedStageId;
    // Se já tem quantidade, calcula total
    if (row.quantity && row.unitCost) this.calcTotal(row);
  }

  calcTotal(row: SpreadsheetRow) {
    row.total = (row.quantity || 0) * (row.unitCost || 0);
  }

  advanceRow(row: SpreadsheetRow) {
    const nextIdx = row.idx + 1;
    if (nextIdx >= this.rows.length) this.addRows(5);
    // Focus will naturally move to next row via tab order
  }

  clearRow(row: SpreadsheetRow) {
    row.code = ''; row.description = ''; row.unit = ''; row.quantity = null;
    row.unitCost = null; row.total = 0; row.compositionId = null; row.status = 'empty';
  }

  addRows(count: number) {
    const start = this.rows.length;
    this.rows.push(...this.createRows(count, start));
  }

  // --- Salvar tudo (batch) ---
  saveAll() {
    if (!this.selectedStageId) {
      this.messages.add({ severity: 'warn', summary: 'Selecione uma etapa' });
      return;
    }
    const items = this.rows
      .filter(r => r.status === 'resolved' && r.compositionId && r.quantity)
      .map(r => ({ compositionId: r.compositionId, quantity: r.quantity, unitCost: r.unitCost || undefined }));

    if (items.length === 0) return;

    this.saving.set(true);
    this.http.post<any>(`/budgets/${this.budgetId()}/stages/${this.selectedStageId}/items/bulk`, items).subscribe({
      next: res => {
        this.saving.set(false);
        this.messages.add({ severity: 'success', summary: `${res.added} itens adicionados` });
        // Limpar linhas salvas e resetar
        this.rows = this.createRows(20);
        this.saved.emit();
      },
      error: () => this.saving.set(false),
    });
  }

  private createRows(count: number, startIdx = 0): SpreadsheetRow[] {
    return Array.from({ length: count }, (_, i) => ({
      idx: startIdx + i,
      code: '', description: '', unit: '', quantity: null, unitCost: null, total: 0,
      compositionId: null, stageId: null, status: 'empty' as const, suggestions: [],
    }));
  }
}
