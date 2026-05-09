export interface Supplier {
  id: string;
  code: string;
  name: string;
  tradeName?: string;
  taxId: string;
  email?: string;
  phone?: string;
  rating: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}
