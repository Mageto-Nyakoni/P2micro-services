import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getMyPatient } from "@/services/patientServices";
import { Patient } from "@/types/patientTypes";

type Appointment = {
  id: number;
  date: string;
  doctorId: number; 
  doctor: string;
  department: string;
  canRebook: boolean;
};


function PatientProfile() {
  const navigate = useNavigate();

  const [patient, setPatient] = useState <Patient | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    (async () => {
      try{
        const data = await getMyPatient();
        if (!cancelled) {
          setPatient(data);
        }
      } catch (err) {
        console.error(err);
        if (!cancelled) {
          setError("Failed to load Profile");
        }
      }
    })();

    return () => {
      cancelled = true;
    }
  }, []);

  if (error) return <p className="text-red-600">{error}</p>;
  if (!patient) return <p>Loading profile...</p>;

  const appointments: Appointment[] = [
  {
    id: 1,
    date: "12 Jan 2026",
    doctorId: 1,
    doctor: "Dr. Ben Martinez",
    department: "Cardiology",
    canRebook: true
  },
  {
    id: 2,
    date: "28 Dec 2025",
    doctorId: 2,    
    doctor: "Dr. Samuel Chen",
    department: "General Medicine",
    canRebook: true
  },
  {
    id: 3,
    date: "05 Dec 2025",
    doctorId: 3,
    doctor: "Dr. Leya Al-Sayed",
    department: "Pediatrics",
    canRebook: true
  }
];

  return (
    <div className="min-h-screen bg-purple-50 p-6">
      
      {/* Header */}
      <div className="max-w-5xl mx-auto flex justify-between items-center mb-6">
        <button
          onClick={() => navigate("/patient/home")}
          className="bg-purple-600 text-white px-4 py-2 rounded-lg hover:bg-purple-700"
        >
          ← Back
        </button>

        <h2 className="text-4xl font-bold text-purple-700">
          Patient Profile
        </h2>

        <button
          onClick={() => navigate("/patient/profile/edit", {state: { patient }})}
          className="bg-purple-500 text-white px-4 py-2 rounded-lg hover:bg-purple-600"
        >
          Edit Profile
        </button>
      </div>

      {/* Profile Card */}
      <div className="max-w-5xl mx-auto bg-white rounded-2xl shadow-lg p-6">
        
        {/* Top Section */}
        <div className="flex flex-col md:flex-row items-center gap-6 border-b pb-6">

          <div className="text-center md:text-left">
            <h3 className="text-2xl font-bold text-gray-800">
              {patient.user.firstName} {patient.user.lastName}
            </h3>
            <p className="text-gray-500">
            {patient.gender
              ? patient.gender.charAt(0).toUpperCase() + patient.gender.slice(1)
              : "—"
            }, {patient.age} years
            </p>

          </div>
        </div>

        {/* Details Section */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6">
          
          <ProfileCard title="Personal Information">
            <ProfileItem label="Date of Birth" value={patient.dateOfBirth} />
            <ProfileItem label="Address" value={patient.address} />
            <ProfileItem label="Phone Number" value={patient.phoneNumber} />
          </ProfileCard>

          <ProfileCard title="Medical Information">
            <ProfileItem label="Allergies" value={patient.allergies?.length ? patient.allergies.map(a => a.name).join(", ") : "None"} />
            <ProfileItem label="Blood Type" value={patient.bloodType?.name ?? "-"} />
          </ProfileCard>

        </div>

{/* Appointment History */}
<div className="mt-10">
  <h3 className="text-xl font-bold text-purple-700 mb-4">
    Previous Appointments
  </h3>

  <div className="space-y-4">
    {appointments.map((appt) => (
      <div
        key={appt.id}
        className="flex flex-col md:flex-row justify-between items-start md:items-center bg-purple-50 rounded-xl p-4"
      >
        <div>
          <p className="font-semibold text-gray-800">
            {appt.department}
          </p>
          <p className="text-sm text-gray-600">
            {appt.doctor}
          </p>
          <p className="text-sm text-gray-500">
            {appt.date}
          </p>
        </div>

        <button
          disabled={!appt.canRebook}
           onClick={() =>
                    navigate("/patient/book", {
                      state: {
                        doctorId: appt.doctorId, // KEY LINE
                      },
                    })
                  }
          className={`mt-3 md:mt-0 px-5 py-2 rounded-lg font-medium transition
            ${
              appt.canRebook
                ? "bg-purple-600 text-white hover:bg-purple-700"
                : "bg-gray-300 text-gray-500 cursor-not-allowed"
            }
          `}
        >
          Rebook
        </button>
      </div>
    ))}
  </div>
</div>

      </div>
    </div>
  );
}

function ProfileCard({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div className="bg-purple-50 rounded-xl p-4">
      <h4 className="text-lg font-semibold text-purple-700 mb-3">
        {title}
      </h4>
      <div className="space-y-2">{children}</div>
    </div>
  );
}

function ProfileItem({ label, value }: { label: string; value: string | null}) {
  return (
    <div className="flex justify-between text-sm">
      <span className="text-gray-600 font-medium">{label}</span>
      <span className="text-gray-800">{value}</span>
    </div>
  );
}

export default PatientProfile;