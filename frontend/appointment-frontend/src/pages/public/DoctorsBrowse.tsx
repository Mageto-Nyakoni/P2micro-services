import { useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import { Doctor } from "@/types/doctorTypes";
import { getAllDoctors } from "@/services/doctorServices";
import DoctorList from "@/components/doctor/DoctorList";
import DoctorBrowseFilters from "@/components/doctor/DoctorBrowseFilters";

export default function DoctorsBrowse() {
    const navigate = useNavigate();

    // Data
    const [doctors, setDoctors] = useState<Doctor[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");

    // UI
    const [expandedId, setExpandedId] = useState<number | null>(null);

    // Filter
    const [query, setQuery] = useState<string>("");
    const [speciality, setSpeciality] = useState<string>("");
    const [gender, setGender] = useState<string>("");

  	useEffect(() => {
		//using AbortController to cancel fetch if component unmounts
		//used because of doctors is a large payload of data

		const controller = new AbortController();
		setLoading(true);
		setError("");
    
		// Fetch doctors from API
		getAllDoctors(controller.signal)
			.then ((data: Doctor[]) => setDoctors(data))
			.catch ((err: any) => {
				if (err?.name === "AbortError") return;
				if (err?.name === "CanceledError" || err?.code === "ERR_CANCELED") return;

				setError(err?.message ?? "Failed to load doctors.");
			})
			.finally(() => setLoading(false));

		return () => controller.abort();
	}, [])

	const specialityOptions = useMemo(() => {
		const names = doctors
			.map((doc) => doc.speciality?.specialityName)
			.filter((name): name is string => Boolean(name && name.trim()));
         
  		return Array.from(new Set(names)).sort((a, b) => a.localeCompare(b));
	}, [doctors])

	const genderOptions = ["Male", "Female"];

	const filteredDoctors = useMemo(() => {
		const q = query.toLowerCase().trim();

		return doctors.filter((doc) => {
			const name = `${doc.user.firstName} ${doc.user.lastName}`.toLowerCase();
			const spec = doc.speciality?.specialityName?.toLowerCase();
			
			const matchesQuery = q === "" || name.includes(q) || spec?.includes(q);

			const matchesSpeciality = speciality === "" || spec === speciality.toLowerCase();

			const matchesGender = gender === "" || doc.gender === gender.toLowerCase();

			return matchesQuery && matchesSpeciality && matchesGender;
		});
	}, [doctors, query, speciality, gender]);
  

	const toggleExpand = (id: number) => {
		setExpandedId((prev) => (prev === id ? null : id));
	};

	const handleBook = (doctorId: number) => {
		navigate("/patient/book", {state: { doctorId }});
	};

	return (
		<div className="min-h-screen bg-gray-50 px-6 md:px-10 py-10">
			<button
				onClick={() => navigate(-1)}
				className="mb-4 text-indigo-600 hover:underline"
			>
				← Back
			</button>

			<div className="flex items-start justify-between gap-4 mb-6">
				<div>
					<h1 className="text-3xl font-bold text-gray-900">Browse Doctors</h1>
						<p className="text-gray-600 mt-1">
							Search and filter by speciality, gender, and experience.
						</p>
				</div>

				<div className="text-sm text-gray-600">
					{loading ? "Loading…" : `${filteredDoctors.length} result(s)`}
				</div>
			</div>

			<DoctorBrowseFilters
				query={query}
				onQueryChange={setQuery}
				speciality={speciality}
				onSpecialityChange={setSpeciality}
				gender={gender}
				onGenderChange={setGender}
				specialityOptions={specialityOptions}
				genderOptions={genderOptions}
				disabled={loading}
			/>

			{error && (
				<div className="bg-white border border-red-200 text-red-700 rounded-xl p-4 mb-6">
					<div className="font-semibold">Could not load doctors</div>
					<div className="text-sm mt-1">{error}</div>
				</div>
			)}

			{!loading && !error && filteredDoctors.length === 0 && (
				<div className="bg-white border border-gray-200 rounded-xl p-6 text-gray-700">
					No doctors match your filters.
				</div>
			)}

			<DoctorList
				doctors={filteredDoctors}
				expandedDoctorId={expandedId}
				onToggle={toggleExpand}
				onBook={handleBook}
			/>
		</div>
	);
}

