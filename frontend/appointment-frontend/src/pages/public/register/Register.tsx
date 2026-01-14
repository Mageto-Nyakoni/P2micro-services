import { useState } from "react";
import RegisterForm1 from "./RegisterForm1";
import RegisterForm2 from "./RegisterForm2";
import { PatientDetailsForm, RegisterUserForm } from "./types";

export default function RegisterWizard() {
  const [step, setStep] = useState<1 | 2>(1);

  const [userForm, setUserForm] = useState<RegisterUserForm>({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
  });

  const [patientForm, setPatientForm] = useState<PatientDetailsForm>({
    age: "",
    gender: "prefer_not_to_say",
    phoneNumber: "",
    DOB: "",
    Address: "",
    bloodTypeId: "",
    allergyIds: [],
  });

  return (
    <>
      {step === 1 ? (
        <RegisterForm1
          value={userForm}
          onChange={setUserForm}
          onNext={() => setStep(2)}
        />
      ) : (
        <RegisterForm2
          value={patientForm}
          onChange={setPatientForm}
        />
      )}
    </>
  );
}