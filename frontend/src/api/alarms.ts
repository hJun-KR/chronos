import { apiFetch } from './client'
import type {
  AlarmCreatePayload,
  AlarmExecutionLog,
  AlarmResponse,
  AlarmSimulationRequestPayload,
  AlarmSimulationResponse,
  AlarmUpdatePayload,
} from './types'

export const getAlarms = (accessToken: string) =>
  apiFetch<AlarmResponse[]>('/api/v1/alarms', {}, accessToken)

export const createAlarm = (accessToken: string, payload: AlarmCreatePayload) =>
  apiFetch<AlarmResponse>(
    '/api/v1/alarms',
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
    accessToken,
  )

export const updateAlarm = (
  accessToken: string,
  alarmId: number,
  payload: AlarmUpdatePayload,
) =>
  apiFetch<AlarmResponse>(
    `/api/v1/alarms/${alarmId}`,
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    accessToken,
  )

export const deleteAlarm = (accessToken: string, alarmId: number) =>
  apiFetch<void>(
    `/api/v1/alarms/${alarmId}`,
    {
      method: 'DELETE',
    },
    accessToken,
  )

export const runAlarmNow = (accessToken: string, alarmId: number) =>
  apiFetch<void>(
    `/api/v1/alarms/${alarmId}/run-now`,
    {
      method: 'POST',
    },
    accessToken,
  )

export const simulateAlarm = (
  accessToken: string,
  alarmId: number,
  payload: AlarmSimulationRequestPayload,
) =>
  apiFetch<AlarmSimulationResponse>(
    `/api/v1/alarms/${alarmId}/simulate`,
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
    accessToken,
  )

export const getAlarmLogs = (accessToken: string, alarmId: number) =>
  apiFetch<AlarmExecutionLog[]>(
    `/api/v1/alarms/${alarmId}/logs`,
    {},
    accessToken,
  )
