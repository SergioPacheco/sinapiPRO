import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { PageHeader } from '@shared';

@Component({
  selector: 'app-analytics-list',
  templateUrl: './analytics-list.html',
  styleUrl: './analytics-list.scss',
  imports: [MatIconModule, PageHeader, RouterLink],
})
export class AnalyticsListComponent {
  reportGroups = [
    {
      title: 'Obras', icon: 'apartment',
      items: [
        { label: 'Obras por Status', icon: 'pie_chart', description: 'Distribuição do portfólio', route: '/projects' },
        { label: 'Obras Atrasadas', icon: 'schedule', description: 'SPI < 1', route: '/projects', params: { status: 'delayed' } },
        { label: 'Evolução Física', icon: 'trending_up', description: 'Curva S consolidada', route: '/projects' },
      ],
    },
    {
      title: 'Financeiro', icon: 'account_balance',
      items: [
        { label: 'Fluxo de Caixa', icon: 'waterfall_chart', description: 'Projeção 12 meses', route: '/finance' },
        { label: 'Previsto × Realizado', icon: 'compare_arrows', description: 'Desvio orçamentário', route: '/finance' },
        { label: 'Custos por Categoria', icon: 'donut_large', description: 'Material, MO, equipamento', route: '/finance' },
        { label: 'DRE por Obra', icon: 'receipt_long', description: 'Resultado gerencial', route: '/finance' },
      ],
    },
    {
      title: 'Suprimentos', icon: 'local_shipping',
      items: [
        { label: 'Compras Pendentes', icon: 'pending_actions', description: 'Pedidos aguardando', route: '/procurement' },
        { label: 'Curva ABC de Insumos', icon: 'bar_chart', description: 'Impacto por material', route: '/sinapi' },
      ],
    },
    {
      title: 'Desempenho', icon: 'speed',
      items: [
        { label: 'EVM (Earned Value)', icon: 'analytics', description: 'CPI, SPI, EAC', route: '/projects' },
        { label: 'Produtividade', icon: 'groups', description: 'Unidades/hora por equipe', route: '/projects' },
        { label: 'Contratos Vencendo', icon: 'event_busy', description: 'Próximos 30 dias', route: '/projects' },
      ],
    },
  ];
}
