import { useLocation, useNavigate } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import { formatPhoneNumber, getAgeFromDOB } from "@/components/RegisterForm2";
import { Allergy, BloodType, PatchPatientRequest, Patient, PatientEditForm } from "@/types/types";
import { getMyPatient, patchPatient } from "@/services/patientServices";
import { getAllergies } from "@/services/allergyService";
import { getBloodType } from "@/services/bloodTypeService";

const emptyForm: PatientEditForm = {
  patientId: 0,
  address: "",
  age: "",
  dateOfBirth: "",
  gender: "other",
  phoneNumber: "",
  bloodType: "",
  allergyIds: [],
};

type EditProfileLocationState = {
  patient?: Patient;
};

function toGender(value: string | null | undefined): "male" | "female" | "other" {
  switch ((value ?? "").toLowerCase()) {
    case "male":
      return "male";
    case "female":
      return "female";
    case "other":
      return "other";
    default:
      return "other";
  }
}

function EditPatientProfile() {
    const navigate = useNavigate();

	const location = useLocation();
	const patientFromState = (location.state as EditProfileLocationState | null)?.patient ?? null;

	const [patient, setPatient] = useState<Patient | null>(patientFromState);
	const [error, setError] = useState<string | null>(null);

	const [allergies, setAllergies] = useState<Allergy[]>([]);
	const [bloodTypes, setBloodTypes] = useState<BloodType[]>([]);

	const [form, setForm] = useState<PatientEditForm>(emptyForm);

	const [saving, setSaving] = useState(false);

	const didFetch = useRef(false);

	useEffect(() => {
		if (didFetch.current) return;
		didFetch.current = true;

		let cancelled = false;
	
		(async () => {
		  	try{
				const allergyData = await getAllergies();
				const bloodData = await getBloodType();

				if (cancelled) return;


				setAllergies(allergyData);
				setBloodTypes(bloodData);
				
		  	} catch (err) {
				console.error(err);
				if (!cancelled) setError("Failed to load Profile");
		  	}

		})();

		return () => {
      		cancelled = true;
    	};

	}, []);

	useEffect(() => {
		if (patient) return;

		(async () => {
			try {
				const p = await getMyPatient();
				setPatient(p);
			} catch (e) {
				console.error(e);
				setError("Failed to load patient");
			}
		})();
  	}, [patient]);

    useEffect(() => {
    	if (!patient) return;

		setForm({
			patientId: patient.patientId ?? 0,
			address: patient.address ?? "",
			age: patient.age != null ? String(patient.age) : "",
			dateOfBirth: patient.dateOfBirth ?? "",
			gender: toGender(patient.gender),
			phoneNumber: patient.phoneNumber ?? "",
			bloodType: patient.bloodType?.name ?? "",
			allergyIds: patient.allergies?.map((a) => a.allergyId) ?? [],
		});
  	}, [patient]);

	if (error) return <p className="text-red-600">{error}</p>;
	if (!patient) return <p>Loading information...</p>;
    

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value: inputValue } = e.target;

        if (name === "phoneNumber") {
            setForm({
                ...form,
                phoneNumber: formatPhoneNumber(inputValue),
            });
            return;
        }
        
        setForm({
            ...form,
            [name]: inputValue,
        });
    };


	const toggleAllergy = (id: number) => {
        const nextIds = form.allergyIds.includes(id) ? form.allergyIds.filter((x) => x !== id) : [...form.allergyIds, id];
    
        setForm({
            ...form,
            allergyIds: nextIds,
        });
    };


	const onSubmit = async () => {
		const allergyPayload: string[] = allergies.filter((a) => form.allergyIds.includes(a.allergyId)).map((a) => a.name);
		
		const payload: PatchPatientRequest = {
			address: form.address,
			age: Number(form.age),
			allergies: allergyPayload,
			bloodType: form.bloodType,
			dateOfBirth: form.dateOfBirth,
			gender: form.gender,
			phoneNumber: form.phoneNumber,
		};
		
		console.log("PATCH payload", payload);

		await patchPatient(patient.user.userId, payload);
	};


    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
      
        if (!form.age || !form.dateOfBirth || !form.phoneNumber || !form.address || !form.bloodType) {
            alert("Please fill all fields");
            return;
        }

		const enteredAge = Number(form.age);
		const computedAge = getAgeFromDOB(form.dateOfBirth);

		if (computedAge != enteredAge) {
			alert(`Age and Birthday do not match. Based on DOB, age should be ${computedAge}`);
			return;
		}

		try {
			setSaving(true);
            await onSubmit();
            alert("Profile updated successfully!");
            navigate("/patient/profile");
        } catch (err){
			console.error(err);
			alert("Failed to update profile");
        } finally {
			setSaving(false);
		}
    };


    return (
        <div className="p-6 max-w-2xl mx-auto">

            {/* Back link */}
            <button
                onClick={() => navigate("/patient/profile")}
                className="mb-4 text-indigo-600 hover:underline"
            >
                ← Back to Profile
            </button>
            <h2 className="text-2xl font-bold mb-6">Edit Patient Profile</h2>

			<form onSubmit={handleSubmit} className="mt-8 space-y-5">
                    <div className="mb-2">
                        <label className="block text-sm mb-1 text-gray-600">
                            Age
                        </label>
                        <input
                            type="number"
                            name="age"
                            value={form.age}
                            onChange={handleChange}
                            placeholder="Enter age"
                            min={1}
                            step={1}
                            className="w-full rounded-lg border border-gray-300 px-4 py-2 
                                        focus:outline-none focus:ring-2 focus:ring-purple-400"
                        />
                    </div>

                    <div className="mb-2">
                        <label className="block text-sm mb-1 text-gray-600">
                            Date of Birth
                        </label>
                        <input
                            type="date"
                            name="dateOfBirth"
                            value={form.dateOfBirth}
                            onChange={handleChange}
                            className="w-full rounded-lg border border-gray-300 px-4 py-2 
                                        focus:outline-none focus:ring-2 focus:ring-purple-400"
                        />
                    </div>

                    <div className="mb-2">
                        <label className="block text-sm mb-1 text-gray-600">
                            Gender
                        </label>
                        <select
                            name="gender"
                            value={form.gender}
                            onChange={handleChange}
                            className="w-full rounded-lg border border-gray-300 px-4 py-2 
                                        focus:outline-none focus:ring-2 focus:ring-purple-400"
                        >
                            <option value="male">Male</option>
                            <option value="female">Female</option>
                            <option value="other">Other</option>
                        </select>
                    </div>

                    <div className="mb-2">
                        <label className="block text-sm mb-1 text-gray-600">
                            Phone Number
                        </label>
                        <input
                            type="tel"
                            name="phoneNumber"
                            value={form.phoneNumber}
                            onChange={handleChange}
                            placeholder="(555) 555-5555"
                            className="w-full rounded-lg border border-gray-300 px-4 py-2 
                                        focus:outline-none focus:ring-2 focus:ring-purple-400"
                        />
                    </div>

                    <div className="mb-2">
                        <label className="block text-sm mb-1 text-gray-600">
                            Address
                        </label>
                        <input
                            type="text"
                            name="address"
                            value={form.address}
                            onChange={handleChange}
                            placeholder="Street, City, State"
                            className="w-full rounded-lg border border-gray-300 px-4 py-2 
                                        focus:outline-none focus:ring-2 focus:ring-purple-400"
                        />
                    </div>

                    <div className="mb-2">
                        <label className="block text-sm mb-1 text-gray-600">
                            Blood Type
                        </label>
                        <select
                            name="bloodType"
                            value={form.bloodType}
                            onChange={handleChange}
                            className="w-full rounded-lg border border-gray-300 px-4 py-2 
                                        focus:outline-none focus:ring-2 focus:ring-purple-400"
                        >
                            <option value="">Select blood type</option>
                            {(bloodTypes ?? []).map((bt) => (
                                <option key={bt.bloodTypeId} value={String(bt.name)}>
                                {bt.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="mb-2">
                        <p className="block text-sm mb-1 text-gray-600">
                        Allergies (optional)
                        </p>

                        <div className="w-full rounded-lg border border-gray-300 px-4 py-2 
                                        focus:outline-none focus:ring-2 focus:ring-purple-400 max-h-40 overflow-auto space-y-2 pr-1">
                        {allergies.map((a) => (
                            <label key={a.allergyId} className="flex items-center gap-2 text-gray-700">
                            <input
                                type="checkbox"
                                checked={form.allergyIds.includes(a.allergyId)}
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
							disabled={saving}
                            className="flex-1 bg-purple-600 hover:bg-purple-700 transition py-2 rounded-lg font-semibold text-white"
                        >
                            {saving ? "Updating..." : "Update Profile"}
                        </button>
                    </div>
                </form>
        </div>
    );
}

export default EditPatientProfile;