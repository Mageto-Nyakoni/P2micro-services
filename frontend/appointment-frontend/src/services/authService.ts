import { Role } from "../components/layout/NavBar/types";
import { User } from "../auth/AuthContext";
import { http } from "./http";
import { RegisterResponse, RegisterUserRequest } from "@/pages/public/register/types";


/*type LoginResponse = {
  userId: number;
  email: string;
  privilege:{
    privilegeId: number;
    roleName: string;
  };
  token: string;
};

export async function login(email: string, password: string): Promise<{user: User; token: string}>{
  const {data} = await http.post<LoginResponse>("/auth/login", {email, password});

  const roleName = data.privilege.roleName as Role;

  return {
    token: data.token,
    user:{
      id: data.userId,
      email: data.email,
      role: roleName
    },
  };
}*/

export const login = async (email: string, password: string) => {
  const response = await http.post("/auth/login", { email, password });
  return response.data;
};
export type RegisterPayload = {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  age: string;
  gender: string;
  phoneNumber: string;
  DOB: string;
  Address: string;
  bloodTypeId: string;
  allergyIds: number[];
};

export async function register(payload: RegisterPayload) {
  const { data } = await http.post("/auth/register", payload);
  return data;
}

export async function register(firstName: string, lastName: string, email: string, password: string): Promise<RegisterResponse>{
  const payload: RegisterUserRequest = {firstName, lastName, email, password, privilegeId: 1}

  const {data} = await http.post<RegisterResponse>("/auth/register", payload);
  return data
}

