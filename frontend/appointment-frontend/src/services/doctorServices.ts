import { Doctor } from "@/types/doctorTypes";
import { http } from "./http";
import { getMyAppointments } from "@/services/appointmentServices";

// This function now requires a token
export async function getMyDoctor(token: string): Promise<Doctor> {
  const { data } = await http.get("/doctors/me", {
    headers: {
      Authorization: `Bearer ${token}`, // send the token
    },
  });
  return data;
}

export async function getAllDoctors(signal?: AbortSignal): Promise<Doctor[]> {
  const { data } = await http.get("/doctors", { signal });
  return data;
}