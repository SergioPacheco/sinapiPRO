export type PurchaseOrderStatus = 'PENDING' | 'PARTIAL' | 'RECEIVED';

export interface PurchaseOrder {
  id: string;
  projectId: string;
  supplierName: string;
  number: string;
  description: string;
  quantity: number;
  unitPrice: number;
  status: PurchaseOrderStatus;
}

export interface Quotation {
  id: string;
  purchaseRequest: string;
  deadline: string;
  status: 'OPEN' | 'CLOSED';
  responsesCount: number;
}

export interface InventoryItem {
  materialCode: string;
  description: string;
  quantity: number;
  unit: string;
  averageCost: number;
  totalValue: number;
}

