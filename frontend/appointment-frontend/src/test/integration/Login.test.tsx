import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import Login from "@/pages/public/Login";

test("user can log in successfully", async () => {
  render(
    <MemoryRouter>
      <Login />
    </MemoryRouter>
  );

  await userEvent.type(
    screen.getByPlaceholderText(/email/i),
    "tester@mail.com"
  );

  await userEvent.type(
    screen.getByPlaceholderText(/password/i),
    "password"
  );

  await userEvent.click(
    screen.getByRole("button", { name: /login/i })
  );

  expect(
    await screen.findByText(/dashboard/i)
  ).toBeInTheDocument();
});
