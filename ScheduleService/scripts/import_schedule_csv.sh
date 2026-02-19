#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage:
  import_schedule_csv.sh \
    --time-slots /absolute/path/time_slot.csv \
    --availability-windows /absolute/path/availability_window.csv \
    [--appointment-types /absolute/path/appointment_type.csv] \
    [--container postgres-schedule] \
    [--db schedule_db] \
    [--user schedule_user] \
    [--password schedule_password] \
    [--truncate]

Required CSV column order:
  time_slot.csv:
    slot_id,doctor_id,date_available,start_time,end_time,status,created_at
  availability_window.csv:
    window_id,doctor_id,available_date,start_time,end_time,active
  appointment_type.csv (optional):
    type_id,name,estimated_time,description

Notes:
  - This script runs against the Docker Postgres container.
  - If --truncate is passed, tables are cleared before import.
EOF
}

TIME_SLOTS_CSV=""
AVAILABILITY_WINDOWS_CSV=""
APPOINTMENT_TYPES_CSV=""
DB_CONTAINER="postgres-schedule"
DB_NAME="schedule_db"
DB_USER="schedule_user"
DB_PASSWORD="schedule_password"
TRUNCATE_TABLES="false"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --time-slots)
      TIME_SLOTS_CSV="$2"
      shift 2
      ;;
    --availability-windows)
      AVAILABILITY_WINDOWS_CSV="$2"
      shift 2
      ;;
    --appointment-types)
      APPOINTMENT_TYPES_CSV="$2"
      shift 2
      ;;
    --container)
      DB_CONTAINER="$2"
      shift 2
      ;;
    --db)
      DB_NAME="$2"
      shift 2
      ;;
    --user)
      DB_USER="$2"
      shift 2
      ;;
    --password)
      DB_PASSWORD="$2"
      shift 2
      ;;
    --truncate)
      TRUNCATE_TABLES="true"
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1"
      usage
      exit 1
      ;;
  esac
done

if [[ -z "$TIME_SLOTS_CSV" || -z "$AVAILABILITY_WINDOWS_CSV" ]]; then
  echo "Both --time-slots and --availability-windows are required."
  usage
  exit 1
fi

for file in "$TIME_SLOTS_CSV" "$AVAILABILITY_WINDOWS_CSV"; do
  if [[ ! -f "$file" ]]; then
    echo "File not found: $file"
    exit 1
  fi
done

if [[ -n "$APPOINTMENT_TYPES_CSV" && ! -f "$APPOINTMENT_TYPES_CSV" ]]; then
  echo "File not found: $APPOINTMENT_TYPES_CSV"
  exit 1
fi

if ! command -v docker >/dev/null 2>&1; then
  echo "docker is required but not found in PATH."
  exit 1
fi

if ! docker ps --format '{{.Names}}' | grep -qx "$DB_CONTAINER"; then
  echo "Container '$DB_CONTAINER' is not running."
  echo "Start it with: docker compose up -d postgres-schedule"
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCHEMA_SQL="$SCRIPT_DIR/../db/create_schedule_schema.sql"
if [[ ! -f "$SCHEMA_SQL" ]]; then
  echo "Schema file not found: $SCHEMA_SQL"
  exit 1
fi

REMOTE_DIR="/tmp/schedule_csv_import_$$"
docker exec "$DB_CONTAINER" mkdir -p "$REMOTE_DIR"

cleanup() {
  docker exec "$DB_CONTAINER" rm -rf "$REMOTE_DIR" >/dev/null 2>&1 || true
}
trap cleanup EXIT

docker cp "$TIME_SLOTS_CSV" "$DB_CONTAINER:$REMOTE_DIR/time_slot.csv"
docker cp "$AVAILABILITY_WINDOWS_CSV" "$DB_CONTAINER:$REMOTE_DIR/availability_window.csv"
if [[ -n "$APPOINTMENT_TYPES_CSV" ]]; then
  docker cp "$APPOINTMENT_TYPES_CSV" "$DB_CONTAINER:$REMOTE_DIR/appointment_type.csv"
fi

docker exec -i -e PGPASSWORD="$DB_PASSWORD" "$DB_CONTAINER" \
  psql -v ON_ERROR_STOP=1 -U "$DB_USER" -d "$DB_NAME" < "$SCHEMA_SQL"

if [[ "$TRUNCATE_TABLES" == "true" ]]; then
  docker exec -i -e PGPASSWORD="$DB_PASSWORD" "$DB_CONTAINER" \
    psql -v ON_ERROR_STOP=1 -U "$DB_USER" -d "$DB_NAME" <<'SQL'
TRUNCATE TABLE time_slot, availability_window, appointment_type RESTART IDENTITY;
SQL
fi

docker exec -i -e PGPASSWORD="$DB_PASSWORD" "$DB_CONTAINER" \
  psql -v ON_ERROR_STOP=1 -U "$DB_USER" -d "$DB_NAME" <<SQL
\copy availability_window(window_id,doctor_id,available_date,start_time,end_time,active) FROM '$REMOTE_DIR/availability_window.csv' WITH (FORMAT csv, HEADER true)
\copy time_slot(slot_id,doctor_id,date_available,start_time,end_time,status,created_at) FROM '$REMOTE_DIR/time_slot.csv' WITH (FORMAT csv, HEADER true)
SQL

if [[ -n "$APPOINTMENT_TYPES_CSV" ]]; then
  docker exec -i -e PGPASSWORD="$DB_PASSWORD" "$DB_CONTAINER" \
    psql -v ON_ERROR_STOP=1 -U "$DB_USER" -d "$DB_NAME" <<SQL
\copy appointment_type(type_id,name,estimated_time,description) FROM '$REMOTE_DIR/appointment_type.csv' WITH (FORMAT csv, HEADER true)
SQL
fi

docker exec -i -e PGPASSWORD="$DB_PASSWORD" "$DB_CONTAINER" \
  psql -v ON_ERROR_STOP=1 -U "$DB_USER" -d "$DB_NAME" <<'SQL'
SELECT setval(
  pg_get_serial_sequence('time_slot', 'slot_id'),
  COALESCE((SELECT MAX(slot_id) FROM time_slot), 1),
  (SELECT COUNT(*) > 0 FROM time_slot)
);

SELECT setval(
  pg_get_serial_sequence('availability_window', 'window_id'),
  COALESCE((SELECT MAX(window_id) FROM availability_window), 1),
  (SELECT COUNT(*) > 0 FROM availability_window)
);

SELECT setval(
  pg_get_serial_sequence('appointment_type', 'type_id'),
  COALESCE((SELECT MAX(type_id) FROM appointment_type), 1),
  (SELECT COUNT(*) > 0 FROM appointment_type)
);
SQL

echo "ScheduleService CSV import completed successfully."
