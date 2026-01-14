
//Create a User on Step 1 of Registration.
export type RegisterUserRequest = {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    privilegeId: number;
};

//created after step 1 of registration is completed
export type RegisterResponse = {
    userId: number;
    token?: string;
};

export type BloodType = {
    bloodTypeId: number;
    name: string;
};

export type Allergy = {
    allergyId: number;
    name: string;
};

//Create a Patient on Step 2 of Registration

export type CreatePatientRequest = {
    userId: number;

    age: number;
    gender: "male" | "female" | "other" | "prefer_not_to_say";
    phoneNumber: string;
    DOB: string; 
    Address: string;

    bloodTypeId: number;
    allergyIds: number[]; 
};

export type CreatePatientResponse = {
    patientId: number;
};

//UI Forms

export type RegisterUserForm = {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
};

export type PatientDetailsForm = {
    age: string;
    gender: CreatePatientRequest['gender'];
    phoneNumber: string;
    DOB: string;
    Address: string;

    bloodTypeId: string;    //selected option value -> number   
    allergyIds: number[];   //checkbox
};