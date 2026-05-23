export type ContractStatus = 'DRAFT' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';

export interface Contract {
  id: string;
  projectId: string;
  supplierId: string;
  number: string;
  description: string;
  originalValue: number;
  retentionPct: number;
  status: ContractStatus;
  startDate: string;
  endDate?: string;
}
