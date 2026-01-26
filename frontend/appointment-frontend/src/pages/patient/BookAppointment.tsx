import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useParams } from "react-router-dom";
/* ================= TYPES ================= */

type Doctor = {
  id: number;
  name: string;
  speciality: string;
  services: string[];
};

/* ================= COMPONENT ================= */

export default function BookAppointment() {
  const navigate = useNavigate();
  const location = useLocation();

  /* ================= NAVIGATION STATE ================= */

  const preselectedDoctorId: number | undefined =
    location.state?.doctorId ?? location.state?.doctor?.id;

  const preselectedDate: string | undefined =
    location.state?.selectedDate;

  const role: "guest" | "patient" | undefined =
    location.state?.role;

  /* ================= STATE ================= */

  const [selectedDoctor, setSelectedDoctor] =
    useState<Doctor | null>(null);
  const [selectedService, setSelectedService] =
    useState("");
  const [date, setDate] = useState("");
  const [time, setTime] = useState("");

  /* ================= DOCTORS ================= */

  const doctors: Doctor[] = [
    {
      id: 1,
      name: "Dr. Ben Martinez",
      speciality: "Cardiology",
      services: [
        "Heart Checkup",
        "ECG Review",
        "Blood Pressure Monitoring",
      ],
    },
    {
      id: 2,
      name: "Dr. Samuel Chen",
      speciality: "General Medicine",
      services: [
        "General Checkup",
        "Physical Examination",
        "Vaccination",
      ],
    },
    {
      id: 3,
      name: "Dr. Layal Al-Sayed",
      speciality: "Pediatrics",
      services: [
        "Vaccination",
        "Fever/Cold Treatment",
        "Child Growth Monitoring",
      ],
    },
  ];

  /* ================= PRESELECT LOGIC ================= */

  useEffect(() => {
    if (preselectedDoctorId) {
      const foundDoctor = doctors.find(
        (d) => d.id === preselectedDoctorId
      );
      if (foundDoctor) {
        setSelectedDoctor(foundDoctor);
      }
    }

    if (preselectedDate) {
      setDate(
        new Date(preselectedDate)
          .toISOString()
          .split("T")[0]
      );
    }
  }, [preselectedDoctorId, preselectedDate]);

  /* ================= TIME SLOTS ================= */

  const availableTimes = [
    "09:00 AM",
    "10:00 AM",
    "11:00 AM",
    "02:00 PM",
    "03:00 PM",
  ];

  /* ================= SUBMIT ================= */

  const handleSubmit = () => {
    if (!selectedDoctor) return;

    if (role === "guest") {
      navigate("/login");
      return;
    }

    alert(
      `Appointment Booked!
Doctor: ${selectedDoctor.name}
Service: ${selectedService}
Date: ${date}
Time: ${time}`
    );
  };

  /* ================= UI ================= */

  return (
    <div className="min-h-screen bg-purple-50 px-6 py-10">
      <div className="max-w-4xl mx-auto bg-white p-8 rounded-2xl shadow-lg">
        <button
          onClick={() => navigate(-1)}
          className="mb-4 text-indigo-600 hover:underline"
        >
          ← Back
        </button>

        <h1 className="text-3xl font-bold text-center text-purple-700 mb-2">
          Book Appointment
        </h1>

        <p className="text-center text-gray-500 mb-8">
          Choose doctor, service, date and time
        </p>

        {/* STEP 1: DOCTOR */}
        <h2 className="font-semibold mb-3">
          1. Select Doctor
        </h2>

        <div className="space-y-4 mb-8">
          {doctors.map((doctor) => (
            <div
              key={doctor.id}
              onClick={() => {
                setSelectedDoctor(doctor);
                setSelectedService("");
              }}
              className={`border rounded-xl p-4 cursor-pointer flex justify-between
                ${
                  selectedDoctor?.id === doctor.id
                    ? "border-purple-600 bg-purple-50"
                    : "hover:border-gray-400"
                }`}
            >
              <div>
                <p className="font-medium">
                  {doctor.name}
                </p>
                <p className="text-sm text-gray-500">
                  {doctor.speciality}
                </p>
              </div>
            </div>
          ))}
        </div>

        {/* STEP 2: SERVICE */}
        {selectedDoctor && (
          <>
            <h2 className="font-semibold mb-3">
              2. Select Service
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
              {selectedDoctor.services.map((service) => (
                <button
                  key={service}
                  onClick={() => setSelectedService(service)}
                  className={`border rounded-lg p-3 text-left
                    ${
                      selectedService === service
                        ? "border-purple-600 bg-purple-50"
                        : "hover:border-gray-400"
                    }`}
                >
                  {service}
                </button>
              ))}
            </div>
          </>
        )}

        {/* STEP 3: DATE & TIME */}
        {selectedService && (
          <>
            <h2 className="font-semibold mb-3">
              3. Select Date & Time
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
              <input
                type="date"
                value={date}
                onChange={(e) =>
                  setDate(e.target.value)
                }
                className="border rounded-lg px-4 py-2"
              />

              <select
                value={time}
                onChange={(e) =>
                  setTime(e.target.value)
                }
                className="border rounded-lg px-4 py-2"
              >
                <option value="">
                  Select Time
                </option>
                {availableTimes.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </select>
            </div>
          </>
        )}

        {/* SUBMIT */}
        <div className="text-center">
          <button
            disabled={
              !selectedDoctor ||
              !selectedService ||
              !date ||
              !time
            }
            onClick={handleSubmit}
            className="bg-purple-600 disabled:bg-gray-400 text-white px-8 py-3 rounded-lg font-semibold"
          >
            Confirm Appointment
          </button>
        </div>
      </div>
    </div>
  );
}
