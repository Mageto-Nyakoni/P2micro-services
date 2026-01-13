// src/components/availability/types.ts

export type TimeSlot = {
  time: string;
  available: boolean;
};

export type DoctorAvailability = {
  id: number;
  name: string;
  specialization: string;
  slots: TimeSlot[];
};
