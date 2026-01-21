import { useNavigate } from "react-router-dom";
import { Allergy, BloodType, PatientDetailsForm } from "./types";
import { register, RegisterPayload } from "@/services/authService"; // <-- import your service

type Props = {
  value: PatientDetailsForm;
  onChange: (next: PatientDetailsForm) => void;
   userForm: { firstName: string; lastName: string; email: string; password: string };
};

// MOCK DATA (replace with backend fetch later)
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
  { allergyId: 2, name: "Shellfish" },
  { allergyId: 3, name: "Dairy" },
  { allergyId: 4, name: "Eggs" },
  { allergyId: 5, name: "Wheat" },
  { allergyId: 6, name: "Soy" },
  { allergyId: 7, name: "Pollen" },
  { allergyId: 8, name: "Latex" },
  { allergyId: 9, name: "Sesame" },
];

export default function RegisterForm2({ value, onChange,userForm }: Props) {
  const navigate = useNavigate();

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    onChange({
      ...value,
      [e.target.name]: e.target.value,
    });
  };

  const toggleAllergy = (id: number) => {
    const nextIds = value.allergyIds.includes(id)
      ? value.allergyIds.filter((x) => x !== id)
      : [...value.allergyIds, id];

    onChange({
      ...value,
      allergyIds: nextIds,
    });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // basic validation
    if (!value.age || !value.DOB || !value.phoneNumber || !value.Address || !value.bloodTypeId) {
      alert("Please fill all fields");
      return;
    }

       const payload: RegisterPayload = {
      ...userForm, // Step 1 data
      ...value,    // Step 2 data
    };

    
    try {
      await register(payload); //  call the new register function
      navigate("/login");
    } catch (err) {
      console.error("Registration failed:", err);
      alert("Registration failed. Please try again.");
    }
  };

   

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-100 to-white px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl p-8">
        {/* Title */}
        <h1 className="text-3xl font-bold text-center text-purple-700">
          Patient Details
        </h1>
        <p className="text-center text-gray-500 mt-2">
          Step 2 of 2: Add your information
        </p>

        <form onSubmit={handleSubmit} className="mt-8 space-y-5">
          <div>
            <label htmlFor="age" className="block text-sm mb-1 text-gray-600">
              Age
            </label>
            <input
              id="age"
              type="number"
              name="age"
              value={value.age}
              onChange={handleChange}
              placeholder="Enter age"
              min={1}
              step={1}
              className="w-full rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-purple-400"
            />
          </div>

          <div>
            <label htmlFor="DOB" className="block text-sm mb-1 text-gray-600">
              DOB
            </label>
            <input
              id="DOB"
              type="date"
              name="DOB"
              value={value.DOB}
              onChange={handleChange}
              className="w-full rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-purple-400"
            />
          </div>

          <div>
            <label htmlFor="gender" className="block text-sm mb-1 text-gray-600">
              Gender
            </label>
            <select
              id="gender"
              name="gender"
              value={value.gender}
              onChange={handleChange}
              className="w-full rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-purple-400"
            >
              <option value="male">Male</option>
              <option value="female">Female</option>
              <option value="other">Other</option>
              <option value="prefer_not_to_say">Prefer not to say</option>
            </select>
          </div>

          <div>
            <label htmlFor="phoneNumber" className="block text-sm mb-1 text-gray-600">
              Phone Number
            </label>
            <input
              id="phoneNumber"
              type="tel"
              name="phoneNumber"
              value={value.phoneNumber}
              onChange={handleChange}
              placeholder="(555) 555-5555"
              className="w-full rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-purple-400"
            />
          </div>

          <div>
            <label htmlFor="Address" className="block text-sm mb-1 text-gray-600">
              Address
            </label>
            <input
              id="Address"
              type="text"
              name="Address"
              value={value.Address}
              onChange={handleChange}
              placeholder="Street, City, State"
              className="w-full rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-purple-400"
            />
          </div>

          <div>
            <label htmlFor="bloodTypeId" className="block text-sm mb-1 text-gray-600">
              Blood Type
            </label>
            <select
              id="bloodTypeId"
              name="bloodTypeId"
              value={value.bloodTypeId}
              onChange={handleChange}
              className="w-full rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-purple-400"
            >
              <option value="">Select blood type</option>
              {MOCK_BLOODTYPES.map((bt) => (
                <option key={bt.bloodTypeId} value={String(bt.bloodTypeId)}>
                  {bt.name}
                </option>
              ))}
            </select>
          </div>

          <div className="rounded-lg border border-gray-200 p-3">
            <p className="text-sm font-semibold text-gray-700 mb-2">Allergies (optional)</p>
            <div className="max-h-40 overflow-auto space-y-2 pr-1">
              {MOCK_ALLERGIES.map((a) => (
                <label key={a.allergyId} className="flex items-center gap-2 text-sm text-gray-700">
                  <input
                    type="checkbox"
                    checked={value.allergyIds.includes(a.allergyId)}
                    onChange={() => toggleAllergy(a.allergyId)}
                  />
                  {a.name}
                </label>
              ))}
            </div>
          </div>

          <div className="flex gap-3 pt-2">
            <button
              type="submit"
              className="flex-1 bg-purple-600 hover:bg-purple-700 transition py-2 rounded-lg font-semibold text-white"
            >
              Register
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
