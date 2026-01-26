import { useNavigate } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { AuthContext } from "@/auth/AuthContext";
import CalendarAvail from "@/components/availability/CalendarAvail";
import type { DoctorAvailability } from "@/components/availability/types";
import { useLocation } from "react-router-dom";
interface Appointment {
  appointmentId: number;
  doctorName: string;
  date: string;
  startTime: string;
  endTime: string;
}

const PatientHome: React.FC = () => {
  const navigate = useNavigate();
  const auth = useContext(AuthContext);
const location = useLocation();
{location.state?.successMessage && (
  <div className="bg-green-100 text-green-700 p-3 rounded mb-4">
    {location.state.successMessage}
  </div>
)}
  // STATE
  const [loading, setLoading] = useState<boolean>(true);
  const [availabilityByDate, setAvailabilityByDate] =
    useState<Record<string, DoctorAvailability[]>>({});
  const [appointments, setAppointments] = useState<Appointment[]>([]);

  // FETCH availability from backend
  const fetchAvailability = () => {
    fetch("http://localhost:8080/smart-appointment/api/availability")
      .then((res) => res.json())
      .then((data) => {
        setAvailabilityByDate(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
      });
  };

  // FETCH patient appointments
  const fetchAppointments = () => {
    if (!auth || !auth.user) return;
    fetch(`http://localhost:8080/smart-appointment/api/appointments/patient/${auth.user.id}`)
      .then((res) => res.json())
      .then((data) => {
        const mapped = data.map((app: any) => ({
          appointmentId: app.appointmentId,
          doctorName: app.doctorName,
          date: app.dateTimeScheduled.split("T")[0],
          startTime: app.dateTimeScheduled.split("T")[1].slice(0, 5),
          endTime: "", // optional
        }));
        setAppointments(mapped);
      })
      .catch((err) => console.error(err));
  };

  // INITIAL DATA FETCH
  useEffect(() => {
    fetchAvailability();
    fetchAppointments();
  }, [auth]);

  // BOOKING handler
  const handleBookSlot = (doctorId: number, slotId: number) => {
    if (!auth || !auth.user) return;
    fetch("http://localhost:8080/smart-appointment/api/book", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ patientId: auth.user.id, doctorId, slotId }),
    })
      .then((res) => res.json())
      .then((newAppointment) => {
        alert("Slot booked successfully!");

        // Map backend response to your Appointment type
        const mappedAppointment: Appointment = {
          appointmentId: newAppointment.appointmentId,
          doctorName: newAppointment.doctorName,
          date: newAppointment.dateTimeScheduled.split("T")[0],
          startTime: newAppointment.dateTimeScheduled.split("T")[1].slice(0, 5),
          endTime: "", // optional
        };

        // Append new appointment to state immediately
        setAppointments((prev) => [...prev, mappedAppointment]);

        // Update availability if needed
        fetchAvailability();
         alert("Slot booked successfully!");

      })
      .catch((err) => console.error(err));
  };

  // AUTH GUARD
  if (!auth || !auth.isAuthenticated || !auth.user) {
    return (
      <h1 className="text-5xl font-extrabold text-gray-800 mb-6">Welcome</h1>
    );
  }

  const { firstName } = auth.user;

  return (
    <div className="min-h-screen bg-gradient-to-b from-indigo-50 to-white">
      {/* HERO SECTION */}
      <section className="flex flex-col items-center text-center px-6 py-24">
        <h1 className="text-5xl font-extrabold text-gray-800 mb-6">
          Welcome {firstName}!
        </h1>
        <p className="text-xl text-gray-600 max-w-2xl mb-10">
          Book your appointment with ease and connect with the best healthcare professionals.
        </p>
        <button
          onClick={() => navigate("/doctors")}
          className="border-2 border-indigo-600 text-indigo-600 px-8 py-3 rounded-xl text-lg font-semibold hover:bg-indigo-600 hover:text-white transition"
        >
          Find Doctors
        </button>
      </section>

      {/* CALENDAR + AVAILABILITY */}
      <section className="pb-20 px-6 min-h-[400px]">
        {loading ? (
          <p className="text-center text-gray-500">Loading availability...</p>
        ) : (
          <CalendarAvail
  availabilityByDate={availabilityByDate}
  onBook={(doctor, slot) => handleBookSlot(doctor.doctorId, slot.slotId)}
/>
        )}
      </section>

      {/* PATIENT APPOINTMENTS */}
      <section className="pb-20 px-6 mt-12">
        <h2 className="text-2xl font-bold mb-4">Your Appointments</h2>
        {appointments.length === 0 ? (
          <p className="text-gray-500">You have no appointments yet.</p>
        ) : (
          appointments.map((app) => (
            <div
                key={app.appointmentId} 
              className="border p-4 mb-2 rounded shadow-sm bg-white"
            >
              <p>
                <strong>Doctor:</strong> {app.doctorName}
              </p>
              <p>
                <strong>Date:</strong> {app.date}
              </p>
              <p>
                <strong>Time:</strong> {app.startTime} - {app.endTime}
              </p>
            </div>
          ))
        )}
      </section>
    </div>
  );
};

export default PatientHome;
