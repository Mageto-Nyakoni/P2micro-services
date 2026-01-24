import { Privilege, User } from "./userTypes";

export type Patient = {
    patientId: number;
    address: string | null;
    age: number | null;
    bloodType: BloodType | null;
    dateOfBirth: string | null;
    gender: string | null;
    phoneNumber: string | null;
    allergies: Allergy[];
    user: User;
}

export type BloodType = {
    bloodTypeId: number;
    name: string;
};

export type Allergy = {
    allergyId: number;
    name: string;
};

export type PatchPatientRequest = {
    address: string;
    age: number;
    allergies: string[]; 
    bloodType: string;
    dateOfBirth: string;
    gender: "male" | "female" | "other";
    phoneNumber: string;
};

export type PatchPatientResponse = {
    patientId: number;
    address: string;
    age: number;
    allergies: Allergy[];
    bloodType: BloodType;
    dateOfBirth: string;
    gender: string;
    phoneNumber: string;
    user: {
        userId: number;
        email: string;
        firstName: string;
        lastName: string;
        privilege: Privilege;
        password?: string;
    };
};

export type PatientDetailsForm = {
    address: string
    age: string;
    gender: "male" | "female" | "other";
    phoneNumber: string;
    dateOfBirth: string;
    bloodType: string;      
    allergyIds: number[];   //checkbox
};

export type PatientEditForm = {
  patientId: number;
  address: string;
  age: string;               // keep as string for inputs
  dateOfBirth: string;
  gender: "male" | "female" | "other";
  phoneNumber: string;
  bloodType: string;
  allergyIds: number[];
};