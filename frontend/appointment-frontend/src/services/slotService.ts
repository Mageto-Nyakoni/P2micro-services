// src/services/slotService.ts
import type { TimeSlot } from "@/types/slotTypes";

export async function fetchDoctorSlots(
  doctorId: number,
  date: string
): Promise<TimeSlot[]> {
  const res = await fetch(
    `http://localhost:8080/smart-appointment/api/slots/doctor/${doctorId}?date=${date}`
  );

  if (!res.ok) {
    throw new Error("Failed to fetch slots");
  }

  return res.json();
}
