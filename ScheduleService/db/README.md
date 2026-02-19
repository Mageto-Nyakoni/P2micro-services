# ScheduleService DB Setup and CSV Import

This module uses Postgres:
- DB name: `schedule_db`
- User: `schedule_user`
- Password: `schedule_password`
- Port: `5436`
- Docker container: `postgres-schedule`

## 1) Start Postgres container

From repo root:

```bash
cd /Users/mageto/Cognizant/P2micro-services
docker compose up -d postgres-schedule
```

## 2) Import CSV data

Run:

```bash
/Users/mageto/Cognizant/P2micro-services/ScheduleService/scripts/import_schedule_csv.sh \
  --time-slots /absolute/path/time_slot.csv \
  --availability-windows /absolute/path/availability_window.csv \
  --appointment-types /absolute/path/appointment_type.csv \
  --truncate
```

`--appointment-types` is optional.

## 3) Required CSV column order

`time_slot.csv`

```csv
slot_id,doctor_id,date_available,start_time,end_time,status,created_at
```

`availability_window.csv`

```csv
window_id,doctor_id,available_date,start_time,end_time,active
```

`appointment_type.csv` (optional)

```csv
type_id,name,estimated_time,description
```

## 4) Verify rows

```bash
docker exec -e PGPASSWORD=schedule_password postgres-schedule \
  psql -U schedule_user -d schedule_db -c "SELECT COUNT(*) FROM time_slot;"

docker exec -e PGPASSWORD=schedule_password postgres-schedule \
  psql -U schedule_user -d schedule_db -c "SELECT COUNT(*) FROM availability_window;"
```
