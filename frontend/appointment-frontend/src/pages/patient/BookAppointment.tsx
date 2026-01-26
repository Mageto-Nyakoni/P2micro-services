import DoctorBrowseFilters from "@/components/doctor/DoctorBrowseFilters";
import { useDoctorsBrowse } from "@/services/useDoctorBrowse";
import { AppointmentType, Doctor } from "@/types/doctorTypes";
import { useEffect, useState, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
<<<<<<< HEAD
import { useAuth } from "@/auth/useAuth";
=======
import { useParams } from "react-router-dom";
/* ================= TYPES ================= */

type Doctor = {
  id: number;
  name: string;
  speciality: string;
  services: string[];
};

/* ================= COMPONENT ================= */
>>>>>>> patients-appointment

export default function BookAppointment() {
	const navigate = useNavigate();
	const location = useLocation();

	const prefillQuery: string | undefined = location.state?.prefillQuery;
	const didPrefillRef = useRef(false);

  	const preselectedDoctorId: number | undefined =
		location.state?.doctorId ??
		location.state?.doctor?.doctorId ??
		location.state?.doctor?.id;

  const { isAuthenticated, user } = useAuth();

	const {
		filteredDoctors, 
		loading, 
		error, 
		query, 
		setQuery, 
		speciality, 
		setSpeciality, 
		gender, 
		setGender, 
		specialityOptions, 
		genderOptions
	} = useDoctorsBrowse();


    const [selectedDoctor, setSelectedDoctor] = useState<Doctor | null>(null);
    const [selectedAppointment, setSelectedAppointment] = useState<AppointmentType | null>(null);
    const [date, setDate] = useState<string>("");
    const [time, setTime] = useState<string>("");


	useEffect(() => {
		// Prefill search bar
		if (!didPrefillRef.current && prefillQuery) {
			setQuery(prefillQuery);
			didPrefillRef.current = true;
		}

		// Auto-select doctor
		if (preselectedDoctorId && filteredDoctors.length > 0) {
			const found = filteredDoctors.find((d) => d.doctorId === preselectedDoctorId);
			if (found) {
				setSelectedDoctor(found);
				setSelectedAppointment(null);
			}
		}
	}, [prefillQuery, preselectedDoctorId, filteredDoctors, setQuery]);

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

		if (!isAuthenticated || !user || user.role !== "Patient") {
			navigate("/login", { replace: true });
			return;
		}

		// ++++ TODO: POST APPOINTMENT TO BACKEND ++++	
		alert(
			`Appointment Booked!
			Doctor: Dr. ${selectedDoctor.user.firstName} ${selectedDoctor.user.lastName}
			Appointment Type: ${selectedAppointment?.name ?? ""}
			Date: ${date}
			Time: ${time}`
		);

    navigate("/patient/home", { replace: true });
	};


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

		<DoctorBrowseFilters
			query={query}
			onQueryChange={setQuery}
			speciality={speciality}
			onSpecialityChange={setSpeciality}
			gender={gender}
			onGenderChange={setGender}
			specialityOptions={specialityOptions}
			genderOptions={genderOptions}
			disabled={loading}
		/>

        <div className="space-y-4 mb-8">
          {filteredDoctors.map((doctor) => (
            <div
              key={doctor.doctorId}
              onClick={() => {
                setSelectedDoctor(doctor);
                setSelectedAppointment(null);
              }}
              className={`border rounded-xl p-4 cursor-pointer flex justify-between
                ${
                  selectedDoctor?.doctorId === doctor.doctorId
                    ? "border-purple-600 bg-purple-50"
                    : "hover:border-gray-400"
                }`}
            >
              <div>
                <p className="font-medium">
                  Dr. {doctor.user.firstName} {doctor.user.lastName}
                </p>
                <p className="text-sm text-gray-500">
                  {doctor.speciality?.specialityName ?? "General"}
                </p>
              </div>
            </div>
          ))}
        </div>

        {/* STEP 2: SERVICE */}
        {selectedDoctor && (
          <>
            <h2 className="font-semibold mb-3">
              2. Select Appointment Type
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
              {selectedDoctor.speciality?.appointmentTypes.map((appointmentType) => (
                <button
                  key={appointmentType.typeId}
                  onClick={() => setSelectedAppointment(appointmentType)}
                  className={`border rounded-lg p-3 text-left
                    ${
                      selectedAppointment === appointmentType
                        ? "border-purple-600 bg-purple-50"
                        : "hover:border-gray-400"
                    }`}
                >
                  {appointmentType.name}
                </button>
              ))}
            </div>
          </>
        )}

        {/* STEP 3: DATE & TIME */}
        {selectedAppointment && (
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
              !selectedAppointment ||
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
