import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import * as authApi from '../api/authApi'

const AuthContext = createContext(null)

function readAuth() {
  const token = localStorage.getItem('pdl_token')
  const email = localStorage.getItem('pdl_email')
  return { token: token || null, email: email || null }
}

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => readAuth())

  useEffect(() => {
    const onChanged = () => setAuth(readAuth())
    window.addEventListener('pdl_auth_changed', onChanged)
    window.addEventListener('storage', onChanged)
    return () => {
      window.removeEventListener('pdl_auth_changed', onChanged)
      window.removeEventListener('storage', onChanged)
    }
  }, [])

  const value = useMemo(() => {
    const isAuthenticated = Boolean(auth.token)

    async function login({ email, password }) {
      const data = await authApi.login({ email, password })
      localStorage.setItem('pdl_token', data.token)
      localStorage.setItem('pdl_email', data.email ?? email)
      window.dispatchEvent(new Event('pdl_auth_changed'))
      return data
    }

    function logout() {
      localStorage.removeItem('pdl_token')
      localStorage.removeItem('pdl_email')
      window.dispatchEvent(new Event('pdl_auth_changed'))
    }

    return {
      token: auth.token,
      email: auth.email,
      isAuthenticated,
      login,
      logout,
    }
  }, [auth.email, auth.token])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

