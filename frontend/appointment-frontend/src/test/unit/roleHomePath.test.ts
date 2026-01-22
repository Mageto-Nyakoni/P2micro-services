import { describe, it, expect } from "vitest";
import { roleHomePath } from "@/utils/roleHomePath";
import { Role } from "@/components/layout/NavBar/types";

describe("roleHomePath (UNIT TEST)", () => {
  it("returns patient home path", () => {
    const role: Role = "Patient";
    expect(roleHomePath(role)).toBe("/patient/home");
  });

  it("returns doctor home path", () => {
    const role: Role = "Doctor";
    expect(roleHomePath(role)).toBe("/doctor/home");
  });

  it("returns admin home path", () => {
    const role: Role = "Admin";
    expect(roleHomePath(role)).toBe("/admin/home");
  });

  it("returns super home path", () => {
    const role: Role = "Super";
    expect(roleHomePath(role)).toBe("/super/home");
  });

  it("returns default path for unknown role", () => {
    expect(roleHomePath("Unknown" as Role)).toBe("/");
  });
});
