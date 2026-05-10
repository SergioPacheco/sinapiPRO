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
