import { PatchPatientRequest, PatchPatientResponse } from "@/pages/public/register/types";
import { http } from "./http";

export async function patchPatient(userID: number, payload: PatchPatientRequest) : Promise<PatchPatientResponse> {
    const {data} = await http.patch<PatchPatientResponse>(`/patients/${userID}`, payload);
    return data;
}