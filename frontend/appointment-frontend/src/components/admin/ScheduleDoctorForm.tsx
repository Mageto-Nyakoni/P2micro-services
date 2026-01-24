import { useState } from "react";

interface Doctor {
  id: number;
  name: string;
}

interface ScheduleDoctorFormProps {
  doctors: Doctor[];
  onSubmit: (doctorId: number, fromDate: string, toDate: string) => void;
}

export default function ScheduleDoctorForm({
  doctors,
  onSubmit,
}: ScheduleDoctorFormProps) {
  const [selectedDoctor, setSelectedDoctor] = useState<number | "">("");
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedDoctor && fromDate && toDate) {
      onSubmit(Number(selectedDoctor), fromDate, toDate);
      // Reset form
      setSelectedDoctor("");
      setFromDate("");
      setToDate("");
    }
  };

  return (
    <div className="rounded-2xl p-8 mb-8 shadow-sm bg-white">
      <h2 className="m-0 mb-6 font-bold text-slate-800 text-2xl">
        Schedule Doctor
      </h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label
            htmlFor="doctor-select"
            className="block mb-2 font-semibold text-slate-700 text-sm"
          >
            Select Doctor
          </label>
          <select
            id="doctor-select"
            value={selectedDoctor}
            //onChange={(e) => setSelectedDoctor(e.target.value)}
            onChange={(e) => setSelectedDoctor(e.target.value === "" ? "" : Number(e.target.value))}
            //^ new Line of code
            className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-slate-800"
            required
          >
            <option value="">Choose a doctor...</option>
            {doctors.map((doctor) => (
              <option key={doctor.id} value={doctor.id}>
                {doctor.name}
              </option>
            ))}
          </select>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label
              htmlFor="from-date"
              className="block mb-2 font-semibold text-slate-700 text-sm"
            >
              From
            </label>
            <input
              id="from-date"
              type="datetime-local"
              value={fromDate}
              onChange={(e) => setFromDate(e.target.value)}
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-slate-800"
              required
            />
          </div>

          <div>
            <label
              htmlFor="to-date"
              className="block mb-2 font-semibold text-slate-700 text-sm"
            >
              To
            </label>
            <input
              id="to-date"
              type="datetime-local"
              value={toDate}
              onChange={(e) => setToDate(e.target.value)}
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-slate-800"
              required
            />
          </div>
        </div>

        <button
          type="submit"
          className="w-full md:w-auto px-6 py-2 bg-indigo-600 text-white font-semibold rounded-lg hover:bg-indigo-700 transition-colors"
        >
          Submit
        </button>
      </form>
    </div>
  );
}
