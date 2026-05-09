export type MeasurementStatus = 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'PAID';

export interface Measurement {
  id: string;
  budgetId: string;
  number: number;
  periodStart: string;
  periodEnd: string;
  status: MeasurementStatus;
  retentionPct: number;
  grossAmount: number;
  netAmount: number;
  createdAt: string;
}

export interface MeasurementItem {
  id: string;
  costCodeId?: string;
  description: string;
  quantity: number;
  unitPrice: number;
  amount: number;
}
