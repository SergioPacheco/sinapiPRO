-- Daily log photos
CREATE TABLE daily_log_photo (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id) ON DELETE CASCADE,
    file_path varchar(500) NOT NULL,
    caption varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_daily_log_photo_log ON daily_log_photo(daily_log_id);
