-- Schedule baseline: snapshot of activities at a point in time
CREATE TABLE schedule_baseline (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL,
    name varchar(100) NOT NULL,
    snapshot jsonb NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_schedule_baseline_project ON schedule_baseline(project_id);
