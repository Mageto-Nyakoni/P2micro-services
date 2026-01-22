import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import Register from '@/pages/public/register/Register'
import * as authService from '@/services/authService/authService'
import * as patientService from '@/services/patientServices'
import { vi } from 'vitest'
import { AuthContext } from '@/auth/AuthContext'

// Mock the service modules
vi.mock('@/services/authService/authService')
vi.mock('@/services/patientServices')

// Create typed mocks for the functions we need
const mockedRegisterStep1 = vi.mocked(authService.registerStep1)
const mockedLogin = vi.mocked(authService.login)
const mockedPatchPatient = vi.mocked(patientService.patchPatient)

describe('Register', () => {
  const mockAuthValue = {
    token: null,
    isAuthenticated: false,
    user: null,
    login: vi.fn(),
    logout: vi.fn(),
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('completes Step 1 and renders Step 2', async () => {
mockedRegisterStep1.mockResolvedValueOnce({
  userId: 1,
  email: 'test@example.com',
  privilege: 1,
})

    render(
      <AuthContext.Provider value={mockAuthValue}>
        <MemoryRouter>
          <Register />
        </MemoryRouter>
      </AuthContext.Provider>
    )

    // Fill Step 1 form
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
    mockedRegisterStep1.mockResolvedValueOnce({
  userId: 1,
  email: 'test@example.com',
  privilege: 1,
})
    mockedLogin.mockResolvedValueOnce({
      token: 'fake-token',
      user: { id: 1, email: 'tester@example.com', role: 'Patient' },
    })
    mockedPatchPatient.mockResolvedValueOnce({patientId: 1,
  
  age: 30,
  gender: 'male',
  phoneNumber: '555-555-5555',
  dateOfBirth: '2000-01-01',
  address: '123 Main St',
  bloodType: 'O+',
  allergies: [{ allergyId: 1, name: 'Peanuts' }],
  user: {
    userId: 1,
    email: 'tester@example.com',
    firstName: 'John',
    lastName: 'Doe',
    privilege: { privilegeId: 1, roleName: 'Patient' },
  },
})

    render(
      <AuthContext.Provider value={mockAuthValue}>
        <MemoryRouter>
          <Register />
        </MemoryRouter>
      </AuthContext.Provider>
    )

    // Fill Step 1
    fireEvent.change(screen.getByLabelText(/first name/i), { target: { value: 'John' } })
    fireEvent.change(screen.getByLabelText(/last name/i), { target: { value: 'Doe' } })
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'test@example.com' } })
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'Password123!' } })
    fireEvent.click(screen.getByRole('button', { name: /continue/i }))

    // Fill Step 2
    fireEvent.change(await screen.findByLabelText(/age/i), { target: { value: '30' } })
    fireEvent.change(screen.getByLabelText(/DOB/i), { target: { value: '2000-01-01' } })
    fireEvent.change(screen.getByLabelText(/gender/i), { target: { value: 'male' } })
    fireEvent.change(screen.getByLabelText(/phone number/i), { target: { value: '555-555-5555' } })
    fireEvent.change(screen.getByLabelText(/address/i), { target: { value: '123 Main St' } })
    fireEvent.change(screen.getByLabelText(/blood type/i), { target: { value: '1' } })

    fireEvent.click(screen.getByRole('button', { name: /register/i }))

    await waitFor(() => {
      expect(mockedLogin).toHaveBeenCalledWith('test@example.com', 'Password123!')
      expect(mockedPatchPatient).toHaveBeenCalled()
      expect(mockAuthValue.login).toHaveBeenCalled()
    })
  })
})
