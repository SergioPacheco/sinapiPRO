export interface DailyLog {
  id: string;
  projectId: string;
  logDate: string;
  weatherMorning?: string;
  weatherAfternoon?: string;
  observations?: string;
  laborCount: number;
  equipmentCount: number;
  occurrenceCount?: number;
  photoCount?: number;
}

export interface CreateDailyLogRequest {
  logDate: string;
  weatherMorning?: string;
  weatherAfternoon?: string;
  observations?: string;
  labor?: DailyLogLaborCreateEntry[];
  equipment?: DailyLogEquipmentCreateEntry[];
  occurrences?: DailyLogOccurrenceCreateEntry[];
}

export interface DailyLogDetail {
  id: string;
  logDate: string;
  weatherMorning?: string;
  weatherAfternoon?: string;
  observations?: string;
  labor: DailyLogLaborEntry[];
  equipment: DailyLogEquipmentEntry[];
  occurrences: DailyLogOccurrenceEntry[];
  photos: DailyLogPhotoEntry[];
}

export interface DailyLogLaborEntry {
  id: string;
  workerName: string;
  role: string;
  hours: number;
}

export interface DailyLogLaborCreateEntry {
  workerName: string;
  role: string;
  hours: number;
}

export interface DailyLogEquipmentEntry {
  id: string;
  equipmentName: string;
  hoursUsed: number;
  hoursIdle: number;
}

export interface DailyLogEquipmentCreateEntry {
  equipmentName: string;
  hoursUsed: number;
  hoursIdle?: number;
}

export interface DailyLogOccurrenceEntry {
  id: string;
  type: string;
  description: string;
}

export interface DailyLogOccurrenceCreateEntry {
  type: string;
  description: string;
}

export interface DailyLogPhotoEntry {
  id: string;
  filePath: string;
  caption?: string;
}

export interface WeatherDelay {
  id: string;
  delayDate: string;
  weatherCondition: string;
  hoursLost: number;
  fullDayLost: boolean;
  impactDescription?: string;
}

export interface WeatherDelaySummary {
  totalDelays: number;
  fullDaysLost: number;
  totalHoursLost: number;
}
