import styled, { css } from 'styled-components';
import { motion } from 'framer-motion';

export const Card = styled(motion.div)`
  background: ${props => props.theme.colors.card};
  border: 1px solid ${props => props.theme.colors.border};
  border-radius: ${props => props.theme.radius.lg};
  padding: 2rem;
  box-shadow: ${props => props.theme.shadows.main};
  position: relative;
  overflow: hidden;
`;

export const Button = styled.button<{ variant?: 'primary' | 'secondary' | 'ghost' | 'danger' }>`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: ${props => props.theme.radius.md};
  font-size: 0.875rem;
  font-weight: 600;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  color: white;

  ${props => props.variant === 'primary' && css`
    background: ${props.theme.colors.accent};
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
    &:hover {
      background: ${props.theme.colors.accentHover};
      transform: translateY(-1px);
    }
  `}

  ${props => props.variant === 'secondary' && css`
    background: ${props.theme.colors.cardHover};
    border: 1px solid ${props.theme.colors.border};
    &:hover {
      background: #334155;
    }
  `}

  ${props => props.variant === 'ghost' && css`
    background: transparent;
    color: ${props.theme.colors.textMuted};
    &:hover {
      color: white;
      background: rgba(255, 255, 255, 0.05);
    }
  `}

  ${props => props.variant === 'danger' && css`
    background: rgba(244, 63, 94, 0.1);
    color: ${props.theme.colors.error};
    &:hover {
      background: ${props.theme.colors.error};
      color: white;
    }
  `}

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

export const Input = styled.input`
  background: rgba(30, 41, 59, 0.5);
  border: 1px solid ${props => props.theme.colors.border};
  border-radius: ${props => props.theme.radius.md};
  padding: 0.75rem 1rem;
  color: white;
  font-size: 0.9375rem;
  width: 100%;
  transition: all 0.2s;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.accent};
    background: rgba(15, 23, 42, 0.8);
    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
  }
`;

export const Select = styled.select`
  background: rgba(30, 41, 59, 0.5);
  border: 1px solid ${props => props.theme.colors.border};
  border-radius: ${props => props.theme.radius.md};
  padding: 0.75rem 1rem;
  color: white;
  font-size: 0.9375rem;
  width: 100%;
  appearance: none;
  cursor: pointer;

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.accent};
  }
`;

export const Label = styled.label`
  display: block;
  font-size: 0.75rem;
  font-weight: 700;
  color: ${props => props.theme.colors.textMuted};
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.5rem;
`;

export const Badge = styled.span<{ variant?: 'success' | 'warning' | 'info' }>`
  font-size: 0.625rem;
  font-weight: 800;
  padding: 0.25rem 0.625rem;
  border-radius: ${props => props.theme.radius.full};
  text-transform: uppercase;
  letter-spacing: 0.025em;

  ${props => props.variant === 'success' ? css`
    background: rgba(16, 185, 129, 0.1);
    color: ${props.theme.colors.success};
  ` : props.variant === 'warning' ? css`
    background: rgba(245, 158, 11, 0.1);
    color: ${props.theme.colors.warning};
  ` : css`
    background: rgba(99, 102, 241, 0.1);
    color: ${props.theme.colors.accent};
  `}
`;
