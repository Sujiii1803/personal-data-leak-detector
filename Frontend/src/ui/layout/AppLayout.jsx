import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { History, LayoutDashboard, LogOut, ShieldAlert } from 'lucide-react'
import { useAuth } from '../../app/auth/AuthContext'
import { cn } from '../lib/cn'
import { Button } from '../components/Button'

export function AppLayout() {
  const { email, logout } = useAuth()
  const navigate = useNavigate()

  function onLogout() {
    logout()
    navigate('/login')
  }

  const navLinkClass = ({ isActive }) =>
    cn(
      'flex items-center gap-2 rounded-lg px-3 py-2 text-sm transition',
      isActive
        ? 'bg-cyber-500/15 text-cyan-100 border border-cyber-500/20'
        : 'text-slate-200 hover:bg-slate-900/60',
    )

  return (
    <div className="min-h-screen bg-[radial-gradient(800px_400px_at_20%_0%,rgba(34,211,238,0.10),transparent),radial-gradient(700px_300px_at_80%_10%,rgba(168,85,247,0.10),transparent)]">
      <div className="mx-auto flex min-h-screen max-w-7xl">
        <aside className="hidden w-72 shrink-0 border-r border-slate-900/60 bg-slate-950/40 p-4 backdrop-blur md:block">
          <div className="flex items-center gap-3 rounded-xl border border-slate-800 bg-slate-950/60 p-3 shadow-glow">
            <div className="grid h-10 w-10 place-items-center rounded-lg bg-cyber-500/15 text-cyan-100">
              <ShieldAlert className="h-5 w-5" />
            </div>
            <div>
              <div className="text-sm font-semibold leading-tight">
                Personal Data Leak Detector
              </div>
              <div className="text-xs text-slate-400">{email}</div>
            </div>
          </div>

          <nav className="mt-4 space-y-1">
            <NavLink to="/app" className={navLinkClass} end>
              <LayoutDashboard className="h-4 w-4" />
              Dashboard
            </NavLink>
            <NavLink to="/app/history" className={navLinkClass}>
              <History className="h-4 w-4" />
              Scan history
            </NavLink>
          </nav>

          <div className="mt-6">
            <Button
              variant="secondary"
              className="w-full justify-start"
              onClick={onLogout}
              type="button"
            >
              <LogOut className="h-4 w-4" />
              Logout
            </Button>
          </div>
        </aside>

        <main className="flex-1 px-4 py-6 md:px-8">
          <div className="mx-auto max-w-5xl">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  )
}

