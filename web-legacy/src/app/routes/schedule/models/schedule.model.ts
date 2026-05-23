export interface ScheduleActivity {
  id: string;
  name: string;
  plannedStart: string;
  plannedEnd: string;
  actualStart?: string;
  actualEnd?: string;
  weight: number;
  progressPct: number;
  sortOrder: number;
}

export interface SCurvePoint {
  period: string;
  plannedCumulative: number;
  actualCumulative: number;
}

export interface SCurveData {
  points: SCurvePoint[];
}

export interface ScheduleBaseline {
  id: string;
  name: string;
  activityCount: number;
  createdAt: string;
}

export interface ScheduleTrackingLine {
  activityId: string;
  name: string;
  weight: number;
  progressPct: number;
  plannedStart: string;
  plannedEnd: string;
  actualStart?: string;
  actualEnd?: string;
  status: 'ON_TRACK' | 'DELAYED';
}

export interface Holiday {
  id: string;
  projectId: string;
  holidayDate: string;
  description?: string;
  recurring: boolean;
}
