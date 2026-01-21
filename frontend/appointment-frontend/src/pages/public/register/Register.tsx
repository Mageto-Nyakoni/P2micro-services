import { useContext, useRef, useState } from "react";
import RegisterForm1 from "../../../components/RegisterForm1";
import RegisterForm2 from "../../../components/RegisterForm2";
import { Allergy, BloodType, PatchPatientRequest, PatientDetailsForm, RegisterUserForm } from "./types";
import { AuthContext } from "@/auth/AuthContext";
import { registerStep1, login, registerStep2 } from "../../../services/authService/authService";
import { patchPatient } from "@/services/patientServices";


//REPLACE THESE WHEN CONNECTING TO BACKEND
const MOCK_BLOODTYPES: BloodType[] = [
  { bloodTypeId: 1, name: "O+" },
  { bloodTypeId: 2, name: "O-" },
  { bloodTypeId: 3, name: "A+" },
  { bloodTypeId: 4, name: "A-" },
  { bloodTypeId: 5, name: "B+" },
  { bloodTypeId: 6, name: "B-" },
  { bloodTypeId: 7, name: "AB+" },
  { bloodTypeId: 8, name: "AB-" },
];

const MOCK_ALLERGIES: Allergy[] = [
  { allergyId: 1, name: "Peanuts" },
  { allergyId: 2, name: "Tree nuts" },
  { allergyId: 3, name: "Dairy" },
  { allergyId: 4, name: "Egg" },
  { allergyId: 5, name: "Soy" },
  { allergyId: 6, name: "Shellfish" },
  { allergyId: 7, name: "Penicillin" },
  { allergyId: 8, name: "Aspirin" },
  { allergyId: 9, name: "NSAIDs" },
  { allergyId: 10, name: "Amoxicillin" },
  { allergyId: 11, name: "Pollen" },
  { allergyId: 12, name: "Mold" },
  { allergyId: 13, name: "Cats" },
  { allergyId: 14, name: "Dogs" },
  { allergyId: 15, name: "Latex" },
  { allergyId: 16, name: "Nickel" },
  { allergyId: 17, name: "Fragrances" },
  { allergyId: 18, name: "Bee venom" },
  { allergyId: 19, name: "Wasp venom" },
];

export default function RegisterWizard() {
  const auth = useContext(AuthContext);
  if (!auth) throw new Error("AuthContext is null");

  const [step, setStep] = useState<1 | 2>(1);
  const [userId, setUserId] = useState<number | null>(null);

  // Step 1 state
  const [userForm, setUserForm] = useState<RegisterUserForm>({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
  });

  // Step 2 state
  const [patientForm, setPatientForm] = useState<PatientDetailsForm>({
    age: "",
    gender: "other",
    phoneNumber: "",
    dateOfBirth: "",
    address: "",
    bloodType: "",
    allergyIds: [],
  });

  // Step 1: Register user
  const handleRegistrationStep1 = async () => {
    try {
      const res = await registerStep1({
        firstName: userForm.firstName,
        lastName: userForm.lastName,
        email: userForm.email,
        password: userForm.password,
        privilegeId: 1,
      });

      setUserId(res.userId); // save the backend userId
      setStep(2); // move to Step 2
    } catch (err) {
      console.error("Registration failed:", err);
      alert("Registration failed");
    }
  };

  // Step 2: Patch patient details
  const handleRegistrationStep2 = async () => {
    if (userId == null) {
      alert("ERROR: Missing User ID");
      setStep(1);
      return;
    }

    try {
      // Login to get token
      const loginResponse = await login(userForm.email, userForm.password);

      // Save user and token in AuthContext
      auth.login(loginResponse);

      // Build allergy payload
      const allergyPayload: Allergy[] = MOCK_ALLERGIES.filter((a) =>
        patientForm.allergyIds.includes(a.allergyId)
      );

      // Build patient payload
      const payload: PatchPatientRequest = {
        age: Number(patientForm.age),
        gender: patientForm.gender,
        phoneNumber: patientForm.phoneNumber,
        dateOfBirth: patientForm.dateOfBirth,
        address: patientForm.address,
        bloodType: patientForm.bloodType,
        allergies: allergyPayload,
      };

      // Patch patient info
      await patchPatient(userId, payload);

      alert("Registration complete!");
    } catch (err) {
      console.error("Updating Patient failed:", err);
      alert("Updating patient details failed");
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
  step1Data={userForm}  // pass Step 1 info explicitly
  onSubmit={handleRegistrationStep2}
  userId={userId!}  // non-null assertion since we check before
/>
      )}
    </>
  );
}