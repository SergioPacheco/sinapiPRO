export type MeasurementStatus = 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'PAID';

export interface Measurement {
  id: string;
  projectId: string;
  number: number;
  periodStart: string;
  periodEnd: string;
  status: MeasurementStatus;
  retentionPct: number;
  grossAmount: number;
  netAmount: number;
  items: MeasurementItem[];
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

export interface AvailableMeasurementItem {
  budgetItemId: string;
  code: string;
  description: string;
  unit: string;
  contractedQuantity: number;
  previousQuantity: number;
  balanceQuantity: number;
  unitPrice: number;
}
