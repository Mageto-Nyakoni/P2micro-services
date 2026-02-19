-- ScheduleService schema bootstrap for local Postgres.
-- Safe to run multiple times.

CREATE TABLE IF NOT EXISTS appointment_type (
    type_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    estimated_time INTEGER,
    description TEXT
);

CREATE TABLE IF NOT EXISTS availability_window (
    window_id SERIAL PRIMARY KEY,
    doctor_id INTEGER NOT NULL,
    available_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS time_slot (
    slot_id SERIAL PRIMARY KEY,
    doctor_id INTEGER NOT NULL,
    date_available DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT uq_time_slot_doctor_date_start UNIQUE (doctor_id, date_available, start_time),
    CONSTRAINT chk_time_slot_status CHECK (status IN ('AVAILABLE', 'HELD', 'BOOKED', 'BLOCKED'))
);

CREATE INDEX IF NOT EXISTS idx_time_slot_doctor_id ON time_slot (doctor_id);
CREATE INDEX IF NOT EXISTS idx_time_slot_date_available ON time_slot (date_available);
