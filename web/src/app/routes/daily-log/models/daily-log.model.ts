export interface DailyLog {
  id: string;
  projectId: string;
  date: string;
  weather: string;
  temperature: number;
  notes: string;
  laborCount: number;
  equipmentCount: number;
}
