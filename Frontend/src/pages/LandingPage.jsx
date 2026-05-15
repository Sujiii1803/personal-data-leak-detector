import { Link } from 'react-router-dom'
import {
  ArrowRight,
  FileSearch,
  LockKeyhole,
  ScanText,
  ShieldCheck,
} from 'lucide-react'
import { Button } from '../ui/components/Button'
import { Card, CardContent } from '../ui/components/Card'

function Feature({ icon: Icon, title, desc }) {
  return (
    <Card className="overflow-hidden">
      <CardContent className="p-5">
        <div className="flex items-start gap-3">
          <div className="grid h-10 w-10 place-items-center rounded-lg bg-cyber-500/15 text-cyan-100">
            <Icon className="h-5 w-5" />
          </div>
          <div>
            <div className="text-sm font-semibold">{title}</div>
            <div className="mt-1 text-sm text-slate-300">{desc}</div>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}

export function LandingPage() {
  return (
    <div className="min-h-screen bg-[radial-gradient(900px_500px_at_20%_0%,rgba(34,211,238,0.12),transparent),radial-gradient(800px_450px_at_80%_10%,rgba(168,85,247,0.12),transparent)]">
      <header className="border-b border-slate-900/60 bg-slate-950/40 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4 md:px-8">
          <div className="flex items-center gap-3">
            <div className="grid h-10 w-10 place-items-center rounded-lg bg-cyber-500/15 text-cyan-100">
              <ShieldCheck className="h-5 w-5" />
            </div>
            <div className="leading-tight">
              <div className="text-sm font-semibold">
                Personal Data Leak Detector
              </div>
              <div className="text-xs text-slate-400">
                Scan, detect, redact — instantly
              </div>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <Button as={Link} to="/login" variant="ghost">
              Login
            </Button>
            <Button as={Link} to="/register" variant="primary">
              Get started <ArrowRight className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-12 md:px-8 md:py-16">
        <section className="grid items-center gap-10 md:grid-cols-2">
          <div>
            <div className="inline-flex items-center gap-2 rounded-full border border-cyber-500/20 bg-cyber-500/10 px-3 py-1 text-xs text-cyan-100">
              <span className="h-2 w-2 animate-pulse rounded-full bg-cyber-400" />
              Enterprise-style PII detection for your text & files
            </div>

            <h1 className="mt-5 text-4xl font-semibold tracking-tight text-slate-50 md:text-5xl">
              Detect personal data leaks before they become incidents.
            </h1>
            <p className="mt-4 max-w-xl text-base text-slate-300">
              Paste text or upload documents to find risky PII patterns, review
              a risk summary, and get a redacted preview you can safely share.
            </p>

            <div className="mt-6 flex flex-wrap gap-3">
              <Button as={Link} to="/register" size="lg">
                Create account <ArrowRight className="h-4 w-4" />
              </Button>
              <Button as={Link} to="/login" size="lg" variant="secondary">
                Sign in
              </Button>
            </div>

            <div className="mt-6 flex flex-wrap items-center gap-4 text-xs text-slate-400">
              <span>JWT auth</span>
              <span className="h-1 w-1 rounded-full bg-slate-700" />
              <span>Scan history</span>
              <span className="h-1 w-1 rounded-full bg-slate-700" />
              <span>Redaction engine</span>
            </div>
          </div>

          <div className="relative">
            <div className="absolute inset-0 -z-10 rounded-3xl bg-gradient-to-br from-cyan-500/10 via-purple-500/10 to-transparent blur-2xl" />
            <Card className="overflow-hidden">
              <CardContent className="p-6">
                <div className="grid gap-4">
                  <div className="rounded-xl border border-slate-800 bg-slate-950/60 p-4">
                    <div className="text-xs text-slate-400">Sample finding</div>
                    <div className="mt-2 flex items-center justify-between">
                      <div className="text-sm font-medium">EMAIL</div>
                      <div className="rounded-full bg-rose-500/15 px-2 py-1 text-xs text-rose-200">
                        HIGH
                      </div>
                    </div>
                    <div className="mt-2 font-mono text-xs text-slate-300">
                      john.doe@example.com
                    </div>
                  </div>

                  <div className="rounded-xl border border-slate-800 bg-slate-950/60 p-4">
                    <div className="text-xs text-slate-400">
                      Redacted preview
                    </div>
                    <div className="mt-2 whitespace-pre-wrap rounded-lg bg-slate-900/40 p-3 font-mono text-xs text-slate-200">
                      Contact: [REDACTED_EMAIL]
                    </div>
                  </div>

                  <div className="grid grid-cols-3 gap-3 text-center">
                    <div className="rounded-xl border border-slate-800 bg-slate-950/60 p-3">
                      <div className="text-xs text-slate-400">High</div>
                      <div className="mt-1 text-lg font-semibold text-rose-200">
                        2
                      </div>
                    </div>
                    <div className="rounded-xl border border-slate-800 bg-slate-950/60 p-3">
                      <div className="text-xs text-slate-400">Medium</div>
                      <div className="mt-1 text-lg font-semibold text-amber-100">
                        1
                      </div>
                    </div>
                    <div className="rounded-xl border border-slate-800 bg-slate-950/60 p-3">
                      <div className="text-xs text-slate-400">Low</div>
                      <div className="mt-1 text-lg font-semibold text-emerald-100">
                        4
                      </div>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </section>

        <section className="mt-14">
          <div className="text-sm font-semibold text-slate-50">Features</div>
          <div className="mt-2 max-w-2xl text-sm text-slate-300">
            Everything you need for a clean scanning workflow, from detection to
            safe sharing.
          </div>

          <div className="mt-6 grid gap-4 md:grid-cols-2">
            <Feature
              icon={ScanText}
              title="Text scanning"
              desc="Paste content and detect sensitive patterns with risk levels."
            />
            <Feature
              icon={FileSearch}
              title="File upload scanning"
              desc="Upload a file, extract text, scan, and keep results in your history."
            />
            <Feature
              icon={LockKeyhole}
              title="JWT secured"
              desc="All scanning APIs are protected with JWT-based authentication."
            />
            <Feature
              icon={ShieldCheck}
              title="Redaction engine"
              desc="Preview redacted output and copy/download safe text."
            />
          </div>

          <div className="mt-8 flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-slate-800 bg-slate-950/50 p-5">
            <div>
              <div className="text-sm font-semibold text-slate-50">
                Ready to scan?
              </div>
              <div className="mt-1 text-sm text-slate-300">
                Create an account and start scanning on your local backend.
              </div>
            </div>
            <Button as={Link} to="/register">
              Start now <ArrowRight className="h-4 w-4" />
            </Button>
          </div>
        </section>
      </main>

      <footer className="border-t border-slate-900/60 bg-slate-950/30">
        <div className="mx-auto flex max-w-7xl flex-col gap-2 px-4 py-6 text-xs text-slate-500 md:flex-row md:items-center md:justify-between md:px-8">
          <div>© {new Date().getFullYear()} Personal Data Leak Detector</div>
          <div className="text-slate-600">
            Backend: <span className="text-slate-400">localhost:8080</span>
          </div>
        </div>
      </footer>
    </div>
  )
}

