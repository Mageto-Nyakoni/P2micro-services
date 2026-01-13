/* ================= TIME SLOT ================= */

export type TimeSlot = {
  time: string;
  available: boolean;
};

/* ================= DOCTOR AVAILABILITY ================= */

export type DoctorAvailability = {
  id: number;
  name: string;
  specialization: string;
  slots: TimeSlot[];
};
