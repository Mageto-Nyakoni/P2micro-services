import { useAuth } from "@/auth/useAuth";
import { http } from "@/services/http";

export const getMyAppointments = async (token: string) => {
  if (!token) throw new Error("No auth token found");

  const { data } = await http.get("/doctors/me/appointments/today", {
    headers: { Authorization: `Bearer ${token}` },
  });

  return data;
};
