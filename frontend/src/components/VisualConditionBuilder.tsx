import { useState, useEffect } from 'react'
import { Cloud, TrendingUp, Clock, Trash2, Globe, Building2 } from 'lucide-react'
import styled from 'styled-components'

const ConditionCard = styled.div`
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 20px;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1rem;
  transition: all 0.2s;
  &:hover { border-color: rgba(99, 102, 241, 0.3); }
`;

const InputRow = styled.div`
  display: flex;
  gap: 0.75rem;
  align-items: center;
`;

const MiniLabel = styled.span`
  font-size: 10px;
  font-weight: 800;
  color: #64748b;
  text-transform: uppercase;
  margin-bottom: 4px;
  display: block;
`;

export const VisualConditionBuilder = ({ value, onChange }: { value: string, onChange: (json: string) => void }) => {
  const [conditions, setConditions] = useState<any[]>([])

  useEffect(() => {
    try {
      const parsed = JSON.parse(value)
      if (Array.isArray(parsed)) setConditions(parsed)
    } catch { setConditions([]) }
  }, [value])

  const update = (newConds: any[]) => {
    setConditions(newConds)
    onChange(JSON.stringify(newConds))
  }

  const add = (type: string) => {
    const fresh = {
      conditionType: type,
      operator: 'GT',
      fieldKey: type === 'WEATHER' ? 'T1H' : type === 'STOCK' ? 'price' : 'hour',
      fieldValue: '',
      extraJson: type === 'STOCK' ? JSON.stringify({ market: 'NASDAQ', symbol: 'AAPL' }) : null
    }
    update([...conditions, fresh])
  }

  const updateStockExtra = (index: number, field: string, val: string) => {
    const next = [...conditions]
    const extra = JSON.parse(next[index].extraJson || '{}')
    extra[field] = val
    next[index].extraJson = JSON.stringify(extra)
    update(next)
  }

  return (
    <div style={{ marginTop: '1.5rem' }}>
      <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.5rem' }}>
        {[
          { id: 'STOCK', label: '주식 시세', icon: TrendingUp, color: '#10b981' },
          { id: 'WEATHER', label: '날씨 정보', icon: Cloud, color: '#3b82f6' },
          { id: 'TIME_RANGE', label: '시간 제한', icon: Clock, color: '#6366f1' },
        ].map(t => (
          <button 
            key={t.id} type="button" 
            onClick={() => add(t.id)}
            style={{ flex: 1, padding: '1.25rem', background: '#1e293b', border: `1px solid rgba(255,255,255,0.05)`, borderRadius: '20px', fontSize: '0.8125rem', fontWeight: 700, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '0.75rem', transition: 'all 0.2s' }}
          >
            <t.icon size={24} style={{ color: t.color }} />
            {t.label}
          </button>
        ))}
      </div>

      {conditions.map((c, i) => (
        <ConditionCard key={i}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.875rem', fontWeight: 800, color: '#fff' }}>
              {c.conditionType === 'STOCK' ? <TrendingUp size={16} className="text-emerald-500" /> : c.conditionType === 'WEATHER' ? <Cloud size={16} className="text-blue-500" /> : <Clock size={16} className="text-indigo-500" />}
              {c.conditionType === 'STOCK' ? '주식 조건' : c.conditionType === 'WEATHER' ? '날씨 조건' : '시간 조건'}
            </div>
            <button type="button" onClick={() => update(conditions.filter((_, idx) => idx !== i))} style={{ color: '#ef4444' }}>
              <Trash2 size={16} />
            </button>
          </div>

          {c.conditionType === 'STOCK' && (
            <div style={{ background: 'rgba(255,255,255,0.02)', padding: '1rem', borderRadius: '16px', border: '1px solid rgba(255,255,255,0.05)' }}>
              <InputRow>
                <div style={{ flex: 1 }}>
                  <MiniLabel>시장 선택</MiniLabel>
                  <select 
                    style={{ width: '100%' }}
                    value={JSON.parse(c.extraJson || '{}').market || 'NASDAQ'}
                    onChange={e => updateStockExtra(i, 'market', e.target.value)}
                  >
                    <option value="NASDAQ">NASDAQ (미국)</option>
                    <option value="KOSDAQ">KOSDAQ (한국)</option>
                    <option value="KOSPI">KOSPI (한국)</option>
                  </select>
                </div>
                <div style={{ flex: 1 }}>
                  <MiniLabel>종목 코드 (Symbol)</MiniLabel>
                  <input 
                    style={{ width: '100%' }}
                    value={JSON.parse(c.extraJson || '{}').symbol || ''}
                    onChange={e => updateStockExtra(i, 'symbol', e.target.value.toUpperCase())}
                    placeholder="예: AAPL, 035720"
                  />
                </div>
              </InputRow>
            </div>
          )}

          <InputRow>
            <div style={{ flex: 1 }}>
              <MiniLabel>대상 필드</MiniLabel>
              <select 
                style={{ width: '100%' }}
                value={c.fieldKey}
                onChange={e => { const n = [...conditions]; n[i].fieldKey = e.target.value; update(n); }}
              >
                {c.conditionType === 'STOCK' && <><option value="price">현재가</option><option value="change_rate">등락률(%)</option></>}
                {c.conditionType === 'WEATHER' && <><option value="T1H">기온</option><option value="PTY">강수형태</option></>}
                {c.conditionType === 'TIME_RANGE' && <option value="hour">작동 시간</option>}
              </select>
            </div>
            <div style={{ width: '100px' }}>
              <MiniLabel>비교</MiniLabel>
              <select 
                style={{ width: '100%' }}
                value={c.operator}
                onChange={e => { const n = [...conditions]; n[i].operator = e.target.value; update(n); }}
              >
                <option value="GT">이상</option><option value="LT">이하</option><option value="EQ">일치</option>
              </select>
            </div>
            <div style={{ flex: 1 }}>
              <MiniLabel>기준값</MiniLabel>
              <input 
                style={{ width: '100%' }}
                value={c.fieldValue}
                onChange={e => { const n = [...conditions]; n[i].fieldValue = e.target.value; update(n); }}
                placeholder="숫자 입력"
              />
            </div>
          </InputRow>
        </ConditionCard>
      ))}
    </div>
  )
}
