import { getMyDoctor } from "@/services/doctorServices";
import { getMyAppointments } from "@/services/appointmentServices";
import { Doctor } from "@/types/doctorTypes";
import { useEffect, useState } from "react";
import { FaBriefcase, FaHospital } from "react-icons/fa6";
import { useAuth } from "@/auth/useAuth";

export default function DoctorHome() {
  const [doctor, setDoctor] = useState<Doctor | null>(null);
  const [appointments, setAppointments] = useState<any[]>([]);
  const [error, setError] = useState<string | null>(null);

  const { token, loading } = useAuth();

  useEffect(() => {
    if (!token || loading) return; // wait until token is available

    let cancelled = false;

    const fetchData = async () => {
      try {
        const doctorData = await getMyDoctor(token);
        if (!cancelled) setDoctor(doctorData);
      } catch (err) {
        console.error("Failed to load doctor profile:", err);
        if (!cancelled) setError("Failed to load doctor profile");
      }

      try {
        const appointmentsData = await getMyAppointments(token);
        if (!cancelled) setAppointments(appointmentsData);
      } catch (err) {
        console.error("Failed to load appointments:", err);
        if (!cancelled) setAppointments([]);
      }
    };

    fetchData();

    return () => {
      cancelled = true;
    };
  }, [token, loading]);

  if (loading) return <p>Loading...</p>;
  if (!doctor) return <p>Loading profile...</p>;
  if (error) return <p className="text-red-600">{error}</p>;

  return (
    <div className="w-full min-h-screen bg-slate-50">
      <div className="max-w-7xl mx-auto px-6 py-12">
        {/* Doctor Profile Section */}
        <div className="rounded-2xl p-8 mb-8 shadow-sm bg-white">
          <div className="flex gap-6 items-start">
            <div className="p-4 bg-indigo-100 rounded-xl shrink-0">
              <FaHospital className="text-5xl text-indigo-600" />
            </div>

            <div className="flex-1">
              <div className="mb-6">
                <h1 className="m-0 mb-1 font-bold text-2xl text-slate-800">
                  Dr. {doctor.user.firstName} {doctor.user.lastName}
                </h1>
                <p className="m-0 font-medium text-indigo-600 text-base">
                  {doctor.speciality?.specialityName ?? "—"}
                </p>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <div className="flex items-center gap-3 p-3 bg-slate-50 rounded-lg">
                  <div className="p-2 bg-indigo-100 rounded-full">
                    <FaBriefcase className="text-indigo-600 text-sm" />
                  </div>
                  <div>
                    <p className="m-0 text-xs font-medium text-slate-500 uppercase tracking-wide">
                      Experience
                    </p>
                    <p className="m-0 text-slate-800 font-semibold">
                      {doctor.experienceYears ?? 0} years
                    </p>
                  </div>
                </div>

                <div className="p-3 bg-slate-50 rounded-lg sm:col-span-2">
                  <p className="m-0 mb-2 text-xs font-medium text-slate-500 uppercase tracking-wide">
                    About
                  </p>
                  <p className="m-0 text-slate-700 leading-relaxed">{doctor.bio}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Appointments */}
        <div>
          <h2 className="text-2xl font-bold mb-4">Today's Appointments</h2>

          {appointments.length === 0 && <p>No appointments for today.</p>}

          {appointments.map((apt) => (
            <div key={apt.appointmentId} className="p-4 mb-3 bg-white shadow rounded-lg">
              <p><strong>Patient:</strong> {apt.patientFirstName} {apt.patientLastName}</p>
              <p><strong>Date & Time:</strong> {new Date(apt.scheduledDateTime).toLocaleString()}</p>
              <p><strong>Type:</strong> {apt.appointmentType ?? "—"}</p>
              <p><strong>Status:</strong> {apt.status}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
