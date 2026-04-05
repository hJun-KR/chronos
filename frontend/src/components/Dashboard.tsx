import { useCallback, useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import styled from 'styled-components'
import { 
  Bell, Plus, Trash2, Play, Settings2, Clock, LogOut, 
  RefreshCcw, Zap, Send, ArrowRight, Activity, Layers, 
  Mail, MessageSquare, Info
} from 'lucide-react'
import { toast } from 'sonner'
import { getAlarmLogs, simulateAlarm } from '../api/alarms'
import type { AlarmExecutionLog, AlarmResponse, AlarmSimulationResponse } from '../api/types'
import { useAlarms } from '../hooks/useAlarms'
import { CronBuilder } from './CronBuilder'
import { VisualConditionBuilder } from './VisualConditionBuilder'
import ConditionPresetPanel from './ConditionPresetPanel'
import type { Session } from '../types/session'
import { Card, Button, Input, Select, Label, Badge } from '../styles/components'

const SummaryBar = styled.div`
  background: rgba(99, 102, 241, 0.1);
  border: 1px solid rgba(99, 102, 241, 0.2);
  padding: 1.5rem;
  border-radius: 24px;
  margin-bottom: 2.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  color: #a5b4fc;
  font-size: 0.9375rem;
  line-height: 1.5;
`;

const FormGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  margin-bottom: 2rem;
`;

const Dashboard = ({ session, onLogout }: { session: Session, onUpdateTokens: any, onLogout: any }) => {
  const { alarms, isLoading, refetch, createAlarm, updateAlarm, deleteAlarm, runAlarmNow, isProcessing } = useAlarms(session.tokens.accessToken)

  const initialForm = {
    name: '',
    alarmType: 'TIME' as any,
    scheduleType: 'RECURRING' as any,
    cronExpression: '0 0 9 * * *',
    runAt: '',
    timezone: 'Asia/Seoul',
    channel: 'EMAIL' as any,
    targetAddress: '',
    conditionsJson: '[]',
    status: 'ACTIVE' as any,
  }

  const [form, setForm] = useState(initialForm)
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [simulationResult, setSimulationResult] = useState<AlarmSimulationResponse | null>(null)
  const [view, setView] = useState<'list' | 'editor'>('list')

  const isEditing = selectedId !== null

  // 실시간 문장 요약 로직
  const summaryMessage = useMemo(() => {
    const timeText = form.scheduleType === 'RECURRING' ? '주기적으로' : '지정한 시간에 한 번';
    const channelText = form.channel === 'EMAIL' ? '이메일로' : '디스코드로';
    const conditions = JSON.parse(form.conditionsJson);
    const condCount = conditions.length;
    
    if (!form.name || !form.targetAddress) return "비서의 이름과 연락처를 먼저 알려주세요.";
    return `[${form.name}] 비서가 ${timeText} 상황을 확인하고, ${condCount > 0 ? condCount + '개의 조건이 맞으면' : '즉시'} ${form.targetAddress} (${channelText}) 보고를 올릴게요.`;
  }, [form])

  const handleEdit = (alarm: AlarmResponse) => {
    setForm({
      ...alarm,
      cronExpression: alarm.cronExpression || '0 0 9 * * *',
      runAt: alarm.runAt ? alarm.runAt.slice(0, 16) : '',
      conditionsJson: JSON.stringify(alarm.conditions),
    } as any)
    setSelectedId(alarm.id)
    setView('editor')
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      const conditions = JSON.parse(form.conditionsJson);
      
      // 자동 데이터 타입 결정
      let inferredType = 'TIME';
      if (conditions.some((c: any) => c.conditionType === 'WEATHER')) inferredType = 'WEATHER';
      else if (conditions.some((c: any) => c.conditionType === 'STOCK')) inferredType = 'STOCK';

      const payload = {
        ...form,
        alarmType: inferredType,
        conditions,
        runAt: form.scheduleType === 'ONCE' && form.runAt ? `${form.runAt}:00` : undefined,
        cronExpression: form.scheduleType === 'RECURRING' ? form.cronExpression : undefined,
      }

      if (isEditing && selectedId) {
        await updateAlarm({ id: selectedId, payload: payload as any })
        toast.success('비서의 업무 매뉴얼을 수정했습니다.')
      } else {
        await createAlarm(payload as any)
        toast.success('새로운 비서가 고용되었습니다!')
      }
      
      setView('list')
      setSelectedId(null)
      setForm(initialForm)
    } catch (err: any) {
      toast.error('비서 고용에 실패했습니다. 입력값을 확인해주세요.')
    }
  }

  return (
    <AnimatePresence mode="wait">
      <motion.div key={view} initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -20 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginBottom: '3rem' }}>
          <div>
            <h1 style={{ fontSize: '2.5rem', fontWeight: 800, letterSpacing: '-0.04em', margin: 0 }}>안녕하세요, {session.user.name}님</h1>
            <p style={{ color: '#94a3b8', fontSize: '1.125rem', marginTop: '0.5rem' }}>당신의 업무를 자동화할 {alarms.length}명의 비서가 대기 중입니다.</p>
          </div>
          <Button variant="primary" style={{ padding: '1rem 2rem', borderRadius: '16px' }} onClick={() => { setView(view === 'list' ? 'editor' : 'list'); setSelectedId(null); setForm(initialForm); }}>
            {view === 'list' ? <><Plus size={20} /> 새로운 비서 고용</> : '업무 리스트로 가기'}
          </Button>
        </div>

        <AnimatePresence mode="wait">
          {view === 'editor' ? (
            <motion.div key="editor" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -20 }}>
              <SummaryBar>
                <Info size={24} className="text-indigo-400" />
                <strong>지시 요약:</strong> {summaryMessage}
              </SummaryBar>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 350px', gap: '2rem', alignItems: 'start' }}>
                <Card style={{ padding: '3rem' }}>
                  <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '3rem' }}>
                    <section>
                      <h3 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                        <User size={20} className="text-indigo-500" /> 1. 비서의 기본 정보
                      </h3>
                      <FormGrid>
                        <div className="input-group">
                          <Label>비서 이름 (나만 아는 이름)</Label>
                          <Input value={form.name} onChange={e => setForm({...form, name: e.target.value})} placeholder="예: 주식 알림 비서" required />
                        </div>
                        <div className="input-group">
                          <Label>보고 받을 연락처</Label>
                          <Input value={form.targetAddress} onChange={e => setForm({...form, targetAddress: e.target.value})} placeholder="이메일 주소 또는 URL" required />
                        </div>
                      </FormGrid>
                      <div className="input-group">
                        <Label>보고 채널 선택</Label>
                        <div style={{ display: 'flex', gap: '1rem' }}>
                          <Button type="button" variant={form.channel === 'EMAIL' ? 'primary' : 'secondary'} style={{ flex: 1 }} onClick={() => setForm({...form, channel: 'EMAIL'})}><Mail size={16} /> 이메일</Button>
                          <Button type="button" variant={form.channel === 'DISCORD' ? 'primary' : 'secondary'} style={{ flex: 1 }} onClick={() => setForm({...form, channel: 'DISCORD'})}><MessageSquare size={16} /> 디스코드</Button>
                        </div>
                      </div>
                    </section>

                    <section style={{ padding: '2rem', background: 'rgba(0,0,0,0.2)', borderRadius: '32px', border: '1px solid rgba(255,255,255,0.03)' }}>
                      <h3 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                        <Clock size={20} className="text-indigo-500" /> 2. 업무 확인 시간
                      </h3>
                      <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem' }}>
                        <button type="button" onClick={() => setForm({...form, scheduleType: 'RECURRING'})} style={{ flex: 1, padding: '1rem', borderRadius: '16px', border: '1px solid', borderColor: form.scheduleType === 'RECURRING' ? '#6366f1' : 'transparent', background: form.scheduleType === 'RECURRING' ? 'rgba(99,102,241,0.1)' : '#1e293b', color: form.scheduleType === 'RECURRING' ? '#fff' : '#94a3b8', fontWeight: 700 }}>반복적으로 확인</button>
                        <button type="button" onClick={() => setForm({...form, scheduleType: 'ONCE'})} style={{ flex: 1, padding: '1rem', borderRadius: '16px', border: '1px solid', borderColor: form.scheduleType === 'ONCE' ? '#6366f1' : 'transparent', background: form.scheduleType === 'ONCE' ? 'rgba(99,102,241,0.1)' : '#1e293b', color: form.scheduleType === 'ONCE' ? '#fff' : '#94a3b8', fontWeight: 700 }}>한 번만 확인</button>
                      </div>
                      {form.scheduleType === 'RECURRING' ? (
                        <CronBuilder value={form.cronExpression} onChange={cron => setForm({...form, cronExpression: cron})} />
                      ) : (
                        <Input type="datetime-local" value={form.runAt} onChange={e => setForm({...form, runAt: e.target.value})} />
                      )}
                    </section>

                    <section>
                      <h3 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                        <Layers size={20} className="text-indigo-500" /> 3. 보고 조건 (비어있으면 무조건 보고)
                      </h3>
                      <VisualConditionBuilder value={form.conditionsJson} onChange={json => setForm({...form, conditionsJson: json})} />
                    </section>

                    <Button variant="primary" style={{ width: '100%', padding: '1.5rem', fontSize: '1.25rem', borderRadius: '24px' }} type="submit" disabled={isProcessing}>
                      {isProcessing ? '비서가 준비 중...' : (isEditing ? '매뉴얼 수정 완료' : '고용 계약 완료')} <ArrowRight size={20} />
                    </Button>
                  </form>
                </Card>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
                  <Card style={{ padding: '2rem' }}>
                    <ConditionPresetPanel accessToken={session.tokens.accessToken} currentConditions={form.conditionsJson} onApplyPreset={conds => setForm({...form, conditionsJson: JSON.stringify(conds)})} />
                  </Card>
                  {isEditing && (
                    <Card style={{ background: 'rgba(245, 158, 11, 0.03)', borderColor: 'rgba(245, 158, 11, 0.1)' }}>
                      <h4 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#f59e0b', margin: '0 0 1rem' }}><Zap size={18} /> 테스트 모드</h4>
                      <Button variant="secondary" style={{ width: '100%', color: '#f59e0b' }} onClick={async () => {
                        const res = await simulateAlarm(session.tokens.accessToken, selectedId!, { weatherData: {}, stockData: {}, customData: {}, timezone: 'Asia/Seoul', sendNotification: false })
                        setSimulationResult(res)
                      }}>가상 보고 받기</Button>
                      {simulationResult && <div style={{ marginTop: '1rem', fontSize: '0.75rem', color: '#94a3b8', background: '#000', padding: '1rem', borderRadius: '12px' }}>{simulationResult.message}</div>}
                    </Card>
                  )}
                </div>
              </div>
            </motion.div>
          ) : (
            <motion.div key="list" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))', gap: '1.5rem' }}>
              {alarms.map(alarm => (
                <Card key={alarm.id} style={{ padding: '1.5rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1.5rem' }}>
                    <div style={{ padding: '0.75rem', background: 'rgba(99, 102, 241, 0.1)', borderRadius: '16px', color: '#6366f1' }}><Activity size={24} /></div>
                    <Badge variant={alarm.status === 'ACTIVE' ? 'success' : 'info'}>{alarm.status === 'ACTIVE' ? '작동 중' : '휴식 중'}</Badge>
                  </div>
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '0.5rem' }}>{alarm.name}</h3>
                  <p style={{ fontSize: '0.875rem', color: '#64748b', marginBottom: '2rem' }}>{alarm.targetAddress}</p>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <Button variant="secondary" style={{ flex: 1 }} onClick={() => handleEdit(alarm)}><Settings2 size={14} /> 관리</Button>
                    <Button variant="secondary" onClick={() => runAlarmNow(alarm.id)}><Play size={14} /></Button>
                    <Button variant="danger" onClick={() => deleteAlarm(alarm.id)}><Trash2 size={14} /></Button>
                  </div>
                </Card>
              ))}
              {alarms.length === 0 && (
                <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '8rem 0', opacity: 0.2 }}>
                  <Bell size={64} style={{ margin: '0 auto 1.5rem' }} />
                  <p style={{ fontSize: '1.25rem', fontWeight: 600 }}>아직 비서를 고용하지 않았습니다.</p>
                </div>
              )}
            </motion.div>
          )}
        </AnimatePresence>
      </motion.div>
    </AnimatePresence>
  )
}

const User = ({ size, className }: { size: number, className?: string }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" className={className}>
    <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" /><circle cx="12" cy="7" r="4" />
  </svg>
)

export default Dashboard
