export interface UserSummary {
  id: number
  email: string
  name: string
  role: string
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  expiresIn: number
}

export interface RegistrationResponse {
  user: UserSummary
  message: string
}

export interface AuthResponse {
  user: UserSummary
  token: TokenResponse
}

export interface AlarmConditionDto {
  conditionType: string
  operator: string
  fieldKey: string
  fieldValue: string
  extraJson?: string | null
}

export interface AlarmConditionResponse extends AlarmConditionDto {
  id: number
}

export interface AlarmResponse {
  id: number
  name: string
  description?: string | null
  alarmType: string
  scheduleType: string
  cronExpression?: string | null
  runAt?: string | null
  timezone: string
  channel: string
  targetAddress: string
  status: string
  lastRunAt?: string | null
  nextRunAt?: string | null
  lastResult?: string | null
  conditions: AlarmConditionResponse[]
  recurrenceType?: string | null
  daysOfWeek?: string[] | null
  dayOfMonth?: number | null
  monthOfYear?: number | null
}

export interface AlarmExecutionLog {
  id: number
  executedAt: string
  success: boolean
  message?: string | null
  payloadSnapshot?: string | null
}

export interface AlarmSimulationRequestPayload {
  weatherData?: Record<string, unknown>
  stockData?: Record<string, unknown>
  customData?: Record<string, unknown>
  timezone?: string
  sendNotification: boolean
}

export interface AlarmSimulationResponse {
  conditionsMet: boolean
  notificationSent: boolean
  message?: string | null
}

export interface ConditionPreset {
  key: string
  name: string
  description: string
  conditions: AlarmConditionDto[]
}

export interface AlarmCreatePayload {
  name: string
  description?: string
  alarmType: string
  scheduleType: string
  cronExpression?: string
  runAt?: string
  timezone: string
  channel: string
  targetAddress: string
  conditions: AlarmConditionDto[]
  recurrenceType?: string
  daysOfWeek?: string[]
  dayOfMonth?: number
  monthOfYear?: number
}

export interface AlarmUpdatePayload extends AlarmCreatePayload {
  status: string
}
