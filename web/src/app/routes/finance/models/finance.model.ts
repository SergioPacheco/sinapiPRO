export interface Payable {
  id: string;
  description: string;
  amount: number;
  dueDate: string;
  paidDate: string | null;
  paidAmount: number | null;
  status: 'PENDING' | 'PAID' | 'OVERDUE' | 'CANCELLED';
  category: string;
  supplierId: string | null;
  purchaseOrderId: string | null;
  measurementId: string | null;
}

export interface Receivable {
  id: string;
  description: string;
  amount: number;
  dueDate: string;
  receivedDate: string | null;
  receivedAmount: number | null;
  status: 'PENDING' | 'PAID' | 'OVERDUE' | 'CANCELLED';
  category: string;
  measurementId: string | null;
  invoiceId: string | null;
}

export interface CashFlowSummary {
  payablesPending: number;
  payablesPaid: number;
  receivablesPending: number;
  receivablesReceived: number;
  currentBalance: number;
  projectedBalance: number;
}

export interface CashFlowMonth {
  month: string;
  inflows: number;
  outflows: number;
  netFlow: number;
  cumulativeBalance: number;
}

export interface BudgetVsActualLine {
  code: string;
  name: string;
  budgeted: number;
  committed: number;
  actual: number;
  variance: number;
  pctExecuted: number;
}
