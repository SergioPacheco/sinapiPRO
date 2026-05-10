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
