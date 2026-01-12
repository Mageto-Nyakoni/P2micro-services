import { Routes, Route, Navigate } from "react-router-dom";
import ProtectedRoute from "./routes/ProtectedRoute";

// Layouts
import GuestLayout from "./layouts/GuestLayout";
import PatientLayout from "./layouts/PatientLayout";
import DoctorLayout from "./layouts/DoctorLayout";

// Pages
import GuestHome from "./pages/public/GuestHome";
import Login from "./pages/public/Login";
import Register from "./pages/public/Register";
import DoctorsBrowse from "./pages/public/DoctorsBrowse";

import PatientHome from "./pages/patient/PatientHome";
import PatientProfile from "./pages/patient/PatientProfile";
import BookAppointment from "./pages/patient/BookAppointment";
import EditPatientProfile from "./pages/patient/EditPatientProfile";

import DoctorHome from "./pages/doctor/DoctorHome";
import DoctorCalendar from "./pages/doctor/DoctorCalendar";

function App() {
  return (
    <Routes>

      {/* ================= PUBLIC / GUEST ================= */}
      <Route element={<GuestLayout />}>
        <Route path="/" element={<GuestHome />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/doctors" element={<DoctorsBrowse />} />
      </Route>

      {/* ================= PATIENT (PROTECTED) ================= */}
      <Route
        path="/patient"
        element={
          <ProtectedRoute>
            <PatientLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="home" replace />} />
        <Route path="home" element={<PatientHome />} />
        <Route path="profile" element={<PatientProfile />} />
        <Route path="profile/edit" element={<EditPatientProfile />} />
        <Route path="book" element={<BookAppointment />} />
        <Route path="doctors" element={<DoctorsBrowse />} />
      </Route>

      {/* ================= DOCTOR ================= */}
      <Route element={<DoctorLayout />}>
        <Route path="/doctor/home" element={<DoctorHome />} />
        <Route path="/doctor/calendar" element={<DoctorCalendar />} />
      </Route>

    </Routes>
  );
}

export default App;
