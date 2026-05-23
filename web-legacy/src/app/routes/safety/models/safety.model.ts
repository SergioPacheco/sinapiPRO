export type Severity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface SafetyIncident {
  id: string;
  projectId: string;
  description: string;
  severity: Severity;
  date: string;
  resolved: boolean;
}
