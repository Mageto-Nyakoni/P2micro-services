import { i } from "vite/dist/node/types.d-aGj9QkWt";
import Navbar from "src/components/layout/NavBar/Navbar";
import { Outlet } from "react-router-dom";
import React from "react";

function DoctorLayout() {
  return (
    <>
      <Navbar />
      <Outlet />
    </>
  );
}

export default DoctorLayout;
