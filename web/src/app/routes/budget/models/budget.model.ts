export type BudgetStatus = 'DRAFT' | 'IN_REVIEW' | 'APPROVED' | 'REJECTED' | 'SUPERSEDED' | 'IN_EXECUTION' | 'COMPLETED' | 'CANCELLED';

export interface Budget {
  id: string;
  code: string;
  title: string;
  customerName: string;
  totalAmount: number;
  status: BudgetStatus;
  active: boolean;
  startDate: string;
  endDate?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateBudgetRequest {
  code: string;
  title: string;
  customerName: string;
  totalAmount: number;
  status: BudgetStatus;
  startDate: string;
  endDate?: string;
}

export interface BudgetWorksheetItem {
  id: string;
  code: string;
  description: string;
  unit: string;
  quantity: number;
  unitCost: number;
  totalCost: number;
  origin: string;
}

export interface BudgetWorksheetStage {
  id: string;
  name: string;
  sortOrder: number;
  items: BudgetWorksheetItem[];
  children: BudgetWorksheetStage[];
  subtotal: number;
}

export interface BudgetWorksheet {
  stages: BudgetWorksheetStage[];
  directCost: number;
  bdiPct: number;
  bdiAmount: number;
  total: number;
}

export interface BudgetMemoLine {
  description: string;
  formula: string;
  value: number;
}

export interface BudgetItemMemo {
  budgetItemId: string;
  lines: BudgetMemoLine[];
  result: number;
  notes?: string;
}

export interface BudgetServiceAbcEntry {
  itemId: string;
  serviceCode: string;
  description: string;
  unit: string;
  quantity: number;
  unitCost: number;
  cost: number;
  percentage: number;
  cumulativePercentage: number;
  classification: string;
}

export interface BudgetBdiConfig {
  itemType: string;
  administration: number;
  profit: number;
  taxes: number;
  socialCharges: number;
  financialExpenses: number;
  risks: number;
  totalBdi: number;
}

export interface BudgetBdiRequest {
  itemType: string;
  administration: number;
  profit: number;
  taxes: number;
  socialCharges: number;
  financialExpenses: number;
  risks: number;
}

export interface BudgetBaseDateUpdateRequest {
  referenceDate: string;
  state: string;
}
