import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { CalendarModule } from 'primeng/calendar';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [FormsModule, TableModule, ButtonModule, DropdownModule, InputTextModule, CalendarModule],
  template: `
    <h3 style="margin:0 0 1rem">Relatórios</h3>

    <div class="flex gap-3 mb-3 flex-wrap">
      <p-dropdown [options]="categories" [(ngModel)]="selectedCategory" placeholder="Categoria" (onChange)="filter()" styleClass="w-12rem" />
      <input pInputText [(ngModel)]="searchText" placeholder="Buscar relatório..." (input)="filter()" class="w-16rem" />
    </div>

    <div class="grid">
      @for (report of filteredReports(); track report.id) {
        <div class="col-12 md:col-6 lg:col-4">
          <div class="report-card">
            <div class="report-icon"><i class="pi" [class]="report.icon"></i></div>
            <div class="report-info">
              <div class="report-name">{{ report.name }}</div>
              <div class="report-desc">{{ report.description }}</div>
              <div class="report-category">{{ report.category }}</div>
            </div>
            <p-button icon="pi pi-file-pdf" severity="secondary" [text]="true" title="Gerar PDF" (onClick)="generate(report)" [loading]="report.loading" />
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .report-card { display: flex; align-items: center; gap: 12px; padding: 1rem; border: 1px solid var(--sp-border); border-radius: var(--sp-radius); background: var(--sp-surface-card); }
    .report-icon { width: 40px; height: 40px; border-radius: 8px; background: var(--sp-primary-subtle); display: flex; align-items: center; justify-content: center; color: var(--sp-primary); }
    .report-info { flex: 1; }
    .report-name { font-weight: 600; font-size: 14px; }
    .report-desc { font-size: 12px; color: var(--sp-text-muted); margin-top: 2px; }
    .report-category { font-size: 11px; color: var(--sp-text-muted); margin-top: 4px; text-transform: uppercase; }
  `],
})
export class ReportsComponent {
  private http = inject(HttpClient);
  searchText = '';
  selectedCategory: string | null = null;
  filteredReports = signal<any[]>([]);

  categories = [
    { label: 'Todos', value: null },
    { label: 'Financeiro', value: 'finance' },
    { label: 'Suprimentos', value: 'procurement' },
    { label: 'Medição/Obra', value: 'measurement' },
    { label: 'Comercial', value: 'commercial' },
    { label: 'Orçamento', value: 'budget' },
    { label: 'MO/Estoque', value: 'labor' },
    { label: 'Gerencial', value: 'managerial' },
  ];

  reports = [
    { id: 'boleto', name: 'Boleto Bancário', description: 'Ficha de compensação', category: 'finance', icon: 'pi-money-bill', endpoint: '/reports/finance/boleto', loading: false },
    { id: 'recibo', name: 'Recibo de Pagamento', description: 'Comprovante para fornecedor', category: 'finance', icon: 'pi-receipt', endpoint: '/reports/finance/recibo', loading: false },
    { id: 'extrato', name: 'Extrato Conta Corrente', description: 'Movimentação por período', category: 'finance', icon: 'pi-list', endpoint: '/reports/finance/extrato-conta', loading: false },
    { id: 'aging-pagar', name: 'Aging Contas a Pagar', description: 'Inadimplência por faixa', category: 'finance', icon: 'pi-chart-bar', endpoint: '/reports/finance/aging-pagar', loading: false },
    { id: 'aging-receber', name: 'Aging Contas a Receber', description: 'Inadimplência por faixa', category: 'finance', icon: 'pi-chart-bar', endpoint: '/reports/finance/aging-receber', loading: false },
    { id: 'dre', name: 'DRE por Obra', description: 'Demonstrativo de resultado', category: 'finance', icon: 'pi-chart-line', endpoint: '/reports/finance/dre', loading: false },
    { id: 'fluxo-caixa', name: 'Fluxo de Caixa', description: 'Projeção mensal', category: 'finance', icon: 'pi-chart-line', endpoint: '/reports/finance/fluxo-caixa', loading: false },
    { id: 'mapa-custos', name: 'Mapa de Custos', description: 'Orçado × Realizado', category: 'finance', icon: 'pi-th-large', endpoint: '/reports/finance/mapa-custos', loading: false },
    { id: 'pedido', name: 'Pedido de Compra', description: 'Impressão do pedido', category: 'procurement', icon: 'pi-shopping-cart', endpoint: '/reports/procurement/pedido', loading: false },
    { id: 'mapa-cotacao', name: 'Mapa Comparativo', description: 'Cotações multi-fornecedor', category: 'procurement', icon: 'pi-table', endpoint: '/reports/procurement/mapa-comparativo', loading: false },
    { id: 'abc-insumos', name: 'Curva ABC Insumos', description: 'Consumo por obra', category: 'procurement', icon: 'pi-sort-amount-down', endpoint: '/reports/procurement/abc-insumos', loading: false },
    { id: 'boletim', name: 'Boletim de Medição', description: 'Medição acumulada', category: 'measurement', icon: 'pi-file', endpoint: '/reports/measurement/boletim-acumulado', loading: false },
    { id: 'rdo', name: 'RDO Completo', description: 'Diário de obra com fotos', category: 'measurement', icon: 'pi-calendar', endpoint: '/reports/measurement/rdo', loading: false },
    { id: 'cronograma', name: 'Cronograma Físico-Financeiro', description: 'Gantt + Curva S', category: 'measurement', icon: 'pi-chart-bar', endpoint: '/reports/measurement/cronograma', loading: false },
    { id: 'analitico', name: 'Orçamento Analítico', description: 'Composições abertas', category: 'budget', icon: 'pi-file', endpoint: '/reports/budget/analitico', loading: false },
    { id: 'sintetico', name: 'Orçamento Sintético', description: 'Resumo por etapa', category: 'budget', icon: 'pi-file', endpoint: '/reports/budget/sintetico', loading: false },
    { id: 'cpu', name: 'CPU', description: 'Composição de preços unitários', category: 'budget', icon: 'pi-file', endpoint: '/reports/budget/cpu', loading: false },
    { id: 'contrato', name: 'Contrato de Venda', description: 'Impressão do contrato', category: 'commercial', icon: 'pi-file', endpoint: '/reports/commercial/contrato', loading: false },
    { id: 'posicao-vendas', name: 'Posição de Vendas', description: 'Por empreendimento', category: 'commercial', icon: 'pi-chart-pie', endpoint: '/reports/commercial/posicao-vendas', loading: false },
    { id: 'dashboard-pdf', name: 'Dashboard Executivo', description: 'KPIs consolidados', category: 'managerial', icon: 'pi-chart-bar', endpoint: '/reports/managerial/dashboard', loading: false },
    { id: 'evm', name: 'EVM', description: 'Earned Value Management', category: 'managerial', icon: 'pi-chart-line', endpoint: '/reports/managerial/evm', loading: false },
  ];

  constructor() { this.filteredReports.set(this.reports); }

  filter() {
    let result = this.reports;
    if (this.selectedCategory) result = result.filter(r => r.category === this.selectedCategory);
    if (this.searchText) result = result.filter(r => r.name.toLowerCase().includes(this.searchText.toLowerCase()));
    this.filteredReports.set(result);
  }

  generate(report: any) {
    report.loading = true;
    this.http.get(report.endpoint, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        window.open(url, '_blank');
        report.loading = false;
      },
      error: () => report.loading = false,
    });
  }
}
