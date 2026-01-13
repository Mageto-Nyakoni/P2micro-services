import { useNavigate } from "react-router-dom";
import { CalendarAvail, DoctorAvailability } from "@/components/availability";

/* ============= MOCK DATA ================= */

const availabilityByDate: Record<string, DoctorAvailability[]> = {
  "2026-01-10": [
    {
      id: 1,
      name: "Dr. Ben Martinez",
      specialization: "Cardiologist",
      slots: [
        { time: "10:00 AM", available: true },
        { time: "11:00 AM", available: true },
        { time: "2:00 PM", available: true },
      ],
    },
  ],
  "2026-01-11": [
    {
      id: 2,
      name: "Dr. Samuel Chen",
      specialization: "General Practitioner",
      slots: [
        { time: "9:00 AM", available: true },
        { time: "1:00 PM", available: true },
      ],
    },
    {
      id: 3,
      name: "Dr. Leyla Al-Sayed",
      specialization: "Pediatrician",
      slots: [
        { time: "10:00 AM", available: true },
        { time: "3:00 PM", available: false },
      ],
    },
  ],
};

/* ============= COMPONENT ================= */

const PatientHome: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gradient-to-b from-indigo-50 to-white">
      {/* HERO SECTION */}
      <section className="flex flex-col items-center text-center px-6 py-24">
        <h1 className="text-5xl font-extrabold text-gray-800 mb-6">
          Welcome to Your Health Journey
        </h1>

        <p className="text-xl text-gray-600 max-w-2xl mb-10">
          Book your appointment with ease and connect with the best healthcare
          professionals
        </p>

        <button
          onClick={() => navigate("/patient/doctors")}
          className="border-2 border-indigo-600 text-indigo-600 px-8 py-3 rounded-xl text-lg font-semibold hover:bg-indigo-600 hover:text-white transition"
        >
          Find Doctors
        </button>
      </section>

      {/* CALENDAR + AVAILABILITY */}
      <section className="pb-20">
        <CalendarAvail
          availabilityByDate={availabilityByDate}
          onBook={(doctor, date) =>
            navigate("/patient/book", {
              state: {
                doctor,
                selectedDate: date,
              },
            })
          }
        />
      </section>
    </div>
  );
};

export default PatientHome;

