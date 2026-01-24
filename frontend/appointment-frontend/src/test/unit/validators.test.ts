import { describe, it, expect } from "vitest";
import { isValidEmail, isStrongPassword } from "@/utils/validators";

describe("validators (UNIT TEST)", () => {
  it("returns true for valid email", () => {
    expect(isValidEmail("tester@mail.com")).toBe(true);
  });

  it("returns false for invalid email", () => {
    expect(isValidEmail("testmail.com")).toBe(false);
  });

  it("returns true for strong password", () => {
    expect(isStrongPassword("abC123")).toBe(true);
  });

  it("returns false for weak password", () => {
    expect(isStrongPassword("123")).toBe(false);
  });
});
