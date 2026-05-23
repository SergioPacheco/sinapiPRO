export interface Composition {
  id: string;
  sinapiCode: string;
  description: string;
  unit: string;
  groupName?: string;
  createdAt: string;
}

export interface CompositionItem {
  id: string;
  materialId: string;
  materialDescription: string;
  coefficient: number;
  unitPrice?: number;
  subtotal?: number;
}

export interface Material {
  id: string;
  sinapiCode: string;
  description: string;
  unit: string;
  origin: string;
}

export interface MaterialPrice {
  id: string;
  materialId: string;
  state: string;
  referenceMonth: string;
  price: number;
}

export interface CompositionCostResult {
  compositionId: string;
  totalCost: number;
  items: CompositionItem[];
}
