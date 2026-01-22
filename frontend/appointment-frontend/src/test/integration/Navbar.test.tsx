import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import Navbar from "@/components/layout/NavBar/Navbar";

// Mock useAuth
vi.mock("@/auth/useAuth", () => ({
  useAuth: () => ({
    user: { role: "Patient" },
    isAuthenticated: true,
    logout: vi.fn(),
  }),
}));

describe("Navbar (Integration Test)", () => {
  test("shows patient navigation links", () => {
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );

    expect(screen.getByText(/appointments/i)).toBeInTheDocument();
    expect(screen.getByText(/profile/i)).toBeInTheDocument();
  });

  test("logout button works", async () => {
    const logoutMock = vi.fn();

    vi.mocked(require("@/auth/useAuth").useAuth).mockReturnValue({
      user: { role: "Patient" },
      isAuthenticated: true,
      logout: logoutMock,
    });

    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );

    await userEvent.click(
      screen.getByRole("button", { name: /logout/i })
    );

    expect(logoutMock).toHaveBeenCalled();
  });
});
