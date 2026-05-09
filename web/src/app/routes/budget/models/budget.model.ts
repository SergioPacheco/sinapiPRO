export type BudgetStatus = 'ESTIMATE' | 'SALE' | 'EXECUTION' | 'COMPLETED';

export interface Budget {
  id: string;
  code: string;
  title: string;
  customerName: string;
  totalAmount: number;
  status: BudgetStatus;
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
