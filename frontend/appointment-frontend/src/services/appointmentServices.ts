import { useAuth } from "@/auth/useAuth";
import { http } from "@/services/http";



export const getMyAppointments = async (token: string) => {
  if (!token) throw new Error("No auth token found");

  const { data } = await http.get("/doctors/me/appointments/today", {
    headers: { Authorization: `Bearer ${token}` },
  });

  return data;
};

export async function updateDoctorAppointmentStatus(
  doctorId: number,
  appointmentId: number,
  status: "COMPLETED" | "CANCELLED" | "NO_SHOW",
  token: string
) {
if (!doctorId) throw new Error("doctorId is undefined");

  const response = await fetch(
    `http://localhost:8080/smart-appointment/api/doctors/${doctorId}/appointments/${appointmentId}/status`,
    {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ status }),
    }
  );

 if (!response.ok) {
    const errorText = await response.text();
    console.error("Backend error:", errorText);
    throw new Error("Failed to update appointment status");
  }

  return response.json(); // DoctorAppointmentView
}
