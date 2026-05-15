import { Link } from 'react-router-dom'
import { ShieldCheck } from 'lucide-react'
import { Card, CardContent } from '../components/Card'

export function AuthShell({ title, subtitle, children, footer }) {
  return (
    <div className="min-h-screen bg-[radial-gradient(900px_500px_at_20%_0%,rgba(34,211,238,0.12),transparent),radial-gradient(800px_450px_at_80%_10%,rgba(168,85,247,0.12),transparent)] px-4 py-10">
      <div className="mx-auto w-full max-w-md">
        <Link to="/" className="inline-flex items-center gap-2 text-sm">
          <span className="grid h-10 w-10 place-items-center rounded-lg bg-cyber-500/15 text-cyan-100">
            <ShieldCheck className="h-5 w-5" />
          </span>
          <span className="font-semibold text-slate-100">
            Personal Data Leak Detector
          </span>
        </Link>

        <Card className="mt-6 overflow-hidden">
          <CardContent className="p-6">
            <h1 className="text-xl font-semibold text-slate-50">{title}</h1>
            {subtitle ? (
              <p className="mt-1 text-sm text-slate-300">{subtitle}</p>
            ) : null}
            <div className="mt-6">{children}</div>
          </CardContent>
        </Card>

        {footer ? (
          <div className="mt-4 text-center text-sm text-slate-400">
            {footer}
          </div>
        ) : null}
      </div>
    </div>
  )
}

