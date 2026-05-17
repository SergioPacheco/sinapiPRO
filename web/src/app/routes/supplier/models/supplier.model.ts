export interface Supplier {
  id: string;
  code: string;
  name: string;
  tradeName?: string;
  taxId: string;
  email?: string;
  phone?: string;
  contactName?: string;
  website?: string;
  category: 'GENERAL' | 'MATERIAL' | 'SERVICE' | 'EQUIPMENT' | 'SUBCONTRACTOR';
  qualificationStatus: 'APPROVED' | 'UNDER_REVIEW' | 'BLOCKED' | 'PROSPECT';
  paymentTermDays: number;
  leadTimeDays: number;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  notes?: string;
  rating: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}
