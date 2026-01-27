import { http } from "@/services/http";

export const getMyAppointments = async () => {
  const { data } = await http.get("/doctors/me/appointments/today");
  return data;
};