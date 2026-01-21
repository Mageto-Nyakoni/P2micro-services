/*import { render, screen, fireEvent } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import LoginPage from "@/pages/public/Login";
import { vi } from "vitest";

// 🔹 mock navigate
const mockNavigate = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<any>("react-router-dom");
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

// 🔹 mock useAuth
vi.mock("@/auth/useAuth", () => ({
  useAuth: () => ({
    login: vi.fn(),
    user: null,
    logout: vi.fn(),
  }),
}));

// 🔹 mock login API
const mockLoginAPI = vi.fn();

vi.mock("@/services/authService", () => ({
  login: (...args: any[]) => mockLoginAPI(...args),
}));

describe("Login Page", () => {
  it("renders login page", () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    expect(
      screen.getByText(/Smart Appointment System/i)
    ).toBeInTheDocument();
  });

  it("submits login form with email and password", async () => {
    mockLoginAPI.mockResolvedValueOnce({
      user: { role: "PATIENT" },
      token: "fake-token",
    });

    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    fireEvent.change(
      screen.getByPlaceholderText(/you@example.com/i),
      { target: { value: "tester@mail.com" } }
    );

    fireEvent.change(
      screen.getByPlaceholderText(/enter your password/i),
      { target: { value: "password" } }
    );

    fireEvent.click(
      screen.getByRole("button", { name: /log in/i })
    );

    expect(mockLoginAPI).toHaveBeenCalledWith(
      "tester@mail.com",
      "password"
    );
  });
});*/

import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { BrowserRouter } from "react-router-dom";
import LoginPage from "@/pages/public/Login";
import { vi } from "vitest";

// Mock navigate
const mockNavigate = vi.fn();
vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<any>("react-router-dom");
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

// Mock useAuth
const mockLoginContext = { login: vi.fn(), user: null, logout: vi.fn() };
vi.mock("@/auth/useAuth", () => ({
  useAuth: () => mockLoginContext,
}));

// Mock login API
const mockLoginAPI = vi.fn();
vi.mock("@/services/authService", () => ({
  login: (...args: any[]) => mockLoginAPI(...args),
}));

describe("Login Page", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders login page correctly", () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    expect(screen.getByText(/Smart Appointment System/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument(); // matches label
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument(); // matches label
    expect(screen.getByRole("button", { name: /log in/i })).toBeInTheDocument();
  });

  it("submits login form successfully", async () => {
    mockLoginAPI.mockResolvedValueOnce({
      user: { role: "PATIENT" },
      token: "fake-token",
    });

    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const loginButton = screen.getByRole("button", { name: /log in/i });

    await userEvent.clear(emailInput);
    await userEvent.type(emailInput, "tester@mail.com");
    await userEvent.clear(passwordInput);
    await userEvent.type(passwordInput, "password");
    await userEvent.click(loginButton);

    expect(mockLoginAPI).toHaveBeenCalledWith("tester@mail.com", "password");
    expect(mockNavigate).toHaveBeenCalled(); // exact path depends on roleHomePath
  });

  it("shows error for invalid credentials", async () => {
    mockLoginAPI.mockRejectedValueOnce(new Error("Invalid credentials"));

    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    const emailInput = screen.getByLabelText(/email/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const loginButton = screen.getByRole("button", { name: /log in/i });

    await userEvent.clear(emailInput);
    await userEvent.type(emailInput, "wrong@mail.com");
    await userEvent.clear(passwordInput);
    await userEvent.type(passwordInput, "wrongpassword");
    await userEvent.click(loginButton);

    expect(mockLoginAPI).toHaveBeenCalledWith("wrong@mail.com", "wrongpassword");

    // Check that an error message is displayed
    expect(await screen.findByText(/login failed/i)).toBeInTheDocument();
  });
});


