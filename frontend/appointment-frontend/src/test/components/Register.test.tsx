import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import Register from '@/pages/public/register/Register'
import * as authService from '@/services/authService'
import { vi } from 'vitest'

vi.mock('@/services/authService')

describe('Register', () => {
  const mockedRegister = vi.mocked(authService.register)

  beforeAll(() => {
    vi.clearAllMocks()
  })

  it('completes Step 1 and renders Step 2', async () => {
    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    )

    // Step 1
    fireEvent.change(screen.getByLabelText(/first name/i), { target: { value: 'John' } })
    fireEvent.change(screen.getByLabelText(/last name/i), { target: { value: 'Doe' } })
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'test@example.com' } })
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'Password123!' } })

    fireEvent.click(screen.getByRole('button', { name: /continue/i }))

    // Step 2 should now render
    expect(await screen.findByLabelText(/age/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/gender/i)).toBeInTheDocument()
  })

  it('submits Step 2 successfully', async () => {
    mockedRegister.mockResolvedValueOnce({
      token: 'fake-token',
      user: { id: 1, email: 'tester@example.com', role: 'Patient' },
    })

    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>
    )

     // Step 1
    fireEvent.change(screen.getByLabelText(/first name/i), { target: { value: 'John' } })
    fireEvent.change(screen.getByLabelText(/last name/i), { target: { value: 'Doe' } })
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'test@example.com' } })
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'Password123!' } })

    fireEvent.click(screen.getByRole('button', { name: /continue/i }))

    // Step 2: Fill all required fields
    fireEvent.change(await screen.findByLabelText(/age/i), { target: { value: '30' } })
    fireEvent.change(screen.getByLabelText(/DOB/i), { target: { value: '2000-01-01' } })
    fireEvent.change(screen.getByLabelText(/gender/i), { target: { value: 'male' } })
    fireEvent.change(screen.getByLabelText(/phone number/i), { target: { value: '555-555-5555' } })
    fireEvent.change(screen.getByLabelText(/address/i), { target: { value: '123 Main St' } })
    fireEvent.change(screen.getByLabelText(/blood type/i), { target: { value: '1' } }) // O+

    fireEvent.click(screen.getByRole('button', { name: /register/i }))

    await waitFor(() => expect(mockedRegister).toHaveBeenCalled())
  })
})