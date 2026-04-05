import { useCallback, useState } from 'react'
import { motion } from 'framer-motion'
import { Activity, LogOut } from 'lucide-react'
import { ThemeProvider } from 'styled-components'
import { theme, GlobalStyle } from './styles/theme'
import OnboardingSection from './components/OnboardingSection'
import Dashboard from './components/Dashboard'
import type { Session } from './types/session'
import { logoutUser } from './api/auth'

const STORAGE_KEY = 'chronos.session'

const loadInitialSession = (): Session | null => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return null
    return JSON.parse(raw) as Session
  } catch {
    return null
  }
}

import styled from 'styled-components'

const Nav = styled.nav`
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 3rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  background: rgba(2, 6, 23, 0.8);
  backdrop-filter: blur(16px);
  position: sticky;
  top: 0;
  z-index: 100;
`;

const Logo = styled.div`
  font-size: 1.5rem;
  font-weight: 900;
  letter-spacing: -0.05em;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background: linear-gradient(to right, #fff, #6366f1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
`;

const UserInfo = styled.div`
  display: flex;
  align-items: center;
  gap: 1.5rem;
  
  span {
    font-size: 0.75rem;
    font-weight: 700;
    color: #64748b;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }
`;

const MainContainer = styled.main`
  flex: 1;
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
  padding: 4rem 2rem;
`;

function App() {
  const [session, setSession] = useState<Session | null>(loadInitialSession)

  const persistSession = useCallback((next: Session | null) => {
    setSession(next)
    if (next) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(next))
    } else {
      localStorage.removeItem(STORAGE_KEY)
    }
  }, [])

  const handleAuthSuccess = useCallback((payload: Session) => {
    persistSession(payload)
  }, [persistSession])

  const handleTokensUpdate = useCallback((tokens: AuthTokens) => {
    if (!session) return
    persistSession({ ...session, tokens })
  }, [persistSession, session])

  const handleLogout = useCallback(async () => {
    if (session?.tokens.refreshToken) {
      try {
        await logoutUser({ refreshToken: session.tokens.refreshToken })
      } catch (err) {
        console.warn('Logout error:', err)
      }
    }
    persistSession(null)
  }, [persistSession, session])

  return (
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <div className="app-shell">
        <Nav>
          <Logo>
            <Activity size={28} className="text-indigo-500" /> CHRONOS
          </Logo>
          {session && (
            <UserInfo>
              <span>{session.user.email}</span>
              <button 
                style={{ padding: '0.5rem', color: '#64748b', cursor: 'pointer' }} 
                onClick={handleLogout}
              >
                <LogOut size={20} />
              </button>
            </UserInfo>
          )}
        </Nav>

        <MainContainer>
          {session ? (
            <Dashboard
              session={session}
              onUpdateTokens={handleTokensUpdate}
              onLogout={handleLogout}
            />
          ) : (
            <OnboardingSection onAuthSuccess={handleAuthSuccess} />
          )}
        </MainContainer>
      </div>
    </ThemeProvider>
  )
}

export default App
