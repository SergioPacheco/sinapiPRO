export interface ServiceTicket {
  id: string;
  unitId: string | null;
  clientName: string;
  category: string;
  description: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
  assignedTo: string | null;
  resolution: string | null;
  openedAt: string;
  dueDate: string | null;
  resolvedAt: string | null;
}

export interface TicketSummary {
  open: number;
  inProgress: number;
  resolved: number;
  closed: number;
}
