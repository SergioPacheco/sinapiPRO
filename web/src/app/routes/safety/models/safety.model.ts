export type Severity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface SafetyIncident {
  id: string;
  budgetId: string;
  description: string;
  severity: Severity;
  date: string;
  resolved: boolean;
}
