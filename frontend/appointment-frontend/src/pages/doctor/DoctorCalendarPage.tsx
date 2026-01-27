/*import { useEffect, useState } from "react";
import DoctorCalendar from "./DoctorCalendar";
import type { Appointment } from "../../types/appointment";




export default function DoctorCalendarPage() {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);

  // 1️ Fetch appointments when page loads
  useEffect(() => {
    fetchAppointments();
  }, []);

  async function fetchAppointments() {
    try {
         
      const response = await fetch(`http://localhost:8080/smart-appointment/api/doctors/${doctorId}/appointments/week`);

      const data = await response.json();

      const mappedAppointments: Appointment[] = data.map((apt: any) => {
      const dateObj = new Date(apt.scheduledDateTime);
      const date = dateObj.toISOString().split("T")[0]; // YYYY-MM-DD
      const time = dateObj.toTimeString().slice(0, 5); // HH:mm
return {
        id: apt.appointmentId,
        patient_name: `${apt.patientFirstName} ${apt.patientLastName}`,
        appointment_type: apt.appointmentType,
        date,
        time,
        status: apt.status,
      };
    });

      setAppointments(mappedAppointments);
    } catch (error) {
      console.error("Error fetching appointments:", error);
    } finally {
      setLoading(false);
    }
  }

  // 2️ Delete appointment
  async function handleDelete(apt: Appointment): Promise<{ isOk: boolean }> {
    await fetch(`http://localhost:8080/api/appointments/${apt.id}`, {
      method: "DELETE",
    });

    setAppointments((prev) =>
      prev.filter((a) => a.id !== apt.id)
    );

    return { isOk: true };
  }

  // 3️Update appointment
  async function handleUpdate(apt: Appointment): Promise<{ isOk: boolean }> {
    await fetch(`http://localhost:8080/api/appointments/${apt.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(apt),
    });

    setAppointments((prev) =>
      prev.map((a) => (a.id === apt.id ? apt : a))
    );

    return { isOk: true };
  }

  if (loading) {
    return <p className="p-6">Loading appointments...</p>;
  }

  return (
    <DoctorCalendar
      appointments={appointments}
      onDelete={handleDelete}
      onUpdate={handleUpdate}
    />
  );
}*/


import { useEffect, useState } from "react";
import DoctorCalendar from "./DoctorCalendar";
import type { Appointment } from "../../types/appointment";

export default function DoctorCalendarPage() {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);

  // Fetch appointments when page loads
  useEffect(() => {
    fetchAppointments();
  }, []);

  async function fetchAppointments() {
    try {
      // 1️⃣ Get the current logged-in doctor
      const doctorRes = await fetch("http://localhost:8080/smart-appointment/api/doctors/me", {
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`, // or however you store JWT
        },
      });

      if (!doctorRes.ok) throw new Error("Failed to fetch doctor info");
      const doctorData = await doctorRes.json();
      const doctorId = doctorData.doctorId; // Make sure this matches your backend response

      // 2️⃣ Fetch upcoming appointments
      const aptRes = await fetch(`http://localhost:8080/smart-appointment/api/doctors/${doctorId}/appointments/upcoming`, {
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
        },
      });

      if (!aptRes.ok) throw new Error("Failed to fetch appointments");
      const data = await aptRes.json();

      // 3️⃣ Map to Appointment type for the calendar
      const mappedAppointments: Appointment[] = data.map((apt: any) => {
        const dateObj = new Date(apt.scheduledDateTime);
        const date = dateObj.toISOString().split("T")[0]; // YYYY-MM-DD
        const time = dateObj.toTimeString().slice(0, 5);   // HH:mm
        return {
          id: apt.appointmentId,
          patient_name: `${apt.patientFirstName} ${apt.patientLastName}`,
          appointment_type: apt.appointmentType,
          date,
          time,
          status: apt.status,
        };
      });

      setAppointments(mappedAppointments);
    } catch (error) {
      console.error("Error fetching appointments:", error);
    } finally {
      setLoading(false);
    }
  }

  // Delete appointment
  async function handleDelete(apt: Appointment): Promise<{ isOk: boolean }> {
    try {
      await fetch(`http://localhost:8080/smart-appointment/api/doctors/appointments/${apt.id}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
        },
      });

      setAppointments((prev) => prev.filter((a) => a.id !== apt.id));
      return { isOk: true };
    } catch {
      return { isOk: false };
    }
  }

  // Update appointment
  async function handleUpdate(apt: Appointment): Promise<{ isOk: boolean }> {
    try {
      await fetch(`http://localhost:8080/smart-appointment/api/doctors/appointments/${apt.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
        },
        body: JSON.stringify(apt),
      });

      setAppointments((prev) => prev.map((a) => (a.id === apt.id ? apt : a)));
      return { isOk: true };
    } catch {
      return { isOk: false };
    }
  }

  if (loading) return <p className="p-6">Loading appointments...</p>;

  return (
    <DoctorCalendar
      appointments={appointments}
      onDelete={handleDelete}
      onUpdate={handleUpdate}
    />
  );
}
