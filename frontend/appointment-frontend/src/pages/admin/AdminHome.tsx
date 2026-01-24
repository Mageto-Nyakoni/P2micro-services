import { useState } from "react";
import { TimeSlotCard, DoctorCard, ScheduleDoctorForm } from "@/components/admin";

interface TimeSlot {
  id: number;
  doctorName: string;
  date: string;
  timeslot: string;
}

interface Doctor {
  id: number;
  name: string;
  email: string;
  speciality: string;
  yearsOfExperience: number;
  gender?: string;
  bio?: string;
}

export default function AdminHome() {
  // Sample data for demonstration purposes. Will connect to backend later.
  const admin = {
    name: "Robert Surtain",
    title: "Administrative Lead",
    experience: "4+ Years in Administration",
    education: "MD, Johns Hopkins University School of Medicine.",
    contact: "Robert.Surtain@email.com",
  };

  const [doctorOptions] = useState([
    { id: 1, name: "Dr. John Peters" },
    { id: 2, name: "Dr. Steven Drove" },
    { id: 3, name: "Dr. Charlotte Web" },
  ]);

  const [todaySlots, setTodaySlots] = useState<TimeSlot[]>([
    {
      id: 1,
      doctorName: "Dr. John Peters",
      date: "JAN 3 2025",
      timeslot: "9:00am - 5:00pm",
    },
    {
      id: 2,
      doctorName: "Dr. Steven Drove",
      date: "JAN 3 2025",
      timeslot: "10:00am - 6:00pm",
    },
    {
      id: 3,
      doctorName: "Dr. Charlotte Web",
      date: "JAN 3 2025",
      timeslot: "7:00am - 7:00pm",
    },
  ]);

  const [tomorrowSlots, setTomorrowSlots] = useState<TimeSlot[]>([
    {
      id: 4,
      doctorName: "Dr. John Peters",
      date: "JAN 4 2025",
      timeslot: "9:00am - 5:00pm",
    },
    {
      id: 5,
      doctorName: "Dr. Steven Drove",
      date: "JAN 4 2025",
      timeslot: "10:00am - 6:00pm",
    },
    {
      id: 6,
      doctorName: "Dr. Charlotte Web",
      date: "JAN 4 2025",
      timeslot: "7:00am - 7:00pm",
    },
  ]);

  const [doctors] = useState<Doctor[]>([
    {
      id: 1,
      name: "Dr. John Peters",
      email: "john.peters@email.com",
      speciality: "Cardiology",
      yearsOfExperience: 10,
    },
    {
      id: 2,
      name: "Dr. Steven Drove",
      email: "steven.drove@email.com",
      speciality: "Neurology",
      yearsOfExperience: 8,
    },
    {
      id: 3,
      name: "Dr. Charlotte Web",
      email: "charlotte.web@email.com",
      speciality: "Pediatrics",
      yearsOfExperience: 12,
    },
    {
      id: 4,
      name: "Dr. John Peters",
      email: "john.peters@email.com",
      speciality: "Cardiology",
      yearsOfExperience: 10,
    },
    {
      id: 5,
      name: "Dr. Steven Drove",
      email: "steven.drove@email.com",
      speciality: "Neurology",
      yearsOfExperience: 8,
    },
    {
      id: 6,
      name: "Dr. Charlotte Web",
      email: "charlotte.web@email.com",
      speciality: "Pediatrics",
      yearsOfExperience: 12,
    },
  ]);

  const handleScheduleSubmit = (
    doctorId: number,
    fromDate: string,
    toDate: string
  ) => {
    // todo: Connect to backend API
    console.log("Schedule submitted:", { doctorId, fromDate, toDate });
    // For now, just show an alert
    alert(
      `Schedule created for doctor ${doctorId} from ${fromDate} to ${toDate}`
    );
  };

  const handleDeleteTimeSlot = (id: number, isToday: boolean) => {
    if (isToday) {
      setTodaySlots(todaySlots.filter((slot) => slot.id !== id));
    } else {
      setTomorrowSlots(tomorrowSlots.filter((slot) => slot.id !== id));
    }
    // todo : Connect to backend API to delete
  };

  return (
    <div className="w-full min-h-screen bg-slate-50">
      <div className="max-w-7xl mx-auto px-6 py-12">
        {/* Administrative Lead Profile Section */}
        <div className="rounded-2xl p-8 mb-8 shadow-sm bg-white">
          <div className="flex gap-8 items-start flex-wrap">
            {/* Admin Info */}
            <div className="flex-1 min-w-[300px]">
              <h1 className="m-0 mb-2 font-bold leading-tight text-slate-800 text-3xl">
                {admin.name}
              </h1>
              <p className="m-0 mb-6 font-medium text-slate-500 text-lg">
                {admin.title}
              </p>

              <div className="mb-4">
                <h3 className="m-0 mb-4 font-semibold text-slate-800 text-lg">
                  Organization Details
                </h3>
                <div className="grid grid-cols-[repeat(auto-fit,minmax(200px,1fr))] gap-4">
                  <div>
                    <p className="m-0 mb-1 font-semibold uppercase tracking-wide text-slate-500 text-sm">
                      Experience
                    </p>
                    <p className="m-0 text-slate-800">{admin.experience}</p>
                  </div>

                  <div>
                    <p className="m-0 mb-1 font-semibold uppercase tracking-wide text-slate-500 text-sm">
                      Education
                    </p>
                    <p className="m-0 text-slate-800">{admin.education}</p>
                  </div>

                  <div>
                    <p className="m-0 mb-1 font-semibold uppercase tracking-wide text-slate-500 text-sm">
                      Contact
                    </p>
                    <p className="m-0 text-slate-800">Email: {admin.contact}</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Professional Image */}
            <div className="flex-shrink-0">
              <svg
                width="200"
                height="200"
                viewBox="0 0 200 200"
                className="rounded-2xl"
                style={{
                  background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                }}
                aria-hidden="true"
              >
                <circle cx="100" cy="75" r="35" fill="white" opacity="0.9" />
                <ellipse cx="100" cy="140" rx="50" ry="35" fill="white" opacity="0.9" />
                {/* Simple laptop representation */}
                <rect
                  x="70"
                  y="60"
                  width="60"
                  height="40"
                  rx="2"
                  fill="white"
                  opacity="0.7"
                />
              </svg>
            </div>
          </div>
        </div>

        {/* Schedule Doctor Section */}
        <ScheduleDoctorForm doctors={doctorOptions} onSubmit={handleScheduleSubmit} />

        {/* Scheduled Time Slots Section */}
        <div className="mb-8">
          <h2 className="m-0 mb-6 font-bold text-slate-800 text-2xl">
            Scheduled Time Slots
          </h2>

          {/* Today's Appointments */}
          <div className="mb-6">
            <h3 className="m-0 mb-4 font-semibold text-slate-700 text-xl">
              Today's
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {todaySlots.map((slot) => (
                <TimeSlotCard
                  key={slot.id}
                  {...slot}
                  onDelete={(id) => handleDeleteTimeSlot(id, true)}
                />
              ))}
            </div>
          </div>

          {/* Tomorrow's Appointments */}
          <div>
            <h3 className="m-0 mb-4 font-semibold text-slate-700 text-xl">
              Tomorrow
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {tomorrowSlots.map((slot) => (
                <TimeSlotCard
                  key={slot.id}
                  {...slot}
                  onDelete={(id) => handleDeleteTimeSlot(id, false)}
                />
              ))}
            </div>
          </div>
        </div>

        {/* Doctors List Section */}
        <div>
          <h2 className="m-0 mb-6 font-bold text-slate-800 text-2xl">
            Doctors List
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {doctors.map((doctor) => (
              <DoctorCard key={doctor.id} {...doctor} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
