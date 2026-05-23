export interface Development {
  id: string;
  name: string;
  address: string;
  city: string;
  state: string;
  totalUnits: number;
  status: string;
  launchDate: string | null;
}

export interface DevelopmentUnit {
  id: string;
  code: string;
  type: string;
  area: number;
  price: number;
  status: 'AVAILABLE' | 'RESERVED' | 'SOLD';
  floor: number | null;
  bedrooms: number | null;
}

export interface SalesProposal {
  id: string;
  unitCode: string;
  clientName: string;
  proposalDate: string;
  proposedPrice: number;
  downPayment: number | null;
  installments: number;
  status: 'PENDING' | 'APPROVED' | 'SIGNED' | 'REJECTED' | 'CANCELLED';
}

export interface BrokerCommission {
  id: string;
  brokerName: string;
  percentage: number;
  amount: number;
  status: 'PENDING' | 'PAID';
  paidDate: string | null;
}
