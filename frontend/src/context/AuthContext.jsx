import { createContext, useContext, useState } from 'react';
import { api } from '../lib/api';

const KEY = 'filmmax.session';
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem(KEY));
    } catch {
      return null;
    }
  });

  function store(next) {
    setSession(next);
    if (next) localStorage.setItem(KEY, JSON.stringify(next));
    else localStorage.removeItem(KEY);
  }

  async function login(username, password) {
    store(await api('/auth/login', { method: 'POST', body: { username, password } }));
  }

  async function register(form) {
    store(await api('/auth/register', { method: 'POST', body: form }));
  }

  const value = {
    user: session ? { username: session.username, role: session.role } : null,
    token: session?.token,
    login,
    register,
    logout: () => store(null),
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}