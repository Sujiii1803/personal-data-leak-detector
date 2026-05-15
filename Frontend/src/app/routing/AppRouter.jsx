import { Navigate, Route, Routes } from 'react-router-dom'
import { ProtectedRoute } from './ProtectedRoute'
import { LandingPage } from '../../pages/LandingPage'
import { LoginPage } from '../../pages/LoginPage'
import { RegisterPage } from '../../pages/RegisterPage'
import { DashboardPage } from '../../pages/DashboardPage'
import { ScanHistoryPage } from '../../pages/ScanHistoryPage'
import { ScanResultPage } from '../../pages/ScanResultPage'
import { AppLayout } from '../../ui/layout/AppLayout'

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/login" element={<LoginPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/app" element={<DashboardPage />} />
          <Route path="/app/history" element={<ScanHistoryPage />} />
          <Route path="/app/scans/:id" element={<ScanResultPage />} />
          <Route path="/app/*" element={<Navigate to="/app" replace />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

