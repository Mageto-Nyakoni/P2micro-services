import PatientNavbar from "../components/layout/NavBar/Navbar";

import { Outlet } from "react-router-dom";

const PatientLayout = ()=> {
  return (
    <>
      <PatientNavbar />
      <Outlet />
    </>
  );
}

export default PatientLayout;
