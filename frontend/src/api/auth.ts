import { apiFetch } from './client'
import type { AuthResponse, RegistrationResponse, TokenResponse } from './types'

export interface RegisterPayload {
  email: string
  password: string
  name: string
}

export interface LoginPayload {
  email: string
  password: string
}

export interface TokenPayload {
  refreshToken: string
}

export const registerUser = (payload: RegisterPayload) =>
  apiFetch<RegistrationResponse>('/api/v1/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const loginUser = (payload: LoginPayload) =>
  apiFetch<AuthResponse>('/api/v1/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const refreshToken = (payload: TokenPayload) =>
  apiFetch<TokenResponse>('/api/v1/auth/refresh', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const logoutUser = (payload: TokenPayload) =>
  apiFetch<void>('/api/v1/auth/logout', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
