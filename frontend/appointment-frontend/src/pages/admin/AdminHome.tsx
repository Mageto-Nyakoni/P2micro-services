import { useCallback, useEffect, useMemo, useState } from "react";

import { TimeSlotCard, ScheduleDoctorForm } from "@/components/admin";

import { getAllDoctors } from "@/services/doctorServices";

import { useAuth } from "@/auth/useAuth";

import {
  AdminAppointment,
  AdminTimeSlot,
  AvailabilityWindow,
  createAvailabilityWindow,
  deleteAvailabilityWindow,
  denyAppointment,
  fetchAdminAppointments,
  fetchAdminTimeSlots,
  fetchAvailabilityWindows,
} from "@/services/adminService";
import { Doctor as DoctorModel } from "@/types/doctorTypes";
import { formatTime } from "@/utils/validators";

interface DoctorOption {
  id: number;
  name: string;
}

export default function AdminHome() {
  const { user } = useAuth();
  const admin = {
    name:
      user?.firstName || user?.lastName
        ? `${user?.firstName ?? ""} ${user?.lastName ?? ""}`.trim()
        : "Admin",

    contact: user?.email ?? "",
  };

  const [doctors, setDoctors] = useState<DoctorModel[]>([]);

  const [availabilityWindows, setAvailabilityWindows] = useState<
    AvailabilityWindow[]
  >([]);

  const [slots, setSlots] = useState<AdminTimeSlot[]>([]);
  const [appointments, setAppointments] = useState<AdminAppointment[]>([]);
  const [selectedDoctorId, setSelectedDoctorId] = useState<number | null>(null);
  const [windowDoctorFilterId, setWindowDoctorFilterId] = useState<
    number | "ALL"
  >("ALL");
  const [slotDoctorFilterId, setSlotDoctorFilterId] = useState<number | "ALL">(
    "ALL",
  );
  const [appointmentDoctorFilterId, setAppointmentDoctorFilterId] = useState<
    number | "ALL"
  >("ALL");
  const [windowPage, setWindowPage] = useState(1);
  const [windowPageSize, setWindowPageSize] = useState(9);
  const [slotPage, setSlotPage] = useState(1);
  const [slotPageSize, setSlotPageSize] = useState(9);
  const [appointmentPage, setAppointmentPage] = useState(1);
  const [appointmentPageSize, setAppointmentPageSize] = useState(10);

  const doctorOptions = useMemo<DoctorOption[]>(
    () =>
      doctors.map((doctor) => ({
        id: doctor.doctorId,

        name: doctor.speciality?.specialityName
          ? `${doctor.user.firstName} ${doctor.user.lastName} (${doctor.speciality.specialityName})`
          : `${doctor.user.firstName} ${doctor.user.lastName}`,
      })),

    [doctors],
  );

  const doctorById = useMemo(() => {
    const map = new Map<number, DoctorModel>();
    doctors.forEach((doctor) => {
      map.set(doctor.doctorId, doctor);
    });
    return map;
  }, [doctors]);

  const formatDoctorDisplay = (
    doctorId?: number | null,
    fallbackName?: string | null,
  ) => {
    const doctor = doctorId ? doctorById.get(doctorId) : undefined;
    const baseName = doctor
      ? `${doctor.user.firstName} ${doctor.user.lastName}`
      : (fallbackName ?? "Unknown");
    const speciality = doctor?.speciality?.specialityName;
    return speciality ? `${baseName} (${speciality})` : baseName;
  };

  const loadAllAvailabilityWindows = useCallback(
    async (sourceDoctors: DoctorModel[] = doctors) => {
      if (sourceDoctors.length === 0) {
        setAvailabilityWindows([]);
        return [];
      }
      const windowsByDoctor = await Promise.all(
        sourceDoctors.map((doctor) => fetchAvailabilityWindows(doctor.doctorId)),
      );
      const merged = windowsByDoctor.flat();
      setAvailabilityWindows(merged);
      return merged;
    },
    [doctors],
  );

  useEffect(() => {
    const controller = new AbortController();
    getAllDoctors(controller.signal)
      .then(setDoctors)
      .catch((error) => {
        console.error("AdminHome: failed to load doctors", error);
      });
    fetchAdminAppointments("ALL")
      .then(setAppointments)
      .catch((error) => {
        console.error("AdminHome: failed to load appointments", error);
      });
    fetchAdminTimeSlots()
      .then(setSlots)
      .catch((error) => {
        console.error("AdminHome: failed to load time slots", error);
      });

    return () => controller.abort();
  }, []);

  useEffect(() => {
    if (selectedDoctorId !== null || doctors.length === 0) return;
    const firstDoctorId = doctors[0].doctorId;
    setSelectedDoctorId(firstDoctorId);
    setWindowDoctorFilterId("ALL");
    loadAllAvailabilityWindows(doctors).catch((error) => {
      console.error("AdminHome: failed to load availability windows", error);
    });
  }, [doctors, selectedDoctorId, loadAllAvailabilityWindows]);

  useEffect(() => {
    setSlotPage(1);
  }, [slots, slotDoctorFilterId]);

  useEffect(() => {
    setWindowPage(1);
  }, [availabilityWindows, windowDoctorFilterId]);

  useEffect(() => {
    setAppointmentPage(1);
  }, [appointments, appointmentDoctorFilterId]);

  const filteredAvailabilityWindows = useMemo(
    () =>
      windowDoctorFilterId === "ALL"
        ? availabilityWindows
        : availabilityWindows.filter(
            (window) => window.doctorId === windowDoctorFilterId,
          ),
    [availabilityWindows, windowDoctorFilterId],
  );

  const windowTotalPages = Math.max(
    1,
    Math.ceil(filteredAvailabilityWindows.length / windowPageSize),
  );

  const filteredSlots = useMemo(
    () =>
      slotDoctorFilterId === "ALL"
        ? slots
        : slots.filter((slot) => slot.doctorId === slotDoctorFilterId),
    [slots, slotDoctorFilterId],
  );

  const slotTotalPages = Math.max(
    1,
    Math.ceil(filteredSlots.length / slotPageSize),
  );

  const filteredAppointments = useMemo(
    () =>
      appointmentDoctorFilterId === "ALL"
        ? appointments
        : appointments.filter((appt) => appt.doctorId === appointmentDoctorFilterId),
    [appointments, appointmentDoctorFilterId],
  );

  const appointmentTotalPages = Math.max(
    1,
    Math.ceil(filteredAppointments.length / appointmentPageSize),
  );

  const pagedSlots = useMemo(
    () =>
      filteredSlots.slice(
        (slotPage - 1) * slotPageSize,
        slotPage * slotPageSize,
      ),
    [filteredSlots, slotPage, slotPageSize],
  );

  const pagedAppointments = useMemo(
    () =>
      filteredAppointments.slice(
        (appointmentPage - 1) * appointmentPageSize,
        appointmentPage * appointmentPageSize,
      ),
    [filteredAppointments, appointmentPage, appointmentPageSize],
  );

  const pagedAvailabilityWindows = useMemo(
    () =>
      filteredAvailabilityWindows.slice(
        (windowPage - 1) * windowPageSize,
        windowPage * windowPageSize,
      ),
    [filteredAvailabilityWindows, windowPage, windowPageSize],
  );

  const handleScheduleSubmit = (
    doctorId: number,
    date: string,
    startTime: string,
    endTime: string,
  ) => {
    createAvailabilityWindow(doctorId, { date, startTime, endTime })
      .then(() =>
        Promise.all([loadAllAvailabilityWindows(), fetchAdminTimeSlots()]),
      )

      .then(([, timeSlots]) => {
        setSlots(timeSlots);
        setSelectedDoctorId(doctorId);
        setWindowDoctorFilterId(doctorId);
      })
      .catch((error) => {
        console.error("AdminHome: failed to create availability window", error);
      });
  };

  const handleDeleteWindow = (windowId: number) => {
    deleteAvailabilityWindow(windowId)
      .then(() => Promise.all([loadAllAvailabilityWindows(), fetchAdminTimeSlots()]))
      .then(([, timeSlots]) => {
        setSlots(timeSlots);
      })

      .catch((error) => {
        console.error("AdminHome: failed to delete availability window", error);
      });
  };

  const handleDoctorChange = (doctorId: number) => {
    setSelectedDoctorId(doctorId);
  };

  const handleDenyAppointment = (appointmentId: number) => {
    denyAppointment(appointmentId)
      .then(() => fetchAdminAppointments("ALL"))
      .then(setAppointments)
      .catch((error) => {
        console.error("AdminHome: failed to deny appointment", error);
      });
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
                {admin.contact}
              </p>
            </div>

            {/* Professional Image */}
            <div className="flex-shrink-0">
              <svg
                width="200"
                height="200"
                viewBox="0 0 200 200"
                className="rounded-2xl"
                style={{
                  background:
                    "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                }}
                aria-hidden="true"
              >
                <circle cx="100" cy="75" r="35" fill="white" opacity="0.9" />

                <ellipse
                  cx="100"
                  cy="140"
                  rx="50"
                  ry="35"
                  fill="white"
                  opacity="0.9"
                />

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

        <ScheduleDoctorForm
          doctors={doctorOptions}
          onSubmit={handleScheduleSubmit}
          onDoctorChange={handleDoctorChange}
        />

        {/* Scheduled Time Slots Section */}

        <div className="mb-8">
          <h2 className="m-0 mb-6 font-bold text-slate-800 text-2xl">
            Availability Windows
          </h2>

          <div className="mb-4 flex flex-wrap items-center gap-3 text-sm text-slate-600">
            <label className="flex items-center gap-2">
              <span>Doctor:</span>
              <select
                value={windowDoctorFilterId}
                onChange={(event) => {
                  const nextValue = event.target.value;
                  setWindowDoctorFilterId(
                    nextValue === "ALL" ? "ALL" : Number(nextValue),
                  );
                }}
                className="rounded-md border border-slate-200 bg-white px-2 py-1 text-slate-700"
              >
                <option value="ALL">All</option>
                {doctorOptions.map((doctor) => (
                  <option key={doctor.id} value={doctor.id}>
                    {doctor.name}
                  </option>
                ))}
              </select>
            </label>
          </div>

          {filteredAvailabilityWindows.length === 0 ? (
            <p className="text-slate-500">No availability windows found.</p>
          ) : (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {pagedAvailabilityWindows.map((window) => (
                  <TimeSlotCard
                    key={window.windowId}
                    id={window.windowId}
                    doctorName={formatDoctorDisplay(
                      window.doctorId,
                      window.doctorName,
                    )}
                    date={window.date}
                    timeslot={`${formatTime(window.startTime)} - ${formatTime(window.endTime)}`}
                    onDelete={handleDeleteWindow}
                  />
                ))}
              </div>

              <div className="mt-4 flex flex-wrap items-center justify-between gap-3 text-sm text-slate-600">
                <div className="flex items-center gap-2">
                  <button
                    onClick={() =>
                      setWindowPage((page) => Math.max(1, page - 1))
                    }
                    disabled={windowPage === 1}
                    className="px-3 py-1 rounded-md border border-slate-200 bg-white text-slate-700 disabled:opacity-60"
                  >
                    Prev
                  </button>

                  <button
                    onClick={() =>
                      setWindowPage((page) =>
                        Math.min(windowTotalPages, page + 1),
                      )
                    }
                    disabled={windowPage === windowTotalPages}
                    className="px-3 py-1 rounded-md border border-slate-200 bg-white text-slate-700 disabled:opacity-60"
                  >
                    Next
                  </button>

                  <span>
                    Page {windowPage} of {windowTotalPages}
                  </span>
                </div>

                <label className="flex items-center gap-2">
                  <span>Windows:</span>

                  <select
                    value={windowPageSize}
                    onChange={(event) => {
                      setWindowPageSize(Number(event.target.value));

                      setWindowPage(1);
                    }}
                    className="rounded-md border border-slate-200 bg-white px-2 py-1 text-slate-700"
                  >
                    {[9, 18, 27].map((size) => (
                      <option key={size} value={size}>
                        {size}
                      </option>
                    ))}
                  </select>
                </label>
              </div>
            </>
          )}
        </div>

        {/* Time Slots */}

        <div className="mb-8">
          <h2 className="m-0 mb-6 font-bold text-slate-800 text-2xl">
            Time Slots
          </h2>

          <div className="mb-4 flex flex-wrap items-center gap-3 text-sm text-slate-600">
            <label className="flex items-center gap-2">
              <span>Doctor:</span>
              <select
                value={slotDoctorFilterId}
                onChange={(event) => {
                  const nextValue = event.target.value;
                  setSlotDoctorFilterId(
                    nextValue === "ALL" ? "ALL" : Number(nextValue),
                  );
                }}
                className="rounded-md border border-slate-200 bg-white px-2 py-1 text-slate-700"
              >
                <option value="ALL">All</option>
                {doctorOptions.map((doctor) => (
                  <option key={doctor.id} value={doctor.id}>
                    {doctor.name}
                  </option>
                ))}
              </select>
            </label>
          </div>

          {filteredSlots.length === 0 ? (
            <p className="text-slate-500">No time slots found.</p>
          ) : (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {pagedSlots.map((slot) => (
                  <TimeSlotCard
                    key={slot.slotId}
                    id={slot.slotId}
                    doctorName={formatDoctorDisplay(
                      slot.doctorId,
                      slot.doctorName,
                    )}
                    date={slot.dateAvailable}
                    timeslot={`${formatTime(slot.startTime)} - ${formatTime(slot.endTime)}`}
                    status={slot.status}
                  />
                ))}
              </div>

              <div className="mt-4 flex flex-wrap items-center justify-between gap-3 text-sm text-slate-600">
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => setSlotPage((page) => Math.max(1, page - 1))}
                    disabled={slotPage === 1}
                    className="px-3 py-1 rounded-md border border-slate-200 bg-white text-slate-700 disabled:opacity-60"
                  >
                    Prev
                  </button>

                  <button
                    onClick={() =>
                      setSlotPage((page) => Math.min(slotTotalPages, page + 1))
                    }
                    disabled={slotPage === slotTotalPages}
                    className="px-3 py-1 rounded-md border border-slate-200 bg-white text-slate-700 disabled:opacity-60"
                  >
                    Next
                  </button>

                  <span>
                    Page {slotPage} of {slotTotalPages}
                  </span>
                </div>

                <label className="flex items-center gap-2">
                  <span>Slots:</span>

                  <select
                    value={slotPageSize}
                    onChange={(event) => {
                      setSlotPageSize(Number(event.target.value));

                      setSlotPage(1);
                    }}
                    className="rounded-md border border-slate-200 bg-white px-2 py-1 text-slate-700"
                  >
                    {[9, 18, 27].map((size) => (
                      <option key={size} value={size}>
                        {size}
                      </option>
                    ))}
                  </select>
                </label>
              </div>
            </>
          )}
        </div>

        {/* Appointments */}

        <div className="mb-8">
          <h2 className="m-0 mb-6 font-bold text-slate-800 text-2xl">
            Appointments
          </h2>

          <div className="mb-4 flex flex-wrap items-center gap-3 text-sm text-slate-600">
            <label className="flex items-center gap-2">
              <span>Doctor:</span>
              <select
                value={appointmentDoctorFilterId}
                onChange={(event) => {
                  const nextValue = event.target.value;
                  setAppointmentDoctorFilterId(
                    nextValue === "ALL" ? "ALL" : Number(nextValue),
                  );
                }}
                className="rounded-md border border-slate-200 bg-white px-2 py-1 text-slate-700"
              >
                <option value="ALL">All</option>
                {doctorOptions.map((doctor) => (
                  <option key={doctor.id} value={doctor.id}>
                    {doctor.name}
                  </option>
                ))}
              </select>
            </label>
          </div>

          {filteredAppointments.length === 0 ? (
            <p className="text-slate-500">No appointments found.</p>
          ) : (
            <>
              <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
                <table className="w-full text-sm">
                  <thead className="bg-slate-100 text-slate-600">
                    <tr>
                      <th className="text-left px-4 py-3">Patient</th>

                      <th className="text-left px-4 py-3">Doctor</th>

                      <th className="text-left px-4 py-3">Date</th>

                      <th className="text-left px-4 py-3">Time</th>

                      <th className="text-left px-4 py-3">Type</th>

                      <th className="text-left px-4 py-3">Status</th>

                      <th className="text-left px-4 py-3">Action</th>
                    </tr>
                  </thead>

                  <tbody>
                    {pagedAppointments.map((appt) => (
                      <tr
                        key={appt.appointmentId}
                        className="border-t border-slate-100"
                      >
                        <td className="px-4 py-3">{appt.patientName ?? "—"}</td>

                        <td className="px-4 py-3">
                          {formatDoctorDisplay(appt.doctorId, appt.doctorName)}
                        </td>

                        <td className="px-4 py-3">
                          {appt.dateAvailable ??
                            appt.scheduledDateTime?.split("T")[0] ??
                            "—"}
                        </td>

                        <td className="px-4 py-3">
                          {appt.startTime && appt.endTime
                            ? `${formatTime(appt.startTime)} - ${formatTime(appt.endTime)}`
                            : appt.scheduledDateTime
                              ? (appt.scheduledDateTime
                                  .split("T")[1]
                                  ?.slice(0, 5) ?? "—")
                              : "—"}
                        </td>

                        <td className="px-4 py-3">
                          {typeof appt.appointmentType === "string"
                            ? appt.appointmentType
                            : (appt.appointmentType?.name ?? "—")}
                        </td>

                        <td className="px-4 py-3">{appt.status}</td>

                        <td className="px-4 py-3">
                          <button
                            onClick={() =>
                              handleDenyAppointment(appt.appointmentId)
                            }
                            className="px-3 py-1 rounded-md text-white bg-rose-600 hover:bg-rose-700 disabled:opacity-60"
                            disabled={
                              appt.status === "DENIED" ||
                              appt.status === "CANCELLED"
                            }
                          >
                            Deny
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              <div className="mt-4 flex flex-wrap items-center justify-between gap-3 text-sm text-slate-600">
                <div className="flex items-center gap-2">
                  <button
                    onClick={() =>
                      setAppointmentPage((page) => Math.max(1, page - 1))
                    }
                    disabled={appointmentPage === 1}
                    className="px-3 py-1 rounded-md border border-slate-200 bg-white text-slate-700 disabled:opacity-60"
                  >
                    Prev
                  </button>

                  <button
                    onClick={() =>
                      setAppointmentPage((page) =>
                        Math.min(appointmentTotalPages, page + 1),
                      )
                    }
                    disabled={appointmentPage === appointmentTotalPages}
                    className="px-3 py-1 rounded-md border border-slate-200 bg-white text-slate-700 disabled:opacity-60"
                  >
                    Next
                  </button>

                  <span>
                    Page {appointmentPage} of {appointmentTotalPages}
                  </span>
                </div>

                <label className="flex items-center gap-2">
                  <span>Rows:</span>

                  <select
                    value={appointmentPageSize}
                    onChange={(event) => {
                      setAppointmentPageSize(Number(event.target.value));

                      setAppointmentPage(1);
                    }}
                    className="rounded-md border border-slate-200 bg-white px-2 py-1 text-slate-700"
                  >
                    {[10, 20, 50].map((size) => (
                      <option key={size} value={size}>
                        {size}
                      </option>
                    ))}
                  </select>
                </label>
              </div>
            </>
          )}
        </div>

        {/* Staff List lives in AdminStaffList only */}
      </div>
    </div>
  );
}
