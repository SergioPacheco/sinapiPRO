import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DecimalPipe, NgTemplateOutlet } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TreeModule } from 'primeng/tree';
import { TreeNode } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { DropdownModule } from 'primeng/dropdown';
import { MenuModule } from 'primeng/menu';
import { TooltipModule } from 'primeng/tooltip';
import { SplitterModule } from 'primeng/splitter';
import { ToolbarModule } from 'primeng/toolbar';
import { MessageService } from 'primeng/api';
import { StatusTagComponent, EmptyStateComponent, ConfirmActionComponent } from '../../shared/components';
import { BdiDialogComponent } from './bdi-dialog.component';

interface GridRow {
  id?: string;
  stageId: string;
  stageName: string;
  isStage: boolean;
  isSubItem?: boolean;
  code: string;
  description: string;
  unit: string;
  quantity: number | null;
  unitCost: number | null;
  total: number;
  compositionId?: string;
  editing: boolean;
  dirty: boolean;
  expanded?: boolean;
}

@Component({
  selector: 'app-budget-worksheet',
  standalone: true,
  imports: [
    DecimalPipe, NgTemplateOutlet, FormsModule, TreeModule, ButtonModule, DialogModule,
    InputTextModule, InputNumberModule, AutoCompleteModule, DropdownModule,
    MenuModule, TooltipModule, SplitterModule, ToolbarModule,
    StatusTagComponent, EmptyStateComponent, ConfirmActionComponent, BdiDialogComponent,
  ],
  template: `
    <!-- Toolbar -->
    <p-toolbar styleClass="mb-0" [style]="{'border-bottom':'none','border-radius':'6px 6px 0 0'}">
      <div class="p-toolbar-group-start flex align-items-center gap-2">
        <h3 style="margin:0">Orçamento</h3>
        <sp-status [status]="budgetStatus()" />
      </div>
      <div class="p-toolbar-group-center">
        <span class="text-muted text-sm">
          Direto: <strong>{{ summary().directCost | number:'1.2-2' }}</strong> |
          BDI: <strong>{{ summary().bdiPct | number:'1.2-2' }}%</strong> |
          Total: <strong class="text-primary">{{ summary().total | number:'1.2-2' }}</strong>
        </span>
      </div>
      <div class="p-toolbar-group-end flex gap-1">
        @if (isEditable()) {
          <p-button icon="pi pi-folder-plus" pTooltip="Nova Etapa" [text]="true" (onClick)="showStageDialog = true" />
        }
        <p-button icon="pi pi-percentage" pTooltip="BDI" [text]="true" (onClick)="showBdi = true" />
        <p-button icon="pi pi-ellipsis-v" [text]="true" (onClick)="menu.toggle($event)" />
        <p-menu #menu [popup]="true" [model]="menuItems" />
      </div>
    </p-toolbar>

    @if (!loading() && gridRows().length === 0) {
      <sp-empty title="Planilha vazia" message="Crie a primeira etapa para começar a orçar." icon="folder" actionLabel="Nova Etapa" (action)="showStageDialog = true" />
    } @else {
      <!-- Planilha principal (estilo Excel) -->
      <div class="sheet-container">
        <table class="sheet">
          <thead>
            <tr>
              <th class="sh-act"></th>
              <th class="sh-code">Código</th>
              <th class="sh-desc">Descrição</th>
              <th class="sh-unit">Un.</th>
              <th class="sh-qty">Quantidade</th>
              <th class="sh-cost">Custo Unit.</th>
              <th class="sh-total">Total</th>
              @if (isEditable()) { <th class="sh-actions"></th> }
            </tr>
          </thead>
          <tbody>
            @for (row of gridRows(); track row.id || $index) {
              @if (row.isStage) {
                <!-- Linha de Etapa (header de grupo) -->
                <tr class="stage-row">
                  <td [attr.colspan]="isEditable() ? 8 : 7">
                    <div class="flex align-items-center gap-2">
                      <i class="pi pi-folder text-primary" style="font-size:0.85rem"></i>
                      <strong>{{ row.stageName }}</strong>
                      <span class="text-muted text-xs ml-auto">{{ row.total | number:'1.2-2' }}</span>
                      @if (isEditable()) {
                        <i class="pi pi-plus cursor-pointer text-primary ml-2" style="font-size:0.8rem" pTooltip="Inserir item" (click)="insertNewRow(row.stageId)"></i>
                        <i class="pi pi-trash cursor-pointer text-red-400 ml-1" style="font-size:0.8rem" pTooltip="Excluir etapa" (click)="deleteStage(row.stageId)"></i>
                      }
                    </div>
                  </td>
                </tr>
              } @else {
                <!-- Linha de Item (editável inline) -->
                <tr class="item-row" [class.row-dirty]="row.dirty" [class.row-new]="!row.id" [class.row-sub]="row.isSubItem">
                  <td class="sh-act">
                    @if (row.compositionId && row.id && !row.isSubItem) {
                      <i [class]="row.expanded ? 'pi pi-minus-circle cursor-pointer text-orange-500' : 'pi pi-plus-circle cursor-pointer text-blue-500'" style="font-size:0.85rem" [pTooltip]="row.expanded ? 'Recolher' : 'Expandir insumos'" (click)="toggleExpand(row)"></i>
                    }
                  </td>
                  <td class="sh-code">
                    @if (row.editing && !row.compositionId) {
                      <input class="cell" [(ngModel)]="row.code" (keydown.enter)="resolveByCode(row)" placeholder="Código..." />
                    } @else {
                      <span class="font-mono text-xs">{{ row.code }}</span>
                    }
                  </td>
                  <td class="sh-desc">
                    @if (row.editing && !row.compositionId) {
                      <p-autoComplete [(ngModel)]="row.description" [suggestions]="suggestions()" (completeMethod)="searchComp($event)" (onSelect)="onSelectComp(row, $event)" field="description" styleClass="w-full" inputStyleClass="cell" placeholder="Buscar..." [forceSelection]="false" />
                    } @else {
                      <span>{{ row.description }}</span>
                    }
                  </td>
                  <td class="sh-unit"><span class="text-muted">{{ row.unit }}</span></td>
                  <td class="sh-qty">
                    @if (isEditable()) {
                      <input type="number" class="cell text-right" [(ngModel)]="row.quantity" (input)="onCellEdit(row)" (keydown.enter)="onEnter(row)" step="0.01" min="0" />
                    } @else {
                      <span class="text-right">{{ row.quantity | number:'1.2-4' }}</span>
                    }
                  </td>
                  <td class="sh-cost">
                    @if (isEditable()) {
                      <input type="number" class="cell text-right" [(ngModel)]="row.unitCost" (input)="onCellEdit(row)" step="0.01" min="0" />
                    } @else {
                      <span class="text-right">{{ row.unitCost | number:'1.2-2' }}</span>
                    }
                  </td>
                  <td class="sh-total text-right font-mono">{{ row.total | number:'1.2-2' }}</td>
                  @if (isEditable()) {
                    <td class="sh-actions">
                      @if (row.dirty && row.compositionId) {
                        <i class="pi pi-circle-fill text-orange-400" style="font-size:0.5rem" pTooltip="Não salvo"></i>
                      }
                      <i class="pi pi-times cursor-pointer text-red-400 ml-1" style="font-size:0.75rem" (click)="removeRow(row)"></i>
                    </td>
                  }
                </tr>
              }
            }
            <!-- Linha vazia para inserção rápida (sempre no final de cada etapa) -->
          </tbody>
        </table>
      </div>

      <!-- Barra de ações inferior -->
      @if (isEditable() && dirtyCount() > 0) {
        <div class="save-bar">
          <span>{{ dirtyCount() }} alterações não salvas</span>
          <p-button label="Salvar" icon="pi pi-save" size="small" (onClick)="saveAll()" [loading]="saving()" />
        </div>
      }
    }

    <!-- Stage Dialog -->
    <p-dialog header="Nova Etapa" [(visible)]="showStageDialog" [style]="{width:'380px'}" [modal]="true">
      <div class="flex flex-column gap-3">
        <div><label>Nome</label><input pInputText [(ngModel)]="newStageName" class="w-full" placeholder="Ex: Infraestrutura" (keydown.enter)="createStage()" /></div>
        <div><label>Pai (opcional)</label>
          <p-dropdown [(ngModel)]="newStageParentId" [options]="stageOpts()" optionLabel="label" optionValue="value" placeholder="Raiz" [showClear]="true" styleClass="w-full" />
        </div>
      </div>
      <ng-template pTemplate="footer">
        <p-button label="Cancelar" severity="secondary" (onClick)="showStageDialog = false" />
        <p-button label="Criar" icon="pi pi-check" (onClick)="createStage()" [disabled]="!newStageName" />
      </ng-template>
    </p-dialog>

    <app-bdi-dialog [visible]="showBdi" (visibleChange)="showBdi = $event" [budgetId]="budgetId!" (saved)="loadWorksheet()" />
    <sp-confirm #confirmApprove header="Aprovar" message="Aprovar este orçamento?" confirmLabel="Aprovar" [severity]="'success'" (confirmed)="changeStatus('APPROVED')" />
    <sp-confirm #confirmEffectuate header="Efetivar" message="Efetivar para execução?" confirmLabel="Efetivar" [severity]="'warn'" (confirmed)="effectuate()" />
  `,
  styles: [`
    .sheet-container { border: 1px solid var(--surface-border); border-top: none; border-radius: 0 0 6px 6px; overflow: auto; max-height: calc(100vh - 200px); }
    .sheet { width: 100%; border-collapse: collapse; font-size: 0.85rem; }
    .sheet thead { position: sticky; top: 0; z-index: 2; }
    .sheet th { background: var(--surface-100); padding: 8px 10px; text-align: left; border-bottom: 2px solid var(--surface-border); font-weight: 600; font-size: 0.8rem; text-transform: uppercase; color: var(--text-color-secondary); }
    .sheet td { padding: 0; border-bottom: 1px solid var(--surface-50); }
    .stage-row td { background: var(--surface-50); padding: 6px 10px !important; border-bottom: 1px solid var(--surface-border); }
    .item-row:hover { background: var(--highlight-bg); }
    .item-row.row-dirty { background: var(--orange-50); }
    .item-row.row-new { background: var(--blue-50); }
    .item-row.row-sub { background: var(--surface-50); font-size: 0.8rem; color: var(--text-color-secondary); }
    .sh-act { width: 30px; text-align: center; padding: 0 2px !important; }
    .sh-code { width: 100px; }
    .sh-desc { min-width: 280px; }
    .sh-unit { width: 50px; }
    .sh-qty { width: 100px; }
    .sh-cost { width: 110px; }
    .sh-total { width: 120px; padding-right: 10px !important; }
    .sh-actions { width: 50px; text-align: center; padding: 0 4px !important; }
    .cell { width: 100%; border: none; background: transparent; padding: 7px 10px; outline: none; font-size: 0.85rem; }
    .cell:focus { background: var(--surface-0); box-shadow: inset 0 0 0 2px var(--primary-color); }
    :host ::ng-deep .p-autocomplete { width: 100%; }
    :host ::ng-deep .p-autocomplete input.cell { border: none !important; box-shadow: none !important; padding: 7px 10px !important; }
    :host ::ng-deep .p-autocomplete input.cell:focus { box-shadow: inset 0 0 0 2px var(--primary-color) !important; }
    .save-bar { display: flex; align-items: center; justify-content: space-between; padding: 8px 12px; background: var(--orange-50); border: 1px solid var(--orange-200); border-radius: 6px; margin-top: 8px; font-size: 0.85rem; }
  `],
})
export class BudgetWorksheetComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  loading = signal(true);
  budgetStatus = signal('DRAFT');
  summary = signal<any>({ directCost: 0, bdiPct: 0, total: 0 });
  gridRows = signal<GridRow[]>([]);
  suggestions = signal<any[]>([]);
  stageOpts = signal<{ label: string; value: string }[]>([]);
  saving = signal(false);

  // Dialogs
  showStageDialog = false;
  showBdi = false;
  newStageName = '';
  newStageParentId: string | null = null;

  get budgetId() { return this.route.snapshot.paramMap.get('budgetId'); }
  isEditable = computed(() => this.budgetStatus() === 'DRAFT');
  dirtyCount = computed(() => this.gridRows().filter(r => !r.isStage && r.dirty && r.compositionId && r.quantity).length);

  menuItems = [
    { label: 'Aprovar', icon: 'pi pi-check-circle', command: () => this.changeStatus('APPROVED') },
    { label: 'Efetivar', icon: 'pi pi-lock', command: () => this.effectuate() },
    { label: 'Reverter', icon: 'pi pi-undo', command: () => this.revert() },
    { separator: true },
    { label: 'Sintético PDF', icon: 'pi pi-file-pdf', command: () => window.open(`/api/v1/budgets/${this.budgetId}/reports/worksheet.pdf`, '_blank') },
    { label: 'Analítico PDF', icon: 'pi pi-file-pdf', command: () => window.open(`/api/v1/budgets/${this.budgetId}/reports/analytical.pdf`, '_blank') },
    { label: 'Curva ABC', icon: 'pi pi-chart-bar', command: () => window.open(`/api/v1/budgets/${this.budgetId}/reports/abc-services.pdf`, '_blank') },
  ];

  ngOnInit() { this.loadWorksheet(); }

  loadWorksheet() {
    this.loading.set(true);
    this.http.get<any>(`/budgets/${this.budgetId}/worksheet`).subscribe({
      next: ws => {
        this.summary.set({ directCost: ws.directCost, bdiPct: (ws.bdiPct || 0) * 100, total: ws.total });
        this.gridRows.set(this.worksheetToGrid(ws.stages));
        this.stageOpts.set(this.flatStages(ws.stages));
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
    this.http.get<any>(`/budgets/${this.budgetId}`).subscribe({
      next: b => this.budgetStatus.set(b.status || 'DRAFT'),
    });
  }

  // --- Expand/collapse composition (show inputs) ---
  toggleExpand(row: GridRow) {
    const rows = this.gridRows();
    const idx = rows.indexOf(row);
    if (row.expanded) {
      // Collapse: remove sub-items below this row
      let removeCount = 0;
      for (let i = idx + 1; i < rows.length; i++) {
        if (rows[i].isSubItem) removeCount++;
        else break;
      }
      rows.splice(idx + 1, removeCount);
      row.expanded = false;
      this.gridRows.set([...rows]);
    } else {
      // Expand: fetch composition items and insert below
      this.http.get<any>(`/compositions/${row.compositionId}`).subscribe({
        next: comp => {
          const subRows: GridRow[] = (comp.items || []).map((item: any) => ({
            isStage: false, isSubItem: true, stageId: row.stageId, stageName: '',
            code: item.code || '', description: '  ↳ ' + item.description,
            unit: item.unit || '', quantity: item.coefficient, unitCost: null, total: 0,
            editing: false, dirty: false,
          }));
          rows.splice(idx + 1, 0, ...subRows);
          row.expanded = true;
          this.gridRows.set([...rows]);
        },
      });
    }
  }

  // --- Inline editing ---
  onCellEdit(row: GridRow) {
    row.total = (row.quantity || 0) * (row.unitCost || 0);
    row.dirty = true;
  }

  onEnter(row: GridRow) {
    // Se a linha está completa, inserir nova linha abaixo
    if (row.compositionId && row.quantity) {
      this.insertNewRow(row.stageId, row);
    }
  }

  resolveByCode(row: GridRow) {
    if (!row.code) return;
    this.http.get<any>(`/compositions?search=${encodeURIComponent(row.code)}&size=1`).subscribe({
      next: res => {
        const items = res.content || res;
        if (items.length > 0) this.fillRow(row, items[0]);
      },
    });
  }

  searchComp(event: any) {
    this.http.get<any>(`/compositions?search=${encodeURIComponent(event.query)}&size=10`).subscribe({
      next: res => this.suggestions.set(res.content || res),
    });
  }

  onSelectComp(row: GridRow, comp: any) {
    this.fillRow(row, comp);
  }

  private fillRow(row: GridRow, comp: any) {
    row.compositionId = comp.id;
    row.code = comp.sinapiCode;
    row.description = comp.description;
    row.unit = comp.unit;
    row.editing = false;
    row.dirty = true;
  }

  insertNewRow(stageId: string, afterRow?: GridRow) {
    const rows = this.gridRows();
    const newRow: GridRow = {
      stageId, stageName: '', isStage: false, code: '', description: '', unit: '',
      quantity: null, unitCost: null, total: 0, editing: true, dirty: false,
    };
    if (afterRow) {
      const idx = rows.indexOf(afterRow);
      rows.splice(idx + 1, 0, newRow);
    } else {
      // Inserir no final da etapa
      let insertIdx = rows.length;
      for (let i = rows.length - 1; i >= 0; i--) {
        if (rows[i].stageId === stageId) { insertIdx = i + 1; break; }
      }
      rows.splice(insertIdx, 0, newRow);
    }
    this.gridRows.set([...rows]);
  }

  removeRow(row: GridRow) {
    if (row.id) {
      // Item existente — deletar no backend
      this.http.delete(`/budgets/${this.budgetId}/items/${row.id}`).subscribe({
        next: () => { this.messages.add({ severity: 'success', summary: 'Item excluído' }); this.loadWorksheet(); },
      });
    } else {
      // Linha nova não salva — remover do grid
      const rows = this.gridRows().filter(r => r !== row);
      this.gridRows.set(rows);
    }
  }

  // --- Save all dirty rows ---
  saveAll() {
    const dirty = this.gridRows().filter(r => !r.isStage && r.dirty && r.compositionId && r.quantity);
    if (dirty.length === 0) return;

    // Agrupar por stageId
    const byStage = new Map<string, GridRow[]>();
    for (const row of dirty) {
      const list = byStage.get(row.stageId) || [];
      list.push(row);
      byStage.set(row.stageId, list);
    }

    this.saving.set(true);
    let pending = byStage.size;

    for (const [stageId, rows] of byStage) {
      const items = rows.filter(r => !r.id).map(r => ({ compositionId: r.compositionId, quantity: r.quantity, unitCost: r.unitCost || undefined }));
      if (items.length === 0) { pending--; if (pending === 0) this.onSaveComplete(); continue; }

      this.http.post<any>(`/budgets/${this.budgetId}/stages/${stageId}/items/bulk`, items).subscribe({
        next: () => { pending--; if (pending === 0) this.onSaveComplete(); },
        error: () => { pending--; if (pending === 0) this.onSaveComplete(); },
      });
    }
  }

  private onSaveComplete() {
    this.saving.set(false);
    this.messages.add({ severity: 'success', summary: 'Salvo' });
    this.loadWorksheet();
  }

  // --- Stage CRUD ---
  createStage() {
    const sortOrder = this.stageOpts().length + 1;
    this.http.post(`/budgets/${this.budgetId}/stages`, { name: this.newStageName, sortOrder, parentId: this.newStageParentId }).subscribe({
      next: () => { this.showStageDialog = false; this.newStageName = ''; this.newStageParentId = null; this.messages.add({ severity: 'success', summary: 'Etapa criada' }); this.loadWorksheet(); },
    });
  }

  deleteStage(stageId: string) {
    this.http.delete(`/budgets/${this.budgetId}/stages/${stageId}`).subscribe({
      next: () => { this.messages.add({ severity: 'success', summary: 'Etapa excluída' }); this.loadWorksheet(); },
    });
  }

  // --- Workflow ---
  changeStatus(status: string) {
    this.http.put(`/budgets/${this.budgetId}`, { status }).subscribe({
      next: () => { this.budgetStatus.set(status); this.messages.add({ severity: 'success', summary: `Status: ${status}` }); },
    });
  }
  effectuate() {
    this.http.post<any>(`/budgets/${this.budgetId}/effectuate`, {}).subscribe({
      next: r => { this.budgetStatus.set(r.status || 'EFFECTIVE'); this.messages.add({ severity: 'success', summary: 'Efetivado' }); },
    });
  }
  revert() {
    this.http.post<any>(`/budgets/${this.budgetId}/revert`, {}).subscribe({
      next: r => { this.budgetStatus.set(r.status || 'APPROVED'); this.messages.add({ severity: 'success', summary: 'Revertido' }); },
    });
  }

  // --- Transform worksheet API response to flat grid ---
  private worksheetToGrid(stages: any[]): GridRow[] {
    const rows: GridRow[] = [];
    const walk = (list: any[]) => {
      for (const s of list) {
        const stageTotal = (s.items || []).reduce((sum: number, i: any) => sum + (i.totalCost || 0), 0);
        rows.push({ isStage: true, stageId: s.id, stageName: s.name, code: '', description: '', unit: '', quantity: null, unitCost: null, total: stageTotal, editing: false, dirty: false });
        for (const i of s.items || []) {
          rows.push({ isStage: false, id: i.id, stageId: s.id, stageName: s.name, code: i.code, description: i.description, unit: i.unit, quantity: i.quantity, unitCost: i.unitCost, total: i.totalCost, compositionId: i.compositionId, editing: false, dirty: false });
        }
        if (s.children?.length) walk(s.children);
      }
    };
    walk(stages);
    return rows;
  }

  private flatStages(stages: any[]): { label: string; value: string }[] {
    const result: { label: string; value: string }[] = [];
    const walk = (list: any[], prefix = '') => {
      for (const s of list) {
        result.push({ label: prefix + s.name, value: s.id });
        if (s.children?.length) walk(s.children, prefix + '  ');
      }
    };
    walk(stages);
    return result;
  }
}
