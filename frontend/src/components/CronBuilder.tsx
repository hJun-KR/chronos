import { useState, useEffect } from 'react'
import { Clock, CalendarDays, RotateCcw } from 'lucide-react'
import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

interface CronBuilderProps {
  value: string
  onChange: (cron: string) => void
}

type Frequency = 'DAILY' | 'WEEKLY' | 'MONTHLY'

const DAYS_OF_WEEK = [
  { label: '일', value: 'SUN' },
  { label: '월', value: 'MON' },
  { label: '화', value: 'TUE' },
  { label: '수', value: 'WED' },
  { label: '목', value: 'THU' },
  { label: '금', value: 'FRI' },
  { label: '토', value: 'SAT' },
]

export const CronBuilder = ({ value, onChange }: CronBuilderProps) => {
  const [frequency, setFrequency] = useState<Frequency>('DAILY')
  const [time, setTime] = useState('09:00')
  const [selectedDays, setSelectedDays] = useState<string[]>(['MON', 'TUE', 'WED', 'THU', 'FRI'])
  const [dayOfMonth, setDayOfMonth] = useState('1')

  // UI 상태로부터 CRON 표현식 생성 (초 분 시 일 월 요일)
  useEffect(() => {
    const [hour, minute] = time.split(':')
    let cron = `0 ${minute} ${hour} `

    if (frequency === 'DAILY') {
      cron += '* * *'
    } else if (frequency === 'WEEKLY') {
      cron += `* * ${selectedDays.length ? selectedDays.join(',') : '*'}`
    } else if (frequency === 'MONTHLY') {
      cron += `${dayOfMonth} * *`
    }

    if (cron !== value) {
      onChange(cron)
    }
  }, [frequency, time, selectedDays, dayOfMonth, onChange, value])

  const toggleDay = (day: string) => {
    setSelectedDays(prev => 
      prev.includes(day) ? prev.filter(d => d !== day) : [...prev, day]
    )
  }

  return (
    <div className="bg-zinc-900/50 p-4 rounded-xl border border-zinc-800 space-y-4">
      <div className="flex gap-2">
        {(['DAILY', 'WEEKLY', 'MONTHLY'] as Frequency[]).map(f => (
          <button
            key={f}
            type="button"
            className={cn(
              "flex-1 py-2 text-xs font-bold rounded-lg border transition-all",
              frequency === f 
                ? "bg-blue-600 border-blue-500 text-white" 
                : "bg-zinc-800 border-zinc-700 text-zinc-400 hover:text-white"
            )}
            onClick={() => setFrequency(f)}
          >
            {f === 'DAILY' ? '매일' : f === 'WEEKLY' ? '매주' : '매월'}
          </button>
        ))}
      </div>

      <div className="flex items-center gap-4">
        <label className="flex-1">
          <span className="text-xs text-zinc-500 flex items-center gap-1 mb-1">
            <Clock size={12} /> 실행 시간
          </span>
          <input
            type="time"
            value={time}
            onChange={e => setTime(e.target.value)}
            className="w-full bg-zinc-800 border-zinc-700 rounded-lg text-sm"
          />
        </label>

        {frequency === 'MONTHLY' && (
          <label className="flex-1">
            <span className="text-xs text-zinc-500 flex items-center gap-1 mb-1">
              <CalendarDays size={12} /> 실행 날짜 (일)
            </span>
            <input
              type="number"
              min="1"
              max="31"
              value={dayOfMonth}
              onChange={e => setDayOfMonth(e.target.value)}
              className="w-full bg-zinc-800 border-zinc-700 rounded-lg text-sm"
            />
          </label>
        )}
      </div>

      {frequency === 'WEEKLY' && (
        <div className="space-y-2">
          <span className="text-xs text-zinc-500 block">실행 요일 선택</span>
          <div className="flex justify-between">
            {DAYS_OF_WEEK.map(day => (
              <button
                key={day.value}
                type="button"
                onClick={() => toggleDay(day.value)}
                className={cn(
                  "w-9 h-9 rounded-full text-xs font-bold border transition-all",
                  selectedDays.includes(day.value)
                    ? "bg-blue-600/20 border-blue-500 text-blue-500"
                    : "bg-zinc-800 border-zinc-700 text-zinc-500 hover:border-zinc-500"
                )}
              >
                {day.label}
              </button>
            ))}
          </div>
        </div>
      )}

      <div className="pt-3 border-t border-zinc-800 flex items-center justify-between">
        <div className="flex items-center gap-2 text-zinc-500">
          <RotateCcw size={14} />
          <span className="text-[10px] font-mono uppercase tracking-wider">Generated CRON</span>
        </div>
        <code className="text-blue-400 font-mono text-xs font-bold bg-blue-500/10 px-2 py-1 rounded">
          {value || '0 0 9 * * *'}
        </code>
      </div>
    </div>
  )
}
