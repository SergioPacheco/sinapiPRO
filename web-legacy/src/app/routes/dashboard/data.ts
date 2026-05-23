export const STATS = [
  {
    title: 'Orçamentos Ativos',
    amount: '12',
    progress: { value: 75, label: '3 em execução' },
    color: 'bg-blue-50',
  },
  {
    title: 'Valor Total Contratado',
    amount: 'R$ 4.2M',
    progress: { value: 62, label: '62% medido' },
    color: 'bg-green-50',
  },
  {
    title: 'Fornecedores Ativos',
    amount: '28',
    progress: { value: 85, label: '24 com pedidos' },
    color: 'bg-orange-50',
  },
  {
    title: 'Medições Pendentes',
    amount: '5',
    progress: { value: 40, label: '2 aguardando aprovação' },
    color: 'bg-red-50',
  },
];

export const CHARTS: any[] = [
  // Chart 1: Fluxo de Caixa
  {
    chart: { type: 'area', height: 280, toolbar: { show: false } },
    series: [
      { name: 'Receitas', data: [120, 180, 150, 220, 280, 310, 250, 290, 340, 380, 420, 450] },
      { name: 'Despesas', data: [90, 140, 130, 180, 200, 240, 210, 250, 280, 310, 350, 380] },
    ],
    xaxis: { categories: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'] },
    yaxis: { labels: { formatter: (v: number) => `R$ ${v}k` } },
    colors: ['#4caf50', '#f44336'],
    stroke: { curve: 'smooth', width: 2 },
    fill: { type: 'gradient', gradient: { opacityFrom: 0.4, opacityTo: 0.1 } },
  },
  // Chart 2: EVM Radar
  {
    chart: { type: 'radar', height: 280, toolbar: { show: false } },
    series: [
      { name: 'Planejado', data: [80, 90, 70, 85, 75] },
      { name: 'Realizado', data: [65, 80, 60, 90, 70] },
    ],
    xaxis: { categories: ['CPI', 'SPI', 'Qualidade', 'Prazo', 'Custo'] },
    colors: ['#1976d2', '#ff9800'],
    stroke: { width: 2 },
    fill: { opacity: 0.2 },
    markers: { size: 3 },
  },
];

export const ELEMENT_DATA: any[] = [];

export const MESSAGES: any[] = [];
