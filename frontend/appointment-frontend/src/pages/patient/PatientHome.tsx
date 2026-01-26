import { useNavigate } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { AuthContext } from "@/auth/AuthContext";
import CalendarAvail from "@/components/availability/CalendarAvail";
import type { DoctorAvailability } from "@/components/availability/types";
import { useLocation } from "react-router-dom";
import { AppointmentDto } from "@/types/appointmentTypes";
import { fetchAvailability } from "@/services/availabilityService";
import { fetchAppointmentsForPatient } from "@/services/appointmentService";

/*interface Appointment {
  appointmentId: number;
  doctorName: string;
  date: string;
  startTime: string;
  endTime: string;
}*/

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
  const [appointments, setAppointments] = useState<AppointmentDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [availabilityByDate, setAvailabilityByDate] =
    useState<Record<string, DoctorAvailability[]>>({});
  //const [appointments, setAppointments] = useState<Appointment[]>([]);

  // FETCH availability from backend
  //FETCH FIX
  const fetchAvailabilityData = async () => {
    try {
      const data = await fetchAvailability();
      setAvailabilityByDate(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // FETCH patient appointments
  //FETCH FIX
  const fetchAppointmentsData = async () => {
    if (!auth?.user) return;

    try {
      const appointments = await fetchAppointmentsForPatient(auth.user.id);
      setAppointments(appointments);
    } catch (err) {
      console.error(err);
    }
  };

  // INITIAL DATA FETCH
  useEffect(() => {
    fetchAvailabilityData();
    fetchAppointmentsData();
  }, [auth]);

  // BOOKING handler
  const handleBookSlot = (doctorId: number, slotId: number) => {
    if (!auth || !auth.user) return;
    //FETCH FIX
    fetch("http://localhost:8080/smart-appointment/api/book", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ patientId: auth.user.id, doctorId, slotId }),
    })
      .then((res) => res.json())
      .then((newAppointment) => {
        alert("Slot booked successfully!");

        // Map backend response to your Appointment type
        const mappedAppointment: AppointmentDto = {
          appointmentId: newAppointment.appointmentId,
          doctorName: newAppointment.doctorName,
          appointmentType: newAppointment.appointmentType,
          startTime: newAppointment.startTime,  // backend ISO string
          endTime: newAppointment.endTime,      // backend ISO string
          status: newAppointment.status,
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
  <strong>Date:</strong> {new Date(app.startTime).toLocaleDateString()}
</p>
<p>
  <strong>Time:</strong> {new Date(app.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} 
  {" - "} 
  {new Date(app.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
</p>
<p>
  <strong>Status:</strong> {app.status}
</p>
            </div>
          ))
        )}
      </section>
    </div>
  );
};

export default PatientHome;
