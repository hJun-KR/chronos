import type { AuthTokens } from '../api/client'
import type { UserSummary } from '../api/types'

export interface Session {
  user: UserSummary
  tokens: AuthTokens
}
