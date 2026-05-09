export type PurchaseOrderStatus = 'PENDING' | 'PARTIAL' | 'RECEIVED';

export interface PurchaseOrder {
  id: string;
  budgetId: string;
  supplierName: string;
  number: string;
  description: string;
  quantity: number;
  unitPrice: number;
  status: PurchaseOrderStatus;
}
