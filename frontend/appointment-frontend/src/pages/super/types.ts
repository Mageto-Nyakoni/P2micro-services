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
    password?: string;
    privilege: Privilege;
    speciality?: string;
    experience?: number;
    gender?: "male" | "female" | "other";
    bio?: string;
}

export interface CreateUserPayload {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    privilege: Privilege;
    speciality?: string;
    experience?: number;
    gender?: "male" | "female" | "other";
    bio?: string;
}

export interface PatchDoctorResponse {

}