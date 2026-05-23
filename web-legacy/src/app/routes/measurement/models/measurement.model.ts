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
  rejectionReason?: string;
  items: MeasurementItem[];
  createdAt: string;
}

export interface MeasurementDetail {
  id: string;
  number: number;
  periodStart: string;
  periodEnd: string;
  status: MeasurementStatus;
  retentionPct: number;
  grossAmount: number;
  netAmount: number;
  items: MeasurementDetailItem[];
}

export interface MeasurementDetailItem {
  id: string;
  budgetItemId?: string;
  description: string;
  periodQuantity: number;
  unitPrice: number;
  periodAmount: number;
  contractedQuantity: number;
  previousQuantity: number;
  cumulativeQuantity: number;
  balanceQuantity: number;
}

export interface MeasurementItem {
  id: string;
  costCodeId?: string;
  budgetItemId?: string;
  description: string;
  quantity: number;
  unitPrice: number;
  amount: number;
  extra?: boolean;
  contractorName?: string;
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

export interface MeasurementHistoryEntry {
  id: string;
  action: string;
  fromStatus?: string;
  toStatus: string;
  performedBy?: string;
  reason?: string;
  createdAt?: string;
}
