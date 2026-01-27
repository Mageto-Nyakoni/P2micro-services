import { Allergy, BloodType, PatientEditForm } from "@/types/patientTypes";
import { formatPhoneNumber, getAgeFromDOB } from "@/utils/validators";

type Props = {
    value: PatientEditForm;
    onChange: (next: PatientEditForm) => void;
    onSubmit: () => void | Promise<void>;
    saving: boolean;

    allergies: Allergy[];
    bloodTypes: BloodType[];

    onBack: () => void;
}

export default function EditPatientProfileForm({value, onChange, onSubmit, saving, allergies, bloodTypes, onBack}: Props){
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value: inputValue } = e.target;

        if (name === "phoneNumber") {
            onChange({
                ...value,
                phoneNumber: formatPhoneNumber(inputValue),
            });
            return;
        }
        
        onChange({
            ...value,
            [name]: inputValue,
        });
    };

    const toggleAllergy = (id: number) => {
        const nextIds = value.allergyIds.includes(id) ? value.allergyIds.filter((x) => x !== id) : [...value.allergyIds, id];
    
        onChange({
            ...value,
            allergyIds: nextIds,
        });
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        
        if (!value.age || !value.dateOfBirth || !value.phoneNumber || !value.address || !value.bloodType) {
            alert("Please fill all fields");
            return;
        }

        const enteredAge = Number(value.age);
        const computedAge = getAgeFromDOB(value.dateOfBirth);

        if (computedAge != enteredAge) {
            alert(`Age and Birthday do not match. Based on DOB, age should be ${computedAge}`);
            return;
        }

        await onSubmit();
    };

    return (
        <div className="p-6 max-w-2xl mx-auto">

        {/* Back link */}
        <button
            onClick={onBack}
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
                        value={value.age}
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
                        value={value.dateOfBirth}
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
                        value={value.gender}
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
                        value={value.phoneNumber}
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
                        value={value.address}
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
                        value={value.bloodType}
                        onChange={handleChange}
                        className="w-full rounded-lg border border-gray-300 px-4 py-2 
                                    focus:outline-none focus:ring-2 focus:ring-purple-400"
                    >
                        {(bloodTypes ?? []).map((bt) => (
                            <option key={bt.bloodTypeId} value={String(bt.name)}>
                            {bt.name}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="mb-2">
                    <p className="block text-sm mb-1 text-gray-600">
                    Allergies
                    </p>

                    <div className="w-full rounded-lg border border-gray-300 px-4 py-2 
                                    focus:outline-none focus:ring-2 focus:ring-purple-400 max-h-40 overflow-auto space-y-2 pr-1">
                            {allergies.map((a) => (
                                <label key={a.allergyId} className="flex items-center gap-2 text-gray-700">
                                    <input
                                        type="checkbox"
                                        checked={value.allergyIds.includes(a.allergyId)}
                                        onChange={() => toggleAllergy(a.allergyId)}
                                    />
                                    {a.name}
                                </label>
                            ))}
                        {/* )} */}
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