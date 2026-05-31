import { Component, inject, OnInit, HostListener, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DecimalPipe, NgClass } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { MenuModule } from 'primeng/menu';
import { ContextMenuModule } from 'primeng/contextmenu';
import { MessageService } from 'primeng/api';
import { BudgetTreeService, BudgetRow } from './budget-tree.service';
import { BdiDialogComponent } from './bdi-dialog.component';
import { StatusTagComponent } from '../../shared/components';

@Component({
  selector: 'app-budget-worksheet',
  standalone: true,
  imports: [DecimalPipe, NgClass, FormsModule, AutoCompleteModule, ButtonModule, TooltipModule, MenuModule, ContextMenuModule, StatusTagComponent, BdiDialogComponent],
  templateUrl: './budget-worksheet.component.html',
  styleUrl: './budget-worksheet.component.scss',
})
export class BudgetWorksheetComponent implements OnInit {
  tree = inject(BudgetTreeService);
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private messages = inject(MessageService);

  selectedRow: BudgetRow | null = null;
  selectedRows: Set<BudgetRow> = new Set();
  suggestions: any[] = [];
  showBdi = false;
  showComposition = false;
  showMultiply = false;
  showInfo = false;
  showSettings = false;
  showFind = false;
  multiplyFactor = 1;
  budgetInfo: any = {};
  compositionItems: any[] = [];
  compositionName = '';
  compositionId: string | null = null;

  // Localizar
  findText = '';
  findField = 'description'; // description | refCode | unit
  findResults: BudgetRow[] = [];
  findIndex = 0;

  // Copiar/Colar
  clipboard: BudgetRow[] = [];

  // Configurações
  settings = { rounding: 'TRUNCATE', decQty: 4, decVal: 2, autoItemize: true };

  // Gráfico / Integridade
  showChart = false;
  chartData: { name: string; total: number; pct: number }[] = [];
  integrityResults: string[] = [];

  // #17 Base de Preço / Insumo
  basePreco = 'SINAPI';
  baseState = 'SP';
  baseMonth = '2024-12';

  // #34 Proteção
  isReadOnly = false;

  // Undo/Redo
  private undoStack: string[] = [];
  private redoStack: string[] = [];

  get budgetId() { return this.route.snapshot.paramMap.get('budgetId'); }

  menuItems = [
    { label: 'Multiplicar Quantidades', icon: 'pi pi-times', command: () => this.showMultiply = true },
    { label: 'Aplicar Preço a Iguais', icon: 'pi pi-equals', command: () => this.applyPriceToEquals() },
    { label: 'Alterar Todas Iguais', icon: 'pi pi-sync', command: () => this.applyToAllEqual() },
    { label: 'Composições Iguais', icon: 'pi pi-clone', command: () => this.showEqualCompositions() },
    { label: 'Itens com Preço Zerado', icon: 'pi pi-exclamation-circle', command: () => this.showZeroItems() },
    { separator: true },
    { label: 'Especificação Técnica', icon: 'pi pi-align-left', command: () => this.showSpecification() },
    { label: 'Corrigir Estrutura', icon: 'pi pi-sort-alt', command: () => this.fixStructure() },
    { label: 'Gráfico por Etapa', icon: 'pi pi-chart-pie', command: () => this.openChart() },
    { label: 'Verificar Integridade', icon: 'pi pi-check-square', command: () => this.verifyIntegrity() },
    { label: 'Backup Orçamento', icon: 'pi pi-download', command: () => this.backup() },
    { separator: true },
    { label: 'Configurações', icon: 'pi pi-cog', command: () => this.loadSettings() },
    { label: 'Informações', icon: 'pi pi-info-circle', command: () => this.loadInfo() },
    { separator: true },
    { label: 'Sintético PDF', icon: 'pi pi-file-pdf', command: () => window.open(`/api/v1/budgets/${this.budgetId}/reports/worksheet.pdf`, '_blank') },
    { label: 'Analítico PDF', icon: 'pi pi-file-pdf', command: () => window.open(`/api/v1/budgets/${this.budgetId}/reports/analytical.pdf`, '_blank') },
    { label: 'Curva ABC', icon: 'pi pi-chart-bar', command: () => window.open(`/api/v1/budgets/${this.budgetId}/reports/abc-services.pdf`, '_blank') },
    { label: 'Excel', icon: 'pi pi-file-excel', command: () => window.open(`/api/v1/budgets/${this.budgetId}/export/excel`, '_blank') },
    { separator: true },
    { label: 'Efetivar', icon: 'pi pi-lock', command: () => this.effectuate() },
  ];

  // Context menu (botão direito)
  contextMenuItems = [
    { label: 'Novo Item (Insert)', icon: 'pi pi-plus', command: () => this.insertItem() },
    { label: 'Excluir (Delete)', icon: 'pi pi-trash', command: () => this.deleteWithConfirm() },
    { separator: true },
    { label: 'Copiar (Ctrl+C)', icon: 'pi pi-clone', command: () => this.copyItems() },
    { label: 'Colar (Ctrl+V)', icon: 'pi pi-clipboard', command: () => this.pasteItems() },
    { separator: true },
    { label: 'Acessar Composição', icon: 'pi pi-list', command: () => this.openComposition() },
    { label: 'Salvar como Própria', icon: 'pi pi-copy', command: () => this.saveAsOwn() },
    { label: 'Alterar Todas Iguais', icon: 'pi pi-sync', command: () => this.applyToAllEqual() },
    { label: 'Composições Iguais', icon: 'pi pi-clone', command: () => this.showEqualCompositions() },
    { separator: true },
    { label: 'Expandir', icon: 'pi pi-angle-down', command: () => { if (this.selectedRow) this.tree.toggle(this.selectedRow); } },
    { label: 'Especificação Técnica', icon: 'pi pi-align-left', command: () => this.showSpecification() },
    { label: 'Aplicar Preço a Iguais', icon: 'pi pi-equals', command: () => this.applyPriceToEquals() },
  ];

  ngOnInit() {
    this.tree.load(this.budgetId!);
    // #34 Verificar se orçamento está bloqueado
    this.http.get<any>(`/budgets/${this.budgetId}`).subscribe({
      next: b => {
        this.isReadOnly = this.isLockedStatus(b.status);
        this.baseState = b.state || 'SP';
        this.baseMonth = this.toMonthInput(b.referenceDate || '2024-12-01');
      },
    });
  }

  // === Keyboard Navigation ===
  @HostListener('document:keydown', ['$event'])
  onKeydown(e: KeyboardEvent) {
    if (this.isReadOnly && (
      e.key === 'Insert' ||
      e.key === 'Delete' ||
      (e.ctrlKey && ['s', 'v', 'z', 'y'].includes(e.key.toLowerCase()))
    )) {
      e.preventDefault();
      return;
    }
    if (e.key === 'Insert') { e.preventDefault(); this.insertItem(); return; }
    if (e.ctrlKey && e.key === 'z') { e.preventDefault(); this.undo(); return; }
    if (e.ctrlKey && e.key === 'y') { e.preventDefault(); this.redo(); return; }
    if (e.ctrlKey && e.key === 's') { e.preventDefault(); this.tree.saveAll(); return; }
    if (e.ctrlKey && e.key === 'f') { e.preventDefault(); this.openFind(); return; }
    if (e.ctrlKey && e.key === 'c') { if (!this.isEditing()) { e.preventDefault(); this.copyItems(); return; } }
    if (e.ctrlKey && e.key === 'v') { if (!this.isEditing()) { e.preventDefault(); this.pasteItems(); return; } }
    if (e.key === 'F3') { e.preventDefault(); this.findNext(); return; }
    if (e.key === 'Delete' && this.selectedRow && !this.isEditing()) { e.preventDefault(); this.deleteWithConfirm(); return; }

    const rows = this.tree.visibleRows();
    const idx = this.selectedRow ? rows.indexOf(this.selectedRow) : -1;

    if (e.key === 'ArrowDown' && idx < rows.length - 1) {
      e.preventDefault();
      this.selectRow(rows[idx + 1], e.shiftKey);
    } else if (e.key === 'ArrowUp' && idx > 0) {
      e.preventDefault();
      this.selectRow(rows[idx - 1], e.shiftKey);
    } else if (e.key === 'Enter' && this.selectedRow?.type === 'EMPTY') {
      // Enter no autocomplete é tratado pelo PrimeNG
    }
  }

  selectRow(row: BudgetRow, multi = false) {
    if (multi) {
      if (this.selectedRows.has(row)) this.selectedRows.delete(row);
      else this.selectedRows.add(row);
    } else {
      this.selectedRows.clear();
    }
    this.selectedRow = row;
    this.selectedRows.add(row);
  }

  isMultiSelected(row: BudgetRow): boolean { return this.selectedRows.size > 1 && this.selectedRows.has(row); }

  private isEditing(): boolean {
    return document.activeElement?.tagName === 'INPUT' || document.activeElement?.tagName === 'TEXTAREA';
  }

  // === Insert ===
  insertItem() {
    if (this.isReadOnly) return;
    this.pushUndo();
    const idx = this.selectedRow ? this.tree.rows().indexOf(this.selectedRow) : this.tree.rows().length - 1;
    this.selectedRow = this.tree.insertEmpty(idx);
    this.selectedRows.clear();
    this.selectedRows.add(this.selectedRow);
  }

  deleteSelected() {
    if (this.isReadOnly) return;
    this.pushUndo();
    if (this.selectedRows.size > 1) {
      for (const row of this.selectedRows) this.tree.deleteRow(row);
      this.selectedRows.clear();
    } else if (this.selectedRow) {
      this.tree.deleteRow(this.selectedRow);
    }
    this.selectedRow = null;
  }

  // === Undo/Redo ===
  private pushUndo() {
    this.undoStack.push(JSON.stringify(this.tree.rows().map(r => ({ ...r, _children: undefined, children: undefined }))));
    this.redoStack = [];
    if (this.undoStack.length > 50) this.undoStack.shift();
  }

  undo() {
    if (!this.undoStack.length) return;
    this.redoStack.push(JSON.stringify(this.tree.rows().map(r => ({ ...r, _children: undefined, children: undefined }))));
    const state = JSON.parse(this.undoStack.pop()!);
    this.tree.rows.set(state);
    this.messages.add({ severity: 'info', summary: 'Desfazer', life: 1500 });
  }

  redo() {
    if (!this.redoStack.length) return;
    this.undoStack.push(JSON.stringify(this.tree.rows().map(r => ({ ...r, _children: undefined, children: undefined }))));
    const state = JSON.parse(this.redoStack.pop()!);
    this.tree.rows.set(state);
    this.messages.add({ severity: 'info', summary: 'Refazer', life: 1500 });
  }

  // === Drag and Drop (reordenar etapas) ===
  onDragStart(event: DragEvent, row: BudgetRow) {
    if (row.type !== 'LEVEL') return;
    event.dataTransfer?.setData('text/plain', String(this.tree.rows().indexOf(row)));
  }

  onDragOver(event: DragEvent, row: BudgetRow) {
    if (row.type === 'LEVEL') event.preventDefault();
  }

  onDrop(event: DragEvent, targetRow: BudgetRow) {
    event.preventDefault();
    const fromIdx = Number(event.dataTransfer?.getData('text/plain'));
    const toIdx = this.tree.rows().indexOf(targetRow);
    if (isNaN(fromIdx) || fromIdx === toIdx) return;
    this.pushUndo();
    const all = this.tree.rows();
    const [moved] = all.splice(fromIdx, 1);
    all.splice(toIdx, 0, moved);
    this.tree.rows.set([...all]);
    this.messages.add({ severity: 'success', summary: 'Etapa reordenada', life: 1500 });
  }

  // === Search ===
  searchUnified(event: any) {
    const q = event.query || '';
    const n = this.tree.rows().filter(r => r.type === 'LEVEL').length + 1;
    const levelOption = { _type: 'LEVEL', label: `${String(n).padStart(2, '0')}. - Nova Etapa`, id: null, description: '' };
    this.http.get<any>(`/compositions?q=${encodeURIComponent(q)}&size=10`).subscribe({
      next: res => {
        const results: any[] = [levelOption];
        for (const c of (res.content || res)) {
          results.push({ ...c, _type: 'COMPOSITION', label: `${c.sinapiCode} - ${c.description} (${c.unit})` });
        }
        this.suggestions = results;
      },
    });
  }

  onSelect(row: BudgetRow, event: any) {
    const sel = event?.value ?? event;
    this.pushUndo();
    this.tree.resolveRow(row, sel);
  }
  onStageBlur(row: BudgetRow) { if (row.description && !row.stageId) this.tree.resolveRow(row, { _type: 'LEVEL', description: row.description }); }

  openComposition() {
    if (!this.selectedRow?.compositionId) { this.messages.add({ severity: 'warn', summary: 'Selecione uma composição' }); return; }
    this.compositionId = this.selectedRow.compositionId;
    this.http.get<any>(`/compositions/${this.compositionId}`).subscribe({
      next: comp => { this.compositionName = comp.description; this.compositionItems = (comp.items || []).map((i: any) => ({ ...i })); this.showComposition = true; },
    });
  }

  saveCompositionChanges() {
    if (this.isReadOnly) return;
    const items = this.compositionItems.map(i => ({ childCompositionId: i.itemType === 'COMPOSITION' ? i.id : null, materialId: i.itemType !== 'COMPOSITION' ? i.id : null, coefficient: i.coefficient, itemType: i.itemType }));
    this.http.put(`/compositions/${this.compositionId}`, { description: this.compositionName, items }).subscribe({
      next: () => { this.showComposition = false; this.messages.add({ severity: 'success', summary: 'Composição atualizada' }); this.tree.load(this.budgetId!); },
    });
  }

  saveAsOwn() {
    if (!this.selectedRow?.compositionId) { this.messages.add({ severity: 'warn', summary: 'Selecione uma composição' }); return; }
    this.http.get<any>(`/compositions/${this.selectedRow.compositionId}`).subscribe({
      next: comp => {
        const body = { sinapiCode: 'P-' + comp.sinapiCode, description: comp.description, unit: comp.unit, origin: 'PROPRIO', items: (comp.items || []).map((i: any) => ({ materialId: i.itemType !== 'COMPOSITION' ? i.id : null, childCompositionId: i.itemType === 'COMPOSITION' ? i.id : null, coefficient: i.coefficient })) };
        this.http.post('/compositions', body).subscribe({ next: () => this.messages.add({ severity: 'success', summary: 'Salva como própria' }) });
      },
    });
  }

  expandAll() { for (const r of this.tree.rows()) if ((r.type === 'LEVEL' || r.type === 'COMPOSITION') && !r.expanded) this.tree.toggle(r); }
  collapseAll() { for (const r of [...this.tree.rows()].reverse()) if ((r.type === 'LEVEL' || r.type === 'COMPOSITION') && r.expanded) this.tree.toggle(r); }

  applyMultiply() {
    if (this.isReadOnly) return;
    this.pushUndo();
    for (const r of this.tree.rows()) if ((r.type === 'COMPOSITION' || r.type === 'INPUT') && r.quantity) { r.quantity *= this.multiplyFactor; r.total = (r.quantity || 0) * (r.unitCost || 0); r.dirty = true; }
    this.tree.rows.set([...this.tree.rows()]); this.showMultiply = false;
    this.messages.add({ severity: 'success', summary: `Multiplicado por ${this.multiplyFactor}` });
  }

  applyPriceToEquals() {
    if (this.isReadOnly) return;
    if (!this.selectedRow?.compositionId || !this.selectedRow.unitCost) return;
    this.pushUndo();
    const id = this.selectedRow.compositionId, price = this.selectedRow.unitCost;
    let n = 0;
    for (const r of this.tree.rows()) if (r.compositionId === id && r !== this.selectedRow) { r.unitCost = price; r.total = (r.quantity || 0) * price; r.dirty = true; n++; }
    this.tree.rows.set([...this.tree.rows()]);
    this.messages.add({ severity: 'success', summary: `Preço aplicado a ${n} iguais` });
  }

  loadInfo() { this.http.get<any>(`/budgets/${this.budgetId}`).subscribe({ next: b => { this.budgetInfo = b; this.showInfo = true; } }); }

  // === LOCALIZAR (Ctrl+F) ===
  openFind() { this.showFind = true; this.findResults = []; this.findIndex = 0; }

  find() {
    if (!this.findText) return;
    const q = this.findText.toLowerCase();
    this.findResults = this.tree.rows().filter(r => {
      if (this.findField === 'description') return r.description?.toLowerCase().includes(q);
      if (this.findField === 'refCode') return r.refCode?.toLowerCase().includes(q);
      if (this.findField === 'unit') return r.unit?.toLowerCase().includes(q);
      return false;
    });
    this.findIndex = 0;
    if (this.findResults.length > 0) this.goToFound();
    else this.messages.add({ severity: 'warn', summary: 'Nenhum item encontrado' });
  }

  findNext() { if (this.findResults.length > 0) { this.findIndex = (this.findIndex + 1) % this.findResults.length; this.goToFound(); } }

  private goToFound() {
    const row = this.findResults[this.findIndex];
    // Auto-expand: se o item está hidden, expandir pais
    if (row.hidden) {
      for (const r of this.tree.rows()) {
        if ((r.type === 'LEVEL' || r.type === 'COMPOSITION') && !r.expanded) this.tree.toggle(r);
      }
    }
    this.selectedRow = row;
    this.selectedRows.clear();
    this.selectedRows.add(row);
    // Scroll to element
    setTimeout(() => document.querySelector('tr.r-selected')?.scrollIntoView({ block: 'center', behavior: 'smooth' }), 50);
  }

  // === COPIAR/COLAR ===
  copyItems() {
    this.clipboard = [...this.selectedRows].filter(r => r.type !== 'EMPTY');
    if (this.clipboard.length === 0 && this.selectedRow) this.clipboard = [this.selectedRow];
    this.messages.add({ severity: 'info', summary: `${this.clipboard.length} item(ns) copiado(s)`, life: 2000 });
  }

  pasteItems() {
    if (this.isReadOnly) return;
    if (!this.clipboard.length) { this.messages.add({ severity: 'warn', summary: 'Nada para colar' }); return; }
    this.pushUndo();
    const all = this.tree.rows();
    const idx = this.selectedRow ? all.indexOf(this.selectedRow) + 1 : all.length;
    const stageId = this.selectedRow?.stageId || this.clipboard[0].stageId;
    const copies = this.clipboard.map(r => ({ ...r, id: undefined, dirty: true, stageId, _children: undefined, children: undefined }));
    all.splice(idx, 0, ...copies);
    this.tree.rows.set([...all]);
    this.messages.add({ severity: 'success', summary: `${copies.length} item(ns) colado(s)` });
  }

  // === CONFIRMAÇÃO DE EXCLUSÃO ===
  deleteWithConfirm() {
    if (this.isReadOnly) return;
    if (!this.selectedRow) return;
    if (confirm('Tem certeza que deseja excluir o item selecionado?')) {
      this.deleteSelected();
    }
  }

  // === CONFIGURAÇÕES ===
  loadSettings() {
    this.http.get<any>(`/budgets/${this.budgetId}`).subscribe({
      next: b => {
        this.settings = { rounding: b.roundingMethod || 'TRUNCATE', decQty: b.decimalPlaces || 4, decVal: 2, autoItemize: true };
        this.showSettings = true;
      },
    });
  }

  saveSettings() {
    this.http.put(`/budgets/${this.budgetId}`, { roundingMethod: this.settings.rounding, decimalPlaces: this.settings.decQty }).subscribe({
      next: () => { this.showSettings = false; this.messages.add({ severity: 'success', summary: 'Configurações salvas' }); },
    });
  }

  // === ALTERAR TODAS COMPOSIÇÕES IGUAIS ===
  applyToAllEqual() {
    if (this.isReadOnly) return;
    if (!this.selectedRow?.compositionId) { this.messages.add({ severity: 'warn', summary: 'Selecione uma composição' }); return; }
    this.pushUndo();
    const src = this.selectedRow;
    let count = 0;
    for (const r of this.tree.rows()) {
      if (r.compositionId === src.compositionId && r !== src && (r.type === 'COMPOSITION' || r.type === 'INPUT')) {
        r.quantity = src.quantity;
        r.unitCost = src.unitCost;
        r.total = (r.quantity || 0) * (r.unitCost || 0);
        r.dirty = true;
        count++;
      }
    }
    this.tree.rows.set([...this.tree.rows()]);
    this.messages.add({ severity: 'success', summary: `Alteração replicada para ${count} item(ns) iguais` });
  }

  // === APRESENTAR ITENS ZERADOS ===
  showZeroItems() {
    const zeros = this.tree.rows().filter(r => (r.type === 'COMPOSITION' || r.type === 'INPUT') && (!r.unitCost || r.unitCost === 0));
    if (zeros.length === 0) { this.messages.add({ severity: 'info', summary: 'Nenhum item com preço zerado' }); return; }
    // Highlight itens zerados
    this.selectedRows.clear();
    for (const r of zeros) this.selectedRows.add(r);
    this.selectedRow = zeros[0];
    setTimeout(() => document.querySelector('tr.r-selected')?.scrollIntoView({ block: 'center', behavior: 'smooth' }), 50);
    this.messages.add({ severity: 'warn', summary: `${zeros.length} item(ns) com preço zerado` });
  }

  // === RESUMO POR TIPO ===
  getSummaryByType(): { type: string; count: number; total: number }[] {
    const map = new Map<string, { count: number; total: number }>();
    for (const r of this.tree.rows()) {
      if (r.type === 'COMPOSITION' || r.type === 'INPUT') {
        const key = r.type === 'COMPOSITION' ? 'Composições' : 'Insumos';
        const entry = map.get(key) || { count: 0, total: 0 };
        entry.count++;
        entry.total += r.total || 0;
        map.set(key, entry);
      }
    }
    return [...map.entries()].map(([type, v]) => ({ type, ...v }));
  }

  // === GRÁFICO POR ETAPA ===
  openChart() {
    const grandTotal = this.tree.summary().directCost || 1;
    this.chartData = this.tree.rows()
      .filter(r => r.type === 'LEVEL')
      .map(r => ({ name: r.description || r.code, total: r.total || 0, pct: ((r.total || 0) / grandTotal) * 100 }));
    this.showChart = true;
  }

  // === VERIFICAR INTEGRIDADE ===
  verifyIntegrity() {
    const issues: string[] = [];
    const rows = this.tree.rows();
    // Itens sem etapa
    const orphans = rows.filter(r => (r.type === 'COMPOSITION' || r.type === 'INPUT') && !r.stageId);
    if (orphans.length) issues.push(`${orphans.length} item(ns) sem etapa vinculada`);
    // Itens com preço zerado
    const zeros = rows.filter(r => (r.type === 'COMPOSITION' || r.type === 'INPUT') && (!r.unitCost || r.unitCost === 0));
    if (zeros.length) issues.push(`${zeros.length} item(ns) com preço zerado`);
    // Itens com quantidade zerada
    const noQty = rows.filter(r => (r.type === 'COMPOSITION' || r.type === 'INPUT') && (!r.quantity || r.quantity === 0));
    if (noQty.length) issues.push(`${noQty.length} item(ns) com quantidade zerada`);
    // Etapas vazias
    const emptyStages = rows.filter(r => r.type === 'LEVEL' && r.total === 0);
    if (emptyStages.length) issues.push(`${emptyStages.length} etapa(s) sem itens`);
    // Itens duplicados (mesmo compositionId na mesma etapa)
    const seen = new Map<string, number>();
    for (const r of rows) {
      if (r.compositionId && r.stageId) {
        const key = `${r.stageId}-${r.compositionId}`;
        seen.set(key, (seen.get(key) || 0) + 1);
      }
    }
    const dups = [...seen.values()].filter(v => v > 1).length;
    if (dups) issues.push(`${dups} composição(ões) duplicada(s) na mesma etapa`);

    if (issues.length === 0) issues.push('✅ Nenhum problema encontrado');
    this.integrityResults = issues;
    this.messages.add({ severity: issues[0].startsWith('✅') ? 'success' : 'warn', summary: 'Verificação concluída', detail: issues.join('; ') });
  }

  // === BACKUP ===
  backup() {
    const data = { budget: this.budgetInfo, rows: this.tree.rows().map(r => ({ ...r, _children: undefined, children: undefined })) };
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = `orcamento-${this.budgetId}-backup.json`; a.click();
    URL.revokeObjectURL(url);
    this.messages.add({ severity: 'success', summary: 'Backup exportado' });
  }

  rowClass(row: BudgetRow): Record<string, boolean> {
    return { 'r-level': row.type === 'LEVEL', 'r-sublevel': row.type === 'SUB_LEVEL', 'r-comp': row.type === 'COMPOSITION', 'r-input': row.type === 'INPUT', 'r-sub': row.type === 'SUB_COMPOSITION', 'r-empty': row.type === 'EMPTY', 'r-dirty': row.dirty, 'r-selected': row === this.selectedRow, 'r-multi': this.isMultiSelected(row) };
  }

  // === ITENS FALTANTES DO STRATO ===

  /** #3 Tab no campo Cód.Ref busca insumo automaticamente */
  onRefCodeTab(row: BudgetRow, event: KeyboardEvent) {
    if (event.key === 'Tab' && row.refCode && !row.compositionId) {
      event.preventDefault();
      // Buscar composição/insumo pelo código
      this.http.get<any>(`/compositions?q=${encodeURIComponent(row.refCode)}&size=1`).subscribe({
        next: res => {
          const items = res.content || res;
          if (items.length > 0) {
            this.tree.resolveRow(row, { ...items[0], _type: 'COMPOSITION' });
            this.messages.add({ severity: 'success', summary: `Encontrado: ${items[0].description}` });
          } else {
            this.messages.add({ severity: 'warn', summary: 'Código não encontrado' });
          }
        },
      });
    }
  }

  /** #26 Apresentar composições iguais — destaca todas ocorrências da mesma composição */
  showEqualCompositions() {
    if (!this.selectedRow?.compositionId) { this.messages.add({ severity: 'warn', summary: 'Selecione uma composição' }); return; }
    const id = this.selectedRow.compositionId;
    const equals = this.tree.rows().filter(r => r.compositionId === id);
    this.selectedRows.clear();
    for (const r of equals) this.selectedRows.add(r);
    this.messages.add({ severity: 'info', summary: `${equals.length} ocorrência(s) encontrada(s)` });
  }

  /** #28 Especificação Técnica — observações do item */
  showSpecification() {
    if (!this.selectedRow) return;
    const spec = prompt('Especificação Técnica / Observações:', this.selectedRow.description);
    if (spec !== null) { this.messages.add({ severity: 'info', summary: 'Especificação registrada' }); }
  }

  /** #33 Corrigir estrutura — renumera e reordena */
  fixStructure() {
    this.pushUndo();
    let levelCount = 0;
    let itemCount = 0;
    for (const r of this.tree.rows()) {
      if (r.type === 'LEVEL') { levelCount++; itemCount = 0; r.code = String(levelCount).padStart(2, '0') + '.'; }
      else if (r.type === 'COMPOSITION' || r.type === 'INPUT') { itemCount++; r.code = String(levelCount).padStart(2, '0') + '.' + String(itemCount).padStart(3, '0'); }
    }
    this.tree.rows.set([...this.tree.rows()]);
    this.messages.add({ severity: 'success', summary: 'Estrutura corrigida e renumerada' });
  }

  /** #17 Atualizar Base de Preço — recalcula todos os preços pela base selecionada */
  updateBasePrice() {
    if (this.isReadOnly) return;
    this.http.post<any>(`/budgets/${this.budgetId}/update-base-date`, { referenceDate: this.normalizeBaseMonth(), state: this.baseState }).subscribe({
      next: res => {
        this.messages.add({ severity: 'success', summary: `Preços atualizados`, detail: `${res.updatedPrices} atualizados, ${res.divergentPrices} divergentes` });
        this.tree.load(this.budgetId!);
      },
    });
  }

  effectuate() {
    if (this.tree.dirtyCount() > 0) {
      this.messages.add({ severity: 'warn', summary: 'Salve as alterações antes de efetivar' });
      return;
    }
    if (!confirm('Efetivar este orçamento e bloquear edição?')) return;
    this.http.post<any>(`/budgets/${this.budgetId}/effectuate`, {}).subscribe({
      next: r => {
        this.tree.budgetStatus.set(r.status);
        this.isReadOnly = this.isLockedStatus(r.status);
        this.messages.add({ severity: 'success', summary: 'Orçamento efetivado' });
      },
      error: err => this.messages.add({ severity: 'warn', summary: 'Não foi possível efetivar', detail: err?.error?.message || err?.message }),
    });
  }

  private normalizeBaseMonth(): string {
    return this.baseMonth.length === 7 ? `${this.baseMonth}-01` : this.baseMonth;
  }

  private toMonthInput(date: string): string {
    return date?.length >= 7 ? date.slice(0, 7) : '2024-12';
  }

  private isLockedStatus(status: string): boolean {
    return ['IN_EXECUTION', 'COMPLETED', 'CANCELLED', 'SUPERSEDED'].includes(status);
  }
}
