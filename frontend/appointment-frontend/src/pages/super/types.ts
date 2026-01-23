export type Privilege = "Doctor" | "Admin" | "Super";

export type Speciality = {
    id: number,
    name: string
}

export interface User {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    privilege: Privilege;
    speciality?: string;
}

export interface CreateUserPayload {
    firstName: string;
    lastName: string;
    email: string;
    privilege: Privilege;
    speciality?: string;
    experience?: number;
    gender?: "male" | "female" | "other";
    bio?: string;
}

export interface PatchDoctorResponse {

}