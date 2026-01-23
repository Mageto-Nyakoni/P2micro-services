import { Doctor } from "@/types/types";
import { http } from "./http";

export async function getMyDoctor(): Promise<Doctor>{
    const {data} = await http.get("/doctors/me");
    return data;
}