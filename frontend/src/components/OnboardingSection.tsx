import { useState } from 'react'
import type { FormEvent } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import styled from 'styled-components'
import { 
  UserPlus, LogIn, MailCheck, ShieldCheck, 
  ArrowRight, Mail, User as UserIcon,
  Sparkles, Activity, Key
} from 'lucide-react'
import { toast } from 'sonner'
import { registerUser, loginUser } from '../api/auth'
import { sendVerificationCode, verifyEmailCode } from '../api/email'
import { getErrorMessage } from '../api/client'
import type { AuthTokens } from '../api/client'
import type { TokenResponse } from '../api/types'
import type { Session } from '../types/session'
import { Card, Button, Input, Label } from '../styles/components'

const Container = styled.div`
  max-width: 440px;
  margin: 5rem auto;
`;

const Header = styled.div`
  text-align: center;
  margin-bottom: 3rem;
`;

const IconWrapper = styled(motion.div)`
  display: inline-flex;
  padding: 1.25rem;
  background: rgba(99, 102, 241, 0.1);
  color: ${props => props.theme.colors.accent};
  border-radius: 24px;
  margin-bottom: 1.5rem;
  box-shadow: ${props => props.theme.shadows.glow};
`;

const TabContainer = styled.div`
  display: flex;
  gap: 0.25rem;
  padding: 0.25rem;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 16px;
  margin-bottom: 2.5rem;
`;

const Tab = styled.button<{ active: boolean }>`
  flex: 1;
  padding: 0.625rem;
  font-size: 0.75rem;
  font-weight: 700;
  border-radius: 12px;
  transition: all 0.2s;
  color: ${props => props.active ? 'white' : props.theme.colors.textMuted};
  background: ${props => props.active ? props.theme.colors.accent : 'transparent'};
  box-shadow: ${props => props.active ? '0 4px 12px rgba(99, 102, 241, 0.2)' : 'none'};
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`;

const toAuthTokens = (token: TokenResponse): AuthTokens => ({
  accessToken: token.accessToken,
  refreshToken: token.refreshToken,
  expiresIn: token.expiresIn,
})

type AuthMode = 'login' | 'signup' | 'verify'

const OnboardingSection = ({ onAuthSuccess }: { onAuthSuccess: (session: Session) => void }) => {
  const [mode, setMode] = useState<AuthMode>('login')
  const [signUpForm, setSignUpForm] = useState({ email: '', password: '', name: '' })
  const [loginForm, setLoginForm] = useState({ email: '', password: '' })
  const [verificationForm, setVerificationForm] = useState({ email: '', code: '' })
  const [isLoading, setIsLoading] = useState(false)

  const handleRegister = async (event: FormEvent) => {
    event.preventDefault()
    setIsLoading(true)
    try {
      await registerUser(signUpForm)
      toast.success('회원가입 성공! 인증 코드를 확인하세요.')
      setVerificationForm(prev => ({ ...prev, email: signUpForm.email }))
      await sendVerificationCode({ email: signUpForm.email })
      setMode('verify')
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setIsLoading(false)
    }
  }

  const handleLogin = async (event: FormEvent) => {
    event.preventDefault()
    setIsLoading(true)
    try {
      const response = await loginUser(loginForm)
      onAuthSuccess({
        user: response.user,
        tokens: toAuthTokens(response.token),
      })
      toast.success(`${response.user.name}님, 환영합니다!`)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setIsLoading(false)
    }
  }

  const handleVerify = async (event: FormEvent) => {
    event.preventDefault()
    setIsLoading(true)
    try {
      await verifyEmailCode(verificationForm)
      toast.success('이메일 인증이 완료되었습니다.')
      setMode('login')
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Container>
      <Header>
        <IconWrapper initial={{ scale: 0.8, opacity: 0 }} animate={{ scale: 1, opacity: 1 }}>
          <Activity size={40} />
        </IconWrapper>
        <h2 style={{ fontSize: '2.5rem', fontWeight: 900, letterSpacing: '-0.05em', marginBottom: '0.5rem' }}>CHRONOS</h2>
        <p style={{ color: '#94a3b8', fontWeight: 500 }}>나만의 지능형 자동화 비서 시스템</p>
      </Header>

      <Card style={{ padding: '2.5rem' }}>
        <TabContainer>
          {(['login', 'signup', 'verify'] as AuthMode[]).map(m => (
            <Tab key={m} active={mode === m} onClick={() => setMode(m)}>
              {m === 'login' ? '로그인' : m === 'signup' ? '가입하기' : '인증'}
            </Tab>
          ))}
        </TabContainer>

        <AnimatePresence mode="wait">
          <motion.div
            key={mode}
            initial={{ y: 10, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            exit={{ y: -10, opacity: 0 }}
            transition={{ duration: 0.2 }}
          >
            {mode === 'login' && (
              <Form onSubmit={handleLogin}>
                <div>
                  <Label>이메일 주소</Label>
                  <Input type="email" required value={loginForm.email} onChange={e => setLoginForm({...loginForm, email: e.target.value})} placeholder="name@example.com" />
                </div>
                <div>
                  <Label>비밀번호</Label>
                  <Input type="password" required value={loginForm.password} onChange={e => setLoginForm({...loginForm, password: e.target.value})} placeholder="••••••••" />
                </div>
                <Button variant="primary" type="submit" style={{ marginTop: '1rem', width: '100%', padding: '1rem' }} disabled={isLoading}>
                  로그인 <ArrowRight size={18} />
                </Button>
              </Form>
            )}

            {mode === 'signup' && (
              <Form onSubmit={handleRegister}>
                <div>
                  <Label>사용자 이름</Label>
                  <Input type="text" required value={signUpForm.name} onChange={e => setSignUpForm({...signUpForm, name: e.target.value})} placeholder="홍길동" />
                </div>
                <div>
                  <Label>이메일 주소</Label>
                  <Input type="email" required value={signUpForm.email} onChange={e => setSignUpForm({...signUpForm, email: e.target.value})} placeholder="name@example.com" />
                </div>
                <div>
                  <Label>비밀번호 설정</Label>
                  <Input type="password" required minLength={8} value={signUpForm.password} onChange={e => setSignUpForm({...signUpForm, password: e.target.value})} placeholder="8자 이상" />
                </div>
                <Button variant="primary" type="submit" style={{ marginTop: '1rem', width: '100%', padding: '1rem' }} disabled={isLoading}>
                  계정 생성 <Sparkles size={18} />
                </Button>
              </Form>
            )}

            {mode === 'verify' && (
              <Form onSubmit={handleVerify}>
                <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
                  <div style={{ display: 'inline-flex', padding: '1rem', background: 'rgba(99, 102, 241, 0.05)', borderRadius: '20px', color: '#6366f1', marginBottom: '1rem' }}>
                    <ShieldCheck size={32} />
                  </div>
                  <p style={{ fontSize: '0.875rem', color: '#94a3b8', lineHeight: 1.6 }}>이메일로 전송된 <span style={{ color: 'white', fontWeight: 700 }}>6자리 코드</span>를 입력해 주세요.</p>
                </div>
                <Input
                  type="text" required maxLength={6}
                  style={{ textAlign: 'center', fontSize: '2rem', fontWeight: 900, letterSpacing: '0.5em', padding: '1.25rem' }}
                  value={verificationForm.code}
                  onChange={e => setVerificationForm({...verificationForm, code: e.target.value})}
                  placeholder="000000"
                />
                <Button variant="primary" type="submit" style={{ marginTop: '1rem', width: '100%', padding: '1rem' }} disabled={isLoading}>
                  인증 완료 <MailCheck size={18} />
                </Button>
              </Form>
            )}
          </motion.div>
        </AnimatePresence>
      </Card>
    </Container>
  )
}

export default OnboardingSection
