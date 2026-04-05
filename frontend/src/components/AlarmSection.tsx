import { useCallback, useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import type { AuthTokens } from '../api/client'
import { getErrorMessage } from '../api/client'
import {
  createAlarm,
  deleteAlarm,
  getAlarmLogs,
  getAlarms,
  runAlarmNow,
  simulateAlarm,
  updateAlarm,
} from '../api/alarms'
import type {
  AlarmConditionDto,
  AlarmExecutionLog,
  AlarmResponse,
  AlarmSimulationResponse,
} from '../api/types'

const DEFAULT_CONDITIONS = `[
  {
    "conditionType": "TIME_RANGE",
    "operator": "BETWEEN",
    "fieldKey": "hour",
    "fieldValue": "09:00,18:00",
    "extraJson": "{\\"value\\":\\"10:00\\"}"
  }
]`

const initialAlarmForm = {
  name: '업무시간',
  description: '9~18시 알람',
  alarmType: 'TIME',
  scheduleType: 'RECURRING',
  cronExpression: '',
  runAt: '',
  timezone: 'Asia/Seoul',
  channel: 'EMAIL',
  targetAddress: '',
  conditionsJson: DEFAULT_CONDITIONS,
  recurrenceType: 'WEEKLY',
  daysOfWeek: 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY',
  dayOfMonth: '',
  monthOfYear: '',
  status: 'ACTIVE',
}

const initialSimulationForm = {
  alarmId: '',
  timezone: 'Asia/Seoul',
  sendNotification: false,
  weatherJson: '{\n  "T1H": 8\n}',
  stockJson: '{\n  "NASDAQ": 10000\n}',
  customJson: '{\n  "hour": "10:00"\n}',
}

interface AlarmSectionProps {
  tokens: AuthTokens | null
}

const AlarmSection = ({ tokens }: AlarmSectionProps) => {
  const [alarmForm, setAlarmForm] = useState(initialAlarmForm)
  const [simulationForm, setSimulationForm] = useState(initialSimulationForm)
  const [alarms, setAlarms] = useState<AlarmResponse[]>([])
  const [selectedAlarmId, setSelectedAlarmId] = useState<number | null>(null)
  const [logs, setLogs] = useState<AlarmExecutionLog[]>([])
  const [logsAlarmId, setLogsAlarmId] = useState<number | null>(null)
  const [simulationResult, setSimulationResult] =
    useState<AlarmSimulationResponse | null>(null)
  const [status, setStatus] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const accessToken = tokens?.accessToken ?? null
  const isEditing = selectedAlarmId !== null

  const clearFeedback = () => {
    setStatus(null)
    setError(null)
  }

  const handleStatus = (message: string) => {
    setStatus(message)
    setError(null)
  }

  const handleError = (err: unknown) => {
    setError(getErrorMessage(err))
    setStatus(null)
  }

  const parseConditions = (): AlarmConditionDto[] => {
    const parsed = JSON.parse(alarmForm.conditionsJson)
    if (!Array.isArray(parsed) || parsed.length === 0) {
      throw new Error('조건은 최소 1개 이상이어야 합니다.')
    }
    return parsed.map((condition) => ({
      conditionType: condition.conditionType,
      operator: condition.operator,
      fieldKey: condition.fieldKey,
      fieldValue: condition.fieldValue,
      extraJson: condition.extraJson ?? null,
    }))
  }

  const parseDaysOfWeek = (value: string): string[] | undefined => {
    const items = value
      .split(',')
      .map((item) => item.trim().toUpperCase())
      .filter(Boolean)
    return items.length ? items : undefined
  }

  const normalizeDateTime = (value: string): string | undefined => {
    if (!value) {
      return undefined
    }
    return value.length === 16 ? `${value}:00` : value
  }

  const setFormFromAlarm = useCallback((alarm: AlarmResponse) => {
    const serializedConditions = JSON.stringify(
      alarm.conditions.map(
        ({ conditionType, operator, fieldKey, fieldValue, extraJson }) => ({
          conditionType,
          operator,
          fieldKey,
          fieldValue,
          extraJson,
        }),
      ),
      null,
      2,
    )
    setAlarmForm({
      name: alarm.name,
      description: alarm.description ?? '',
      alarmType: alarm.alarmType,
      scheduleType: alarm.scheduleType,
      cronExpression: alarm.cronExpression ?? '',
      runAt: alarm.runAt ? alarm.runAt.slice(0, 16) : '',
      timezone: alarm.timezone,
      channel: alarm.channel,
      targetAddress: alarm.targetAddress,
      conditionsJson: serializedConditions,
      recurrenceType: alarm.recurrenceType ?? '',
      daysOfWeek: alarm.daysOfWeek?.join(',') ?? '',
      dayOfMonth: alarm.dayOfMonth ? String(alarm.dayOfMonth) : '',
      monthOfYear: alarm.monthOfYear ? String(alarm.monthOfYear) : '',
      status: alarm.status ?? 'ACTIVE',
    })
    setSimulationForm((prev) => ({
      ...prev,
      alarmId: String(alarm.id),
    }))
  }, [])

  const resetForm = useCallback(() => {
    setAlarmForm({ ...initialAlarmForm })
    setSelectedAlarmId(null)
  }, [])

  const refreshAlarms = useCallback(async () => {
    if (!accessToken) {
      setAlarms([])
      setSelectedAlarmId(null)
      return
    }
    setLoading(true)
    try {
      const list = await getAlarms(accessToken)
      setAlarms(list)
      if (selectedAlarmId) {
        const current = list.find((alarm) => alarm.id === selectedAlarmId)
        if (!current) {
          resetForm()
        } else {
          setFormFromAlarm(current)
        }
      }
    } catch (err) {
      handleError(err)
    } finally {
      setLoading(false)
    }
  }, [accessToken, selectedAlarmId, setFormFromAlarm, resetForm])

  useEffect(() => {
    refreshAlarms()
  }, [refreshAlarms])

  const handleAlarmSubmit = async (event: FormEvent) => {
    event.preventDefault()
    if (!accessToken) {
      setError('로그인이 필요합니다.')
      return
    }
    clearFeedback()
    try {
      const conditions = parseConditions()
      if (
        alarmForm.scheduleType === 'ONCE' &&
        !normalizeDateTime(alarmForm.runAt)
      ) {
        throw new Error('ONCE 스케줄은 실행 시간을 지정해야 합니다.')
      }
      if (
        alarmForm.scheduleType === 'RECURRING' &&
        !alarmForm.cronExpression &&
        !alarmForm.recurrenceType
      ) {
        throw new Error('RECURRING 스케줄은 cron 또는 반복 설정이 필요합니다.')
      }
      const basePayload = {
        name: alarmForm.name,
        description: alarmForm.description || undefined,
        alarmType: alarmForm.alarmType,
        scheduleType: alarmForm.scheduleType,
        cronExpression: alarmForm.cronExpression || undefined,
        runAt: normalizeDateTime(alarmForm.runAt),
        timezone: alarmForm.timezone,
        channel: alarmForm.channel,
        targetAddress: alarmForm.targetAddress,
        conditions,
        recurrenceType: alarmForm.recurrenceType || undefined,
        daysOfWeek: parseDaysOfWeek(alarmForm.daysOfWeek),
        dayOfMonth: alarmForm.dayOfMonth
          ? Number(alarmForm.dayOfMonth)
          : undefined,
        monthOfYear: alarmForm.monthOfYear
          ? Number(alarmForm.monthOfYear)
          : undefined,
      }
      if (isEditing && selectedAlarmId) {
        await updateAlarm(accessToken, selectedAlarmId, {
          ...basePayload,
          status: alarmForm.status || 'ACTIVE',
        })
        handleStatus('알람이 수정되었습니다.')
      } else {
        await createAlarm(accessToken, basePayload)
        handleStatus('알람이 생성되었습니다.')
        resetForm()
      }
      refreshAlarms()
    } catch (err) {
      handleError(err)
    }
  }

  const handleDelete = async (alarmId: number) => {
    if (!accessToken) {
      setError('로그인이 필요합니다.')
      return
    }
    clearFeedback()
    try {
      await deleteAlarm(accessToken, alarmId)
      handleStatus('알람이 삭제되었습니다.')
      if (selectedAlarmId === alarmId) {
        resetForm()
      }
      refreshAlarms()
    } catch (err) {
      handleError(err)
    }
  }

  const handleRunNow = async (alarmId: number) => {
    if (!accessToken) {
      setError('로그인이 필요합니다.')
      return
    }
    clearFeedback()
    try {
      await runAlarmNow(accessToken, alarmId)
      handleStatus('즉시 실행이 요청되었습니다.')
    } catch (err) {
      handleError(err)
    }
  }

  const handleSelectAlarm = (alarm: AlarmResponse) => {
    setSelectedAlarmId(alarm.id)
    setFormFromAlarm(alarm)
  }

  const handleSimulationSubmit = async (event: FormEvent) => {
    event.preventDefault()
    if (!accessToken) {
      setError('로그인이 필요합니다.')
      return
    }
    const alarmId = Number(simulationForm.alarmId)
    if (!alarmId) {
      setError('시뮬레이션할 알람 ID를 입력하세요.')
      return
    }
    clearFeedback()
    try {
      const payload = {
        weatherData: JSON.parse(simulationForm.weatherJson || '{}'),
        stockData: JSON.parse(simulationForm.stockJson || '{}'),
        customData: JSON.parse(simulationForm.customJson || '{}'),
        timezone: simulationForm.timezone,
        sendNotification: simulationForm.sendNotification,
      }
      const result = await simulateAlarm(accessToken, alarmId, payload)
      setSimulationResult(result)
      handleStatus('시뮬레이션이 완료되었습니다.')
    } catch (err) {
      handleError(err)
    }
  }

  const handleLogs = async (alarmId: number) => {
    if (!accessToken) {
      setError('로그인이 필요합니다.')
      return
    }
    clearFeedback()
    try {
      const response = await getAlarmLogs(accessToken, alarmId)
      setLogs(response)
      setLogsAlarmId(alarmId)
      handleStatus('실행 로그를 불러왔습니다.')
    } catch (err) {
      handleError(err)
    }
  }

  useEffect(() => {
    if (!accessToken) {
      setLogs([])
      setLogsAlarmId(null)
      setSimulationResult(null)
      setSimulationForm({ ...initialSimulationForm })
      resetForm()
    }
  }, [accessToken, resetForm])

  const alarmSummary = useMemo(
    () =>
      alarms.map((alarm) => ({
        id: alarm.id,
        title: `${alarm.name} (${alarm.scheduleType})`,
        description: alarm.description,
        target: `${alarm.channel} → ${alarm.targetAddress}`,
        nextRun: alarm.nextRunAt ?? '예정 없음',
        status: alarm.status,
      })),
    [alarms],
  )

  return (
    <section className="panel">
      <header>
        <h2>2. 알람 관리</h2>
        <p>
          로그인 후 알람을 생성하고 CRUD, 시뮬레이션, 로그 확인까지 모든 시나리오를
          검증할 수 있습니다.
        </p>
      </header>

      <div className="button-row">
        <button type="button" onClick={refreshAlarms} disabled={!accessToken || loading}>
          {loading ? '불러오는 중...' : '알람 목록 새로고침'}
        </button>
        <button type="button" className="secondary" onClick={resetForm}>
          폼 초기화
        </button>
      </div>

      {!accessToken && (
        <div className="feedback warning">
          알람 관리 기능을 사용하려면 먼저 로그인하세요.
        </div>
      )}

      <div className="panel-grid">
        <div className="card">
          <h3>{isEditing ? '알람 수정' : '새 알람 생성'}</h3>
          <form onSubmit={handleAlarmSubmit} className="form-grid">
            <label>
              이름
              <input
                type="text"
                required
                value={alarmForm.name}
                onChange={(event) =>
                  setAlarmForm({ ...alarmForm, name: event.target.value })
                }
              />
            </label>
            <label>
              설명
              <input
                type="text"
                value={alarmForm.description}
                onChange={(event) =>
                  setAlarmForm({
                    ...alarmForm,
                    description: event.target.value,
                  })
                }
              />
            </label>
            <label>
              알람 유형
              <select
                value={alarmForm.alarmType}
                onChange={(event) =>
                  setAlarmForm({ ...alarmForm, alarmType: event.target.value })
                }
              >
                {['TIME', 'WEATHER', 'STOCK', 'CUSTOM'].map((type) => (
                  <option key={type}>{type}</option>
                ))}
              </select>
            </label>
            <label>
              스케줄 유형
              <select
                value={alarmForm.scheduleType}
                onChange={(event) =>
                  setAlarmForm({
                    ...alarmForm,
                    scheduleType: event.target.value,
                  })
                }
              >
                {['ONCE', 'RECURRING'].map((type) => (
                  <option key={type}>{type}</option>
                ))}
              </select>
            </label>
            <label>
              실행 시간 (runAt)
              <input
                type="datetime-local"
                value={alarmForm.runAt}
                onChange={(event) =>
                  setAlarmForm({ ...alarmForm, runAt: event.target.value })
                }
              />
            </label>
            <label>
              Cron 표현식
              <input
                type="text"
                value={alarmForm.cronExpression}
                onChange={(event) =>
                  setAlarmForm({
                    ...alarmForm,
                    cronExpression: event.target.value,
                  })
                }
                placeholder="0 0 * * *"
              />
            </label>
            <label>
              시간대
              <input
                type="text"
                required
                value={alarmForm.timezone}
                onChange={(event) =>
                  setAlarmForm({ ...alarmForm, timezone: event.target.value })
                }
                placeholder="Asia/Seoul"
              />
            </label>
            <label>
              채널
              <select
                value={alarmForm.channel}
                onChange={(event) =>
                  setAlarmForm({ ...alarmForm, channel: event.target.value })
                }
              >
                {['EMAIL', 'DISCORD'].map((channel) => (
                  <option key={channel}>{channel}</option>
                ))}
              </select>
            </label>
            <label>
              대상 주소
              <input
                type="text"
                required
                value={alarmForm.targetAddress}
                onChange={(event) =>
                  setAlarmForm({
                    ...alarmForm,
                    targetAddress: event.target.value,
                  })
                }
                placeholder="user@example.com"
              />
            </label>
            <label>
              반복 유형
              <select
                value={alarmForm.recurrenceType}
                onChange={(event) =>
                  setAlarmForm({
                    ...alarmForm,
                    recurrenceType: event.target.value,
                  })
                }
              >
                <option value="">선택 안 함</option>
                {['DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'].map((option) => (
                  <option key={option}>{option}</option>
                ))}
              </select>
            </label>
            <label>
              요일 (쉼표 구분)
              <input
                type="text"
                value={alarmForm.daysOfWeek}
                onChange={(event) =>
                  setAlarmForm({
                    ...alarmForm,
                    daysOfWeek: event.target.value,
                  })
                }
                placeholder="MONDAY,TUESDAY"
              />
            </label>
            <label>
              일(dayOfMonth)
              <input
                type="number"
                min={1}
                max={31}
                value={alarmForm.dayOfMonth}
                onChange={(event) =>
                  setAlarmForm({
                    ...alarmForm,
                    dayOfMonth: event.target.value,
                  })
                }
              />
            </label>
            <label>
              월(monthOfYear)
              <input
                type="number"
                min={1}
                max={12}
                value={alarmForm.monthOfYear}
                onChange={(event) =>
                  setAlarmForm({
                    ...alarmForm,
                    monthOfYear: event.target.value,
                  })
                }
              />
            </label>
            <label className="full-width">
              조건 JSON
              <textarea
                required
                rows={8}
                value={alarmForm.conditionsJson}
                onChange={(event) =>
                  setAlarmForm({
                    ...alarmForm,
                    conditionsJson: event.target.value,
                  })
                }
              />
            </label>
            <label>
              상태 (수정 시)
              <select
                value={alarmForm.status}
                onChange={(event) =>
                  setAlarmForm({ ...alarmForm, status: event.target.value })
                }
                disabled={!isEditing}
              >
                {['ACTIVE', 'PAUSED'].map((status) => (
                  <option key={status}>{status}</option>
                ))}
              </select>
            </label>
            <button type="submit" disabled={!accessToken}>
              {isEditing ? '알람 수정' : '알람 생성'}
            </button>
          </form>
        </div>

        <div className="card">
          <h3>등록된 알람</h3>
          {alarms.length === 0 ? (
            <p className="empty">등록된 알람이 없습니다.</p>
          ) : (
            <ul className="alarm-list">
              {alarmSummary.map((alarm) => (
                <li key={alarm.id} className="alarm-item">
                  <div>
                    <strong>{alarm.title}</strong>
                    <p>{alarm.description}</p>
                    <p className="meta">
                      {alarm.target} · 다음 실행: {alarm.nextRun}
                    </p>
                    <p className="meta">상태: {alarm.status}</p>
                  </div>
                  <div className="button-row">
                    <button
                      type="button"
                      onClick={() => {
                        const found = alarms.find((item) => item.id === alarm.id)
                        if (found) {
                          handleSelectAlarm(found)
                        }
                      }}
                    >
                      수정
                    </button>
                    <button
                      type="button"
                      className="secondary"
                      onClick={() => handleRunNow(alarm.id)}
                    >
                      즉시 실행
                    </button>
                    <button
                      type="button"
                      onClick={() => handleLogs(alarm.id)}
                    >
                      로그 조회
                    </button>
                    <button
                      type="button"
                      className="danger"
                      onClick={() => handleDelete(alarm.id)}
                    >
                      삭제
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      <div className="panel-grid">
        <div className="card">
          <h3>시뮬레이션</h3>
          <form onSubmit={handleSimulationSubmit} className="form-grid">
            <label>
              알람 ID
              <input
                type="number"
                required
                value={simulationForm.alarmId}
                onChange={(event) =>
                  setSimulationForm({
                    ...simulationForm,
                    alarmId: event.target.value,
                  })
                }
              />
            </label>
            <label>
              시간대
              <input
                type="text"
                value={simulationForm.timezone}
                onChange={(event) =>
                  setSimulationForm({
                    ...simulationForm,
                    timezone: event.target.value,
                  })
                }
              />
            </label>
            <label className="checkbox">
              <input
                type="checkbox"
                checked={simulationForm.sendNotification}
                onChange={(event) =>
                  setSimulationForm({
                    ...simulationForm,
                    sendNotification: event.target.checked,
                  })
                }
              />
              Notification 발송
            </label>
            <label className="full-width">
              Weather JSON
              <textarea
                rows={4}
                value={simulationForm.weatherJson}
                onChange={(event) =>
                  setSimulationForm({
                    ...simulationForm,
                    weatherJson: event.target.value,
                  })
                }
              />
            </label>
            <label className="full-width">
              Stock JSON
              <textarea
                rows={4}
                value={simulationForm.stockJson}
                onChange={(event) =>
                  setSimulationForm({
                    ...simulationForm,
                    stockJson: event.target.value,
                  })
                }
              />
            </label>
            <label className="full-width">
              Custom JSON
              <textarea
                rows={4}
                value={simulationForm.customJson}
                onChange={(event) =>
                  setSimulationForm({
                    ...simulationForm,
                    customJson: event.target.value,
                  })
                }
              />
            </label>
            <button type="submit" disabled={!accessToken}>
              시뮬레이션 실행
            </button>
          </form>

          {simulationResult && (
            <div className="result-block">
              <p>
                조건 충족: {simulationResult.conditionsMet ? '예' : '아니오'} ·
                통지 여부: {simulationResult.notificationSent ? '전송' : '미전송'}
              </p>
              {simulationResult.message && <p>{simulationResult.message}</p>}
            </div>
          )}
        </div>

        <div className="card">
          <h3>실행 로그</h3>
          {logsAlarmId ? (
            <p className="meta">알람 #{logsAlarmId} 실행 내역</p>
          ) : (
            <p className="meta">조회된 로그가 없습니다.</p>
          )}
          {logs.length === 0 ? (
            <p className="empty">표시할 로그가 없습니다.</p>
          ) : (
            <ul className="log-list">
              {logs.map((log) => (
                <li key={log.id}>
                  <div className="log-header">
                    <span>{log.executedAt}</span>
                    <strong className={log.success ? 'success' : 'danger'}>
                      {log.success ? '성공' : '실패'}
                    </strong>
                  </div>
                  {log.message && <p>{log.message}</p>}
                  {log.payloadSnapshot && (
                    <details>
                      <summary>Payload</summary>
                      <pre>{log.payloadSnapshot}</pre>
                    </details>
                  )}
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      {(status || error) && (
        <div className={`feedback ${error ? 'error' : 'success'}`}>
          {error ?? status}
        </div>
      )}
    </section>
  )
}

export default AlarmSection
