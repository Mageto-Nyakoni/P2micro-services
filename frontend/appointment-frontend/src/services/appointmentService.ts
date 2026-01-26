import { a } from "vitest/dist/chunks/suite.d.BJWk38HB";
import { http } from "./http";

export type AppointmentDto = {
    appointmentId: number;
    doctorName: string;
    appointmentType: string;
    startTime: string; 
    endTime: string;   
    status: string;
};

export async function fetchAppointmentsForPatient(patientId: number): Promise<AppointmentDto[]> {
    const {data} = await http.get<AppointmentDto[]>(`/appointments/patient/${patientId}`);

    return data.map((app) => ({
        appointmentId: app.appointmentId,
        doctorName: app.doctorName,
        appointmentType: app.appointmentType,
        startTime: app.startTime,
        endTime: app.endTime,
        status: app.status,
    }));
}
