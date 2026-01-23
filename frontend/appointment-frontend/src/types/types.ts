
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

export type Speciality = {
    description: string | null;
    specialityName: string | null;
    specialityId: number;
    appointmentTypes: AppointmentType[];
}

export type AppointmentType = {
    typeId: number;
    name: string | null;
    estimatedTime: number | null;
    description: string | null;
}

export type User = {
    userId: number;
    email: string;
    firstName: string;
    lastName: string;
    privilege?: Privilege;
}

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

export type Doctor = {
    doctorId: number;
    bio: string | null;
    experienceYears: number | null;
    gender: string | null;
    speciality: Speciality | null;
    user: User;
}

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