import { apiFetch } from './client'

interface VerificationSendPayload {
  email: string
}

interface VerificationConfirmPayload extends VerificationSendPayload {
  code: string
}

export const sendVerificationCode = (payload: VerificationSendPayload) =>
  apiFetch<void>('/api/v1/email-verifications/send', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const verifyEmailCode = (payload: VerificationConfirmPayload) =>
  apiFetch<void>('/api/v1/email-verifications/verify', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
