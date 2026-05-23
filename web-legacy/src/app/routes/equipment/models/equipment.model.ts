export type EquipmentStatus = 'AVAILABLE' | 'IN_USE' | 'MAINTENANCE';

export interface Equipment {
  id: string;
  code: string;
  name: string;
  type: string;
  status: EquipmentStatus;
  hourlyRate: number;
}
