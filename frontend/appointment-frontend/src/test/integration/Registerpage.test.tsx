import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import Register from "@/pages/public/register/Register";
import { AuthContext, AuthContextType, User } from "@/auth/AuthContext";

// Create a mock AuthProvider for tests
const MockAuthProvider = ({ children }: { children: React.ReactNode }) => {
  const mockUser: User = {
    id: 123,
    email: "john@mail.com",
    role: "Patient",
  };

  const mockAuth: AuthContextType = {
    user: mockUser,
    token: "mock-token", // satisfies AuthContextType
    login: vi.fn(),
    logout: vi.fn(),
    isAuthenticated: true,
  };

  return (
    <AuthContext.Provider value={mockAuth}>
      {children}
    </AuthContext.Provider>
  );
};

describe("RegisterWizard (Integration Test)", () => {
  const renderWithProviders = () =>
    render(
      <MemoryRouter>
        <MockAuthProvider>
          <Register />
        </MockAuthProvider>
      </MemoryRouter>
    );

  test("moves from step 1 to step 2", async () => {
    renderWithProviders();

    await userEvent.type(
      screen.getByPlaceholderText(/first name/i),
      "John"
    );
    await userEvent.type(
      screen.getByPlaceholderText(/last name/i),
      "Doe"
    );
    await userEvent.type(
      screen.getByPlaceholderText(/email/i),
      "john@mail.com"
    );
    await userEvent.type(
      screen.getByPlaceholderText(/password/i),
      "Password@123"
    );

    await userEvent.click(screen.getByRole("button", { name: /next/i }));

    expect(await screen.findByText(/date of birth/i)).toBeInTheDocument();
  });

  test("shows validation error if step 1 is incomplete", async () => {
    renderWithProviders();

    await userEvent.click(screen.getByRole("button", { name: /next/i }));

    expect(await screen.findByText(/required/i)).toBeInTheDocument();
  });
});
