import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Save, Trash2, ChevronRight, Bookmark, Sparkles, Plus } from 'lucide-react'
import { toast } from 'sonner'
import { getPresets, createPreset, deletePreset } from '../api/presets'
import type { ConditionPresetResponse, AlarmConditionDto } from '../api/types'

function cn(...inputs: any[]) {
  return inputs.filter(Boolean).join(' ')
}

interface ConditionPresetPanelProps {
  accessToken: string
  currentConditions: string
  onApplyPreset: (conditions: AlarmConditionDto[]) => void
}

const DEFAULT_PRESETS = [
  {
    id: -1,
    name: '평일 업무 시간대',
    description: '월-금, 09:00 ~ 18:00',
    conditionsJson: JSON.stringify([
      { conditionType: 'TIME_RANGE', operator: 'BETWEEN', fieldKey: 'hour', fieldValue: '09:00,18:00' }
    ])
  },
  {
    id: -2,
    name: '날씨: 강수 주의보',
    description: '강수 상태가 0보다 클 때',
    conditionsJson: JSON.stringify([
      { conditionType: 'WEATHER', operator: 'GT', fieldKey: 'PTY', fieldValue: '0' }
    ])
  }
]

const ConditionPresetPanel = ({ accessToken, currentConditions, onApplyPreset }: ConditionPresetPanelProps) => {
  const [userPresets, setUserPresets] = useState<ConditionPresetResponse[]>([])
  const [saveMode, setSaveMode] = useState(false)
  const [newPresetName, setNewPresetName] = useState('')

  const fetchPresets = async () => {
    try {
      const data = await getPresets(accessToken)
      setUserPresets(data)
    } catch (err) {
      console.error('Failed to fetch presets')
    }
  }

  useEffect(() => {
    if (accessToken) fetchPresets()
  }, [accessToken])

  const handleSaveCurrent = async () => {
    if (!newPresetName.trim()) return toast.error('프리셋 이름을 입력하세요.')
    try {
      // JSON 유효성 검사
      JSON.parse(currentConditions)
      
      await createPreset(accessToken, {
        name: newPresetName,
        conditionsJson: currentConditions
      })
      toast.success('나만의 프리셋이 저장되었습니다!')
      setNewPresetName('')
      setSaveMode(false)
      fetchPresets()
    } catch (err) {
      toast.error('현재 조건 설정이 올바른 JSON 형식이 아닙니다.')
    }
  }

  const handleDelete = async (e: React.MouseEvent, id: number) => {
    e.stopPropagation()
    try {
      await deletePreset(accessToken, id)
      toast.success('프리셋이 삭제되었습니다.')
      fetchPresets()
    } catch (err) {
      toast.error('삭제 실패')
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h4 className="text-sm font-bold text-zinc-400 flex items-center gap-2">
          <Bookmark size={16} /> 조건 라이브러리
        </h4>
        <button 
          type="button" 
          className={cn(
            "text-xs px-3 py-1.5 rounded-full font-bold transition-all flex items-center gap-1",
            saveMode ? "bg-zinc-800 text-zinc-400" : "bg-blue-600/10 text-blue-500 hover:bg-blue-600/20"
          )}
          onClick={() => setSaveMode(!saveMode)}
        >
          {saveMode ? '취소' : <><Plus size={12} /> 현재 구성 저장</>}
        </button>
      </div>

      <AnimatePresence>
        {saveMode && (
          <motion.div 
            initial={{ height: 0, opacity: 0, scale: 0.95 }}
            animate={{ height: 'auto', opacity: 1, scale: 1 }}
            exit={{ height: 0, opacity: 0, scale: 0.95 }}
            className="overflow-hidden"
          >
            <div className="bg-blue-600/5 border border-blue-500/20 p-4 rounded-2xl space-y-3 mb-4">
              <p className="text-xs text-blue-400 font-medium">현재 작성한 조건을 프리셋으로 만듭니다.</p>
              <div className="flex gap-2">
                <input 
                  type="text" 
                  placeholder="프리셋 이름"
                  className="flex-1 bg-zinc-950 border-zinc-800 text-sm py-2 px-3 rounded-xl text-white focus:border-blue-500"
                  value={newPresetName}
                  onChange={e => setNewPresetName(e.target.value)}
                  onKeyDown={e => e.key === 'Enter' && handleSaveCurrent()}
                />
                <button 
                  type="button" 
                  className="bg-blue-600 hover:bg-blue-500 text-white text-xs px-4 rounded-xl font-bold shadow-lg shadow-blue-600/20"
                  onClick={handleSaveCurrent}
                >
                  저장
                </button>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      <div className="space-y-3">
        {/* System Presets */}
        <div className="space-y-2">
          <p className="text-[10px] font-black text-zinc-600 uppercase tracking-widest pl-1">시스템 추천</p>
          {DEFAULT_PRESETS.map((preset) => (
            <PresetItem 
              key={preset.id} 
              preset={preset} 
              isSystem 
              onApply={() => onApplyPreset(JSON.parse(preset.conditionsJson))} 
            />
          ))}
        </div>

        {/* User Presets */}
        {userPresets.length > 0 && (
          <div className="space-y-2 pt-2">
            <p className="text-[10px] font-black text-blue-900 uppercase tracking-widest pl-1">나만의 프리셋</p>
            {userPresets.map((preset) => (
              <PresetItem 
                key={preset.id} 
                preset={preset} 
                onApply={() => onApplyPreset(JSON.parse(preset.conditionsJson))}
                onDelete={(e) => handleDelete(e, preset.id)}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

const PresetItem = ({ preset, isSystem, onApply, onDelete }: any) => (
  <button
    type="button"
    className="group w-full flex items-center justify-between p-3.5 bg-zinc-900/40 border border-zinc-800/50 hover:border-blue-500/30 hover:bg-blue-500/5 rounded-2xl transition-all text-left"
    onClick={onApply}
  >
    <div className="flex items-center gap-3">
      <div className={cn(
        "p-2.5 rounded-xl transition-colors", 
        isSystem ? "bg-zinc-800 text-zinc-500 group-hover:text-zinc-300" : "bg-blue-500/10 text-blue-500 group-hover:bg-blue-500/20"
      )}>
        {isSystem ? <Bookmark size={14} /> : <Sparkles size={14} />}
      </div>
      <div>
        <strong className="block text-sm text-zinc-200 group-hover:text-white">{preset.name}</strong>
        <span className="text-[11px] text-zinc-500 leading-none">{preset.description || '커스텀 조건 구성'}</span>
      </div>
    </div>
    <div className="flex items-center gap-2">
      {!isSystem && onDelete && (
        <button 
          type="button" 
          className="p-2 text-zinc-700 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-all hover:bg-red-500/10 rounded-lg"
          onClick={onDelete}
        >
          <Trash2 size={14} />
        </button>
      )}
      <ChevronRight size={14} className="text-zinc-800 group-hover:text-blue-500 transition-transform group-hover:translate-x-0.5" />
    </div>
  </button>
)

export default ConditionPresetPanel
