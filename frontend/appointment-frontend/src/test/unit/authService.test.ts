/// <reference types="vitest" />

import { describe, it, expect,vi, beforeEach } from "vitest";
import { http } from "@/services/http";
import { login } from "@/services/authService";

// Mock http.post
vi.mock("@/services/http", () => ({
  http: { post: vi.fn() },
}));

describe("authService.login (UNIT TEST)", () => {
 beforeEach(() => {
  vi.clearAllMocks();
});
  it("calls login API with correct email and password", async () => {
    (http.post as unknown as ReturnType<typeof vi.fn>).mockResolvedValue({
      data: { token: "fake-token", user: { role: "Patient" } },
    });

    const result = await login("tester@mail.com", "password");

    expect(http.post).toHaveBeenCalledWith("/auth/login", {
      email: "tester@mail.com",
      password: "password",
    });

    expect(result).toEqual({
      token: "fake-token",
      user: { role: "Patient" },
    });
  });

  it("throws an error when API fails", async () => {
    (http.post as unknown as ReturnType<typeof vi.fn>).mockRejectedValue(new Error("Network error"));

    await expect(login("tester@mail.com", "password")).rejects.toThrow("Network error");
  });
});
