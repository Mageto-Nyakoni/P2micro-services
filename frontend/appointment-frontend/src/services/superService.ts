import { http } from "./http";
import { Speciality, User, CreateUserPayload, PatchDoctorResponse} from "@/pages/super/types";

export async function getSpecialities(): Promise<Speciality[]> {
    const {data} = await http.get<Speciality[]>("/specialities");
    return data;
}

export async function getUsersForTable(): Promise<User[]> {
    const {data} = await http.get<User[]>("/users/table");
    return data;
}

export async function patchDoctor(userID: number, payload: CreateUserPayload): Promise<PatchDoctorResponse> {
    const {data} = await http.patch<PatchDoctorResponse>(`/doctors/${userID}`, payload);
    return data;
}

export async function patchUser(userID: number, payload: CreateUserPayload): Promise<User> {
    const {data} = await http.patch<User>(`/users/${userID}`);
    return data;
}

export async function deleteUser(userID: number): Promise<User> {
    const {data} = await http.delete<User>(`/users/${userID}`);
    return data;
}