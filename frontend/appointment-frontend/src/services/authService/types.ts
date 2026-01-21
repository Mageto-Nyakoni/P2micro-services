// authService/types.ts

export interface RegisterUserPayload {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  privilegeId: number;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
}
export interface RegisterUserRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  privilegeId: number; // usually 1 for normal user
}

export interface PatientPayload {
  age: string;
  gender: string;
  phoneNumber: string;
  DOB: string;
  Address: string;
  bloodTypeId: string;
  allergyIds: number[];
}