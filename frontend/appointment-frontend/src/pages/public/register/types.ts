
//Reference Tables
export type Privilege = {
    privilegeId: number;
    roleName: string;
}

export type BloodType = {
    bloodTypeId: number;
    name: string;
};

export type Allergy = {
    allergyId: number;
    name: string;
};

//Create a User on Step 1 of Registration. (POST /auth/register)
export type RegisterUserRequest = {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    privilegeId: number;
};

export type RegisterResponse = {
    userId: number
  email: string
  privilege: number
};



//Create a Patient on Step 2 of Registration (PATCH /patients/{patientID})
export type PatchPatientRequest = {
    address: string;
    age: number;
    allergies: Allergy[]; 
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
    bloodType: string;
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

//UI Forms
export type RegisterUserForm = {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
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