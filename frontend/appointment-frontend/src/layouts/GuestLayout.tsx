import GuestNavbar from "src/components/layout/NavBar/Navbar";
import { Outlet } from "react-router-dom";

function GuestLayout() {
  return (
    <>
      <GuestNavbar />
      <Outlet />
    </>
  );
}

export default GuestLayout;
