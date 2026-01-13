// src/components/availability/CalendarAvail.tsx

import { useState } from "react";
import Calendar from "react-calendar";
import type { CalendarProps } from "react-calendar";
import "react-calendar/dist/Calendar.css";

import { DoctorAvailability } from "./types";

/* ================= PROPS ================= */

type CalendarAvailProps = {
  availabilityByDate: Record<string, DoctorAvailability[]>;
  onBook: (doctor: DoctorAvailability) => void;
};

/* ================= COMPONENT ================= */

const CalendarAvail: React.FC<CalendarAvailProps> = ({
  availabilityByDate,
  onBook,
}) => {
  const [selectedDate, setSelectedDate] =
    useState<CalendarProps["value"]>(null);

  const [availableDoctors, setAvailableDoctors] =
    useState<DoctorAvailability[]>([]);

  const handleDateChange: CalendarProps["onChange"] = (value) => {
    setSelectedDate(value);

    if (value instanceof Date) {
      const dateKey = value.toISOString().split("T")[0];
      setAvailableDoctors(availabilityByDate[dateKey] || []);
    } else {
      setAvailableDoctors([]);
    }
  };

  return (
    <div className="flex flex-col items-center gap-8">
      {/* CALENDAR */}
      <div className="bg-white p-8 rounded-2xl shadow-xl">
        <h2 className="text-2xl font-bold text-center mb-4">
          Select Appointment Date
        </h2>

        <Calendar
          onChange={handleDateChange}
          value={selectedDate}
          className="rounded-lg"
        />
      </div>

      {/* AVAILABLE DOCTORS */}
      {availableDoctors.length > 0 && (
        <div className="bg-white p-8 rounded-2xl shadow-xl w-full max-w-xl">
          <h3 className="text-xl font-bold mb-4 text-center">
            Available Doctors
          </h3>

          {availableDoctors.map((doctor) => (
            <div key={doctor.id} className="mb-6 border-b pb-4">
              <h4 className="font-semibold text-lg">
                {doctor.name}
              </h4>
              <p className="text-gray-600">
                {doctor.specialization}
              </p>

              <div className="flex gap-3 flex-wrap mt-3">
                {doctor.slots.map((slot, index) => (
                  <span
                    key={index}
                    className={`px-3 py-1 rounded-lg text-sm ${
                      slot.available
                        ? "bg-green-100 text-green-700"
                        : "bg-gray-200 text-gray-500 line-through"
                    }`}
                  >
                    {slot.time}
                  </span>
                ))}
              </div>

              <button
                onClick={() => onBook(doctor)}
                className="mt-4 bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition"
              >
                Book Appointment
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default CalendarAvail;
