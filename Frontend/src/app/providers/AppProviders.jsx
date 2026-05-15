import { BrowserRouter } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { AuthProvider } from '../auth/AuthContext'

export function AppProviders({ children }) {
  return (
    <AuthProvider>
      <BrowserRouter>
        {children}
        <Toaster
          position="top-right"
          toastOptions={{
            style: {
              background: 'rgba(15, 23, 42, 0.92)',
              color: '#e2e8f0',
              border: '1px solid rgba(34, 211, 238, 0.18)',
            },
          }}
        />
      </BrowserRouter>
    </AuthProvider>
  )
}

