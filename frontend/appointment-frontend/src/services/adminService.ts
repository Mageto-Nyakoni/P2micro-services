import { http } from "./http";

export type AvailabilityWindow = {
  windowId: number;
  date: string;
  startTime: string;
  endTime: string;
  active: boolean;
  doctorId: number;
  doctorName: string | null;
};

export type AdminTimeSlot = {
  slotId: number;
  doctorId: number | null;
  doctorName: string | null;
  dateAvailable: string;
  startTime: string;
  endTime: string;
  status: string;
};

export type AdminAppointment = {
  appointmentId: number;
  status: string;
  scheduledDateTime: string;
  createdAt: string;
  patientId?: number;
  patientName?: string;
  doctorId?: number;
  doctorName?: string;
  slotId?: number;
  dateAvailable?: string;
  startTime?: string;
  endTime?: string;
  appointmentTypeId?: number;
  appointmentType?: string;
};

export async function createAvailabilityWindow(
  doctorId: number,
  payload: { date: string; startTime: string; endTime: string }
) {
  const { data } = await http.post<AvailabilityWindow>(
    `/admin/doctors/${doctorId}/availability-windows`,
    payload
  );
  return data;
}

export async function deleteAvailabilityWindow(windowId: number) {
  const { data } = await http.delete<{ blockedCount: number; skippedSlotIds: number[] }>(
    `/admin/availability-windows/${windowId}`
  );
  return data;
}

export async function fetchAvailabilityWindows(doctorId: number) {
  const { data } = await http.get<AvailabilityWindow[]>(
    `/admin/doctors/${doctorId}/availability-windows`
  );
  return data;
}

export async function fetchAdminTimeSlots(params?: {
  doctorId?: number;
  from?: string;
  to?: string;
  status?: string;
}) {
  const { data } = await http.get<AdminTimeSlot[]>(
    `/admin/time-slots`,
    { params }
  );
  return data;
}

export async function fetchAdminAppointments(status?: string) {
  const { data } = await http.get<AdminAppointment[]>(
    `/admin/appointments`,
    { params: { status } }
  );
  return data;
}

export async function denyAppointment(appointmentId: number) {
  const { data } = await http.patch<AdminAppointment>(
    `/admin/appointments/${appointmentId}/deny`
  );
  return data;
}

export type StaffMember = {
  id: number;
  name: string;
  role: "DOCTOR" | "ADMIN";
  email?: string;
};

export async function fetchAdminStaff() {
  const { data } = await http.get<Array<{
    userId: number;
    firstName: string;
    lastName: string;
    email: string;
    roleName: string;
  }>>(`/admin/staff`);

  return data;
}
