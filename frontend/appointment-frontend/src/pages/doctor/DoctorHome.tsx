import { getMyDoctor } from "@/services/doctorServices";
import { getMyAppointments } from "@/services/appointmentServices";
// import { updateAppointmentStatus } from "@/services/appointmentServices"; // uncomment when backend ready
import { Doctor } from "@/types/doctorTypes";
import { useEffect, useState } from "react";
import { FaBriefcase, FaHospital } from "react-icons/fa6";
import { useAuth } from "@/auth/useAuth";

export default function DoctorHome() {
  const [doctor, setDoctor] = useState<Doctor | null>(null);
  const [appointments, setAppointments] = useState<any[]>([]);
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);

  const { token, loading } = useAuth();

  useEffect(() => {
    if (!token || loading) return;

    let cancelled = false;

    const fetchData = async () => {
      try {
        const doctorData = await getMyDoctor();
        if (!cancelled) setDoctor(doctorData);
      } catch (err) {
        console.error(err);
        if (!cancelled) setError("Failed to load doctor profile");
      }

      try {
        const appointmentsData = await getMyAppointments();
        if (!cancelled) setAppointments(appointmentsData);
      } catch (err) {
        console.error(err);
        if (!cancelled) setAppointments([]);
      }
    };

    fetchData();
    return () => {
      cancelled = true;
    };
  }, [token, loading]);

  // ✅ Only show CONFIRMED appointments
  const confirmedAppointments = appointments.filter(
    (apt) => apt.status === "CONFIRMED"
  );

  const handleStatusChange = async (
    appointmentId: number,
    status: "COMPLETED" | "CANCELLED" | "NO_SHOW"
  ) => {
    try {
      // 🔥 Call backend when ready
      // await updateAppointmentStatus(appointmentId, status, token);

      // 🔥 Instantly remove from UI
      setAppointments((prev) =>
        prev.filter((apt) => apt.appointmentId !== appointmentId)
      );

      setExpandedId(null);
    } catch (err) {
      console.error("Status update failed", err);
      alert("Failed to update appointment status");
    }
  };

  if (loading) return <p>Loading...</p>;
  if (!doctor) return <p>Loading profile...</p>;
  if (error) return <p className="text-red-600">{error}</p>;

  return (
    <div className="w-full min-h-screen bg-slate-50">
      <div className="max-w-7xl mx-auto px-6 py-12">

        {/* Doctor Profile */}
        <div className="rounded-2xl p-8 mb-8 shadow-sm bg-white">
          <div className="flex gap-6 items-start">
            <div className="p-4 bg-indigo-100 rounded-xl">
              <FaHospital className="text-5xl text-indigo-600" />
            </div>

            <div className="flex-1">
              <h1 className="text-2xl font-bold text-slate-800">
                Dr. {doctor.user.firstName} {doctor.user.lastName}
              </h1>

              <p className="text-indigo-600 font-medium">
                {doctor.speciality?.specialityName ?? "—"}
              </p>

              <div className="mt-4 grid grid-cols-1 sm:grid-cols-2 gap-6">
                <div className="flex items-center gap-3 p-3 bg-slate-50 rounded-lg">
                  <div className="p-2 bg-indigo-100 rounded-full">
                    <FaBriefcase className="text-indigo-600 text-sm" />
                  </div>
                  <div>
                    <p className="text-xs text-slate-500 uppercase">
                      Experience
                    </p>
                    <p className="font-semibold">
                      {doctor.experienceYears ?? 0} years
                    </p>
                  </div>
                </div>

                <div className="p-3 bg-slate-50 rounded-lg sm:col-span-2">
                  <p className="text-xs text-slate-500 uppercase mb-1">
                    About
                  </p>
                  <p className="text-slate-700">{doctor.bio}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Appointments */}
        <h2 className="text-2xl font-bold mb-4">
          Today&apos;s Appointments
        </h2>

        {confirmedAppointments.length === 0 && (
          <p>No confirmed appointments.</p>
        )}

       {confirmedAppointments.map((apt) => {
  const isExpanded = expandedId === apt.appointmentId;

  return (
    <div
      key={apt.appointmentId}
      className="mb-4 bg-white rounded-2xl shadow-sm border border-violet-100 hover:shadow-md transition-all"
    >
      {/* HEADER */}
      <div
        onClick={() =>
          setExpandedId(isExpanded ? null : apt.appointmentId)
        }
        className="p-5 cursor-pointer flex justify-between items-start"
      >
        <div className="space-y-1">
          <p className="text-slate-800 font-semibold">
            {apt.patientFirstName} {apt.patientLastName}
          </p>

          <p className="text-sm text-slate-500">
            {new Date(apt.scheduledDateTime).toLocaleString()}
          </p>

          <p className="text-sm text-slate-500">
            Type: {apt.appointmentType ?? "—"}
          </p>
        </div>

        {/* STATUS BADGE */}
        <span className="px-3 py-1 text-xs font-semibold rounded-full bg-violet-100 text-violet-700">
          {apt.status}
        </span>
      </div>

      {/* EXPANDED ACTIONS */}
      {isExpanded && (
        <div className="px-5 pb-5 pt-3 border-t border-violet-100 bg-violet-50 rounded-b-2xl">
          <p className="text-sm text-slate-600 mb-3">
            Update appointment status
          </p>

          <div className="flex gap-3 flex-wrap">
            <button
              onClick={() =>
                handleStatusChange(apt.appointmentId, "COMPLETED")
              }
              className="px-4 py-2 rounded-xl text-sm font-medium bg-emerald-500 text-white hover:bg-emerald-600 transition"
            >
              Completed
            </button>

            <button
              onClick={() =>
                handleStatusChange(apt.appointmentId, "CANCELLED")
              }
              className="px-4 py-2 rounded-xl text-sm font-medium bg-rose-500 text-white hover:bg-rose-600 transition"
            >
              Cancelled
            </button>

            <button
              onClick={() =>
                handleStatusChange(apt.appointmentId, "NO_SHOW")
              }
              className="px-4 py-2 rounded-xl text-sm font-medium bg-amber-400 text-white hover:bg-amber-500 transition"
            >
              No Show
            </button>
          </div>
        </div>
      )}
    </div>
  );
})}

      </div>
    </div>
  );
}

