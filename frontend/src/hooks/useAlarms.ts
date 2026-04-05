import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import {
  createAlarm,
  deleteAlarm,
  getAlarms,
  runAlarmNow,
  updateAlarm,
} from '../api/alarms'
import type { AlarmCreateRequest, AlarmUpdatePayload } from '../api/types'
import { getErrorMessage } from '../api/client'

export const useAlarms = (accessToken: string) => {
  const queryClient = useQueryClient()

  const { data: alarms = [], isLoading, refetch } = useQuery({
    queryKey: ['alarms', accessToken],
    queryFn: () => getAlarms(accessToken),
    enabled: !!accessToken,
  })

  const createMutation = useMutation({
    mutationFn: (payload: AlarmCreateRequest) => createAlarm(accessToken, payload),
    onSuccess: () => {
      toast.success('새 알람이 등록되었습니다.')
      queryClient.invalidateQueries({ queryKey: ['alarms'] })
    },
    onError: (err) => toast.error(getErrorMessage(err)),
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: AlarmUpdatePayload }) =>
      updateAlarm(accessToken, id, payload),
    onSuccess: () => {
      toast.success('알람이 업데이트되었습니다.')
      queryClient.invalidateQueries({ queryKey: ['alarms'] })
    },
    onError: (err) => toast.error(getErrorMessage(err)),
  })

  const deleteMutation = useMutation({
    mutationFn: (id: number) => deleteAlarm(accessToken, id),
    onSuccess: () => {
      toast.success('알람이 삭제되었습니다.')
      queryClient.invalidateQueries({ queryKey: ['alarms'] })
    },
    onError: (err) => toast.error(getErrorMessage(err)),
  })

  const runNowMutation = useMutation({
    mutationFn: (id: number) => runAlarmNow(accessToken, id),
    onSuccess: () => toast.success('즉시 실행이 요청되었습니다.'),
    onError: (err) => toast.error(getErrorMessage(err)),
  })

  return {
    alarms,
    isLoading,
    refetch,
    createAlarm: createMutation.mutateAsync,
    updateAlarm: updateMutation.mutateAsync,
    deleteAlarm: deleteMutation.mutateAsync,
    runAlarmNow: runNowMutation.mutateAsync,
    isProcessing:
      createMutation.isPending ||
      updateMutation.isPending ||
      deleteMutation.isPending ||
      runNowMutation.isPending,
  }
}
