import { useEffect, useRef, useState } from "react";
import RegisterForm1 from "../../components/RegisterForm1";
import RegisterForm2 from "../../components/RegisterForm2";
import { Allergy, BloodType, PatchPatientRequest, PatientDetailsForm, RegisterUserForm } from "../../types/types";
import { register, login } from "../../services/authService";
import { patchPatient } from "@/services/patientServices";
import { setTokenGetter } from "@/services/http";
import { getAllergies } from "@/services/allergyService";
import { getBloodType } from "@/services/bloodTypeService";



export default function RegisterWizard() {
  const [step, setStep] = useState<1 | 2>(1);
  const [userId, setUserId] = useState<number | null>(null);

  const [allergies, setAllergies] = useState<Allergy[]>([]);
  const [bloodTypes, setBloodTypes] = useState<BloodType[]>([]);
  
  const tokenRef = useRef<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    (async () => {
      try {
        const allergyData = await getAllergies();
        const bloodData = await getBloodType();
        
        if (!cancelled) {
          setAllergies(allergyData);
          setBloodTypes(bloodData);
        }
      } catch (err) {
        console.error(err);
        alert("Failed to load allergies or bloodTypes")
      }
    })();

    return () => {
      cancelled = true;
    }
  }, []);

  const [userForm, setUserForm] = useState<RegisterUserForm>({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
  });

  const [patientForm, setPatientForm] = useState<PatientDetailsForm>({
    age: "",
    gender: "other",
    phoneNumber: "",
    dateOfBirth: "",
    address: "",
    bloodType: "",
    allergyIds: [],
  });

  const handleRegistrationStep1 = async () => {
    try {
      const res = await register(userForm.firstName, userForm.lastName, userForm.email, userForm.password);
      
      setUserId(res.userId);
      setStep(2);
    } catch (err) {
      console.error(err);
      alert("registration failed");
    }
  };

  const handleRegistrationStep2 = async () => {
    if (userId == null){
      alert("ERROR: Missing User ID");
      setStep(1);
      return;
    }

    

    try {
      //get token
      const { token } = await login(userForm.email, userForm.password);

      tokenRef.current = token;

      setTokenGetter(() => tokenRef.current);   
      
      console.log(token);
      
      const allergyPayload: string[] = allergies.filter((a) => patientForm.allergyIds.includes(a.allergyId)).map((a) => a.name);

      const payload: PatchPatientRequest = {
        address: patientForm.address,
        age: Number(patientForm.age),
        allergies: allergyPayload,
        bloodType: patientForm.bloodType,
        dateOfBirth: patientForm.dateOfBirth,
        gender: patientForm.gender,
        phoneNumber: patientForm.phoneNumber,
      };
      
      console.log("PATCH payload", payload);

      //patch
      await patchPatient(userId, payload);
      
    } catch (err: any) {
      console.log("Backend response data", err?.response?.data);
      throw err;
    }
  };

  return (
    <>
      {step === 1 ? (
        <RegisterForm1
          value={userForm}
          onChange={setUserForm}
          onNext={handleRegistrationStep1}
        />
      ) : (
        <RegisterForm2
          value={patientForm}
          onChange={setPatientForm} 
          onSubmit={handleRegistrationStep2}
          allergies={allergies}
          bloodTypes={bloodTypes}
        />
      )}
    </>
  );
}