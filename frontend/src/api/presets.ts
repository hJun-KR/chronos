import { apiFetch } from './client'
import type { ConditionPresetResponse } from './types'

export interface ConditionPresetRequest {
  name: string
  description?: string
  conditionsJson: string
}

export async function createPreset(accessToken: string, payload: ConditionPresetRequest) {
  return apiFetch<ConditionPresetResponse>('/api/v1/presets', {
    method: 'POST',
    body: JSON.stringify(payload),
  }, accessToken)
}

export async function getPresets(accessToken: string) {
  return apiFetch<ConditionPresetResponse[]>('/api/v1/presets', {
    method: 'GET',
  }, accessToken)
}

export async function deletePreset(accessToken: string, presetId: number) {
  return apiFetch<void>(`/api/v1/presets/${presetId}`, {
    method: 'DELETE',
  }, accessToken)
}
