import { createGlobalStyle } from 'styled-components';

export const theme = {
  colors: {
    bg: '#020617',
    card: '#0f172a',
    cardHover: '#1e293b',
    accent: '#6366f1',
    accentHover: '#818cf8',
    textMain: '#f8fafc',
    textMuted: '#94a3b8',
    border: 'rgba(255, 255, 255, 0.06)',
    success: '#10b981',
    error: '#f43f5e',
    warning: '#f59e0b',
  },
  shadows: {
    main: '0 25px 50px -12px rgba(0, 0, 0, 0.5)',
    glow: '0 0 20px rgba(99, 102, 241, 0.15)',
  },
  radius: {
    sm: '8px',
    md: '12px',
    lg: '24px',
    full: '9999px',
  },
};

export const GlobalStyle = createGlobalStyle`
  @import url('https://rsms.me/inter/inter.css');

  * {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }

  body {
    font-family: 'Inter', sans-serif;
    background-color: ${props => props.theme.colors.bg};
    color: ${props => props.theme.colors.textMain};
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    overflow-x: hidden;
  }

  button {
    font-family: inherit;
    border: none;
    outline: none;
    cursor: pointer;
    background: none;
  }

  input, select, textarea {
    font-family: inherit;
  }

  ::-webkit-scrollbar {
    width: 6px;
  }

  ::-webkit-scrollbar-track {
    background: ${props => props.theme.colors.bg};
  }

  ::-webkit-scrollbar-thumb {
    background: #1e293b;
    border-radius: 10px;
  }
`;
