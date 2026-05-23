export type PurchaseOrderStatus = 'PENDING' | 'PARTIAL' | 'RECEIVED';

export interface PurchaseOrder {
  id: string;
  projectId: string;
  supplierName: string;
  number: string;
  description: string;
  quantity: number;
  unitPrice: number;
  totalAmount: number;
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
  id: string;
  description: string;
  currentQuantity: number;
  unit: string;
  minQuantity: number;
  location?: string;
  belowMinimum: boolean;
}
