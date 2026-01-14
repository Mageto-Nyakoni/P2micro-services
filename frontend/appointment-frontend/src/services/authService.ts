import { Role } from "../components/layout/NavBar/types";
import { User } from "../auth/AuthContext";
import { http } from "./http";


type LoginResponse = {
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
}

