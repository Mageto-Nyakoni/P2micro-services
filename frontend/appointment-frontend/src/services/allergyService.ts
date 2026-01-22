import { Allergy } from "@/types/types";
import { http } from "./http";

export async function getAllergies(): Promise<Allergy[]> {
    const {data} = await http.get<Allergy[]>("/allergies");
    return data;
}