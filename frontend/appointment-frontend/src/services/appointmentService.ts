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

    return data;
}
