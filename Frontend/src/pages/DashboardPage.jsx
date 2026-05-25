import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { FileUp, ScanText } from 'lucide-react'
import { scanFile, scanText, getHistory } from '../app/api/scanApi'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/components/Card'
import { Button } from '../ui/components/Button'
import { Textarea } from '../ui/components/Fields'
import { Spinner } from '../ui/components/Spinner'
import { Badge } from '../ui/components/Badge'
import { formatInstant } from '../app/utils/format'

function StatCard({ label, value, tone }) {
  const toneClass =
    tone === 'high'
      ? 'text-rose-200'
      : tone === 'medium'
        ? 'text-amber-100'
        : tone === 'low'
          ? 'text-emerald-100'
          : 'text-slate-100'
  return (
    <Card>
      <CardContent className="p-5">
        <div className="text-xs text-slate-400">{label}</div>
        <div className={`mt-1 text-2xl font-semibold ${toneClass}`}>{value}</div>
      </CardContent>
    </Card>
  )
}

export function DashboardPage() {
  const navigate = useNavigate()
  const [mode, setMode] = useState('text') // 'text' | 'file'
  const [text, setText] = useState('')
  const [file, setFile] = useState(null)
  const [loading, setLoading] = useState(false)
  const [history, setHistory] = useState([])
  const [loadingHistory, setLoadingHistory] = useState(false)

  async function refreshHistory() {
    setLoadingHistory(true)
    try {
      const data = await getHistory()
      setHistory(Array.isArray(data) ? data : [])
    } catch (err) {
      toast.error('Failed to load history')
    } finally {
      setLoadingHistory(false)
    }
  }

  useEffect(() => {
    refreshHistory()
  }, [])

  const stats = useMemo(() => {
    const items = history.slice(0, 25)
    return {
      totalScans: history.length,
      high: items.reduce((a, s) => a + (s.highRiskCount || 0), 0),
      medium: items.reduce((a, s) => a + (s.mediumRiskCount || 0), 0),
      low: items.reduce((a, s) => a + (s.lowRiskCount || 0), 0),
    }
  }, [history])

  async function onScan() {
    if (mode === 'text' && !text.trim()) {
      toast.error('Paste some text to scan')
      return
    }
    if (mode === 'file' && !file) {
      toast.error('Choose a file to scan')
      return
    }

    setLoading(true)
    try {
      const res =
        mode === 'text' ? await scanText(text.trim()) : await scanFile(file)
      toast.success('Scan completed')
      await refreshHistory()
      if (res?.id != null) {
        navigate(`/app/scans/${res.id}`, { state: { result: res } })
      } else {
        navigate('/app/history')
      }
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        'Scan failed'
      toast.error(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-50">Dashboard</h1>
          <p className="mt-1 text-sm text-slate-300">
            Scan text or upload a file. Results are saved to your history.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Button
            type="button"
            variant={mode === 'text' ? 'primary' : 'secondary'}
            onClick={() => setMode('text')}
          >
            <ScanText className="h-4 w-4" />
            Text
          </Button>
          <Button
            type="button"
            variant={mode === 'file' ? 'primary' : 'secondary'}
            onClick={() => setMode('file')}
          >
            <FileUp className="h-4 w-4" />
            File
          </Button>
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-4">
        <StatCard label="Total scans" value={stats.totalScans} />
        <StatCard label="High findings (last 25)" value={stats.high} tone="high" />
        <StatCard
          label="Medium findings (last 25)"
          value={stats.medium}
          tone="medium"
        />
        <StatCard label="Low findings (last 25)" value={stats.low} tone="low" />
      </div>

      <Card>
        <CardHeader>
          <CardTitle>New scan</CardTitle>
          <CardDescription>
            {mode === 'text'
              ? 'Paste text content to scan for PII.'
              : 'Upload a file. The backend will extract text and scan it.'}
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          {mode === 'text' ? (
            <Textarea
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Paste text here…"
            />
          ) : (
            <div className="rounded-xl border border-dashed border-slate-800 bg-slate-950/40 p-5">
              <input
                type="file"
                className="block w-full text-sm text-slate-200 file:mr-4 file:rounded-lg file:border-0 file:bg-slate-900/70 file:px-4 file:py-2 file:text-sm file:font-medium file:text-slate-100 hover:file:bg-slate-900"
                onChange={(e) => setFile(e.target.files?.[0] ?? null)}
              />
              {file ? (
                <div className="mt-3 text-xs text-slate-400">
                  Selected: <span className="text-slate-200">{file.name}</span>
                </div>
              ) : null}
            </div>
          )}

          <div className="flex items-center justify-between gap-3">
            <Button type="button" onClick={onScan} disabled={loading}>
              {loading ? <Spinner /> : null}
              Scan now
            </Button>
          </div>
        </CardContent>



      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Recent scans</CardTitle>
          <CardDescription>Latest results from your scan history.</CardDescription>
        </CardHeader>
        <CardContent>
          {loadingHistory ? (
            <div className="flex items-center gap-2 text-sm text-slate-300">
              <Spinner /> Loading…
            </div>
          ) : history.length === 0 ? (
            <div className="text-sm text-slate-400">No scans yet.</div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="text-xs text-slate-400">
                  <tr className="border-b border-slate-900/60">
                    <th className="py-2 pr-3">When</th>
                    <th className="py-2 pr-3">Type</th>
                    <th className="py-2 pr-3">Filename</th>
                    <th className="py-2 pr-3">Risk</th>
                    <th className="py-2 pr-3">Total</th>
                    <th className="py-2 pr-3"></th>
                  </tr>
                </thead>
                <tbody>
                  {history.slice(0, 6).map((s) => (
                    <tr
                      key={s.id}
                      className="border-b border-slate-900/40 hover:bg-slate-900/20"
                    >
                      <td className="py-3 pr-3 text-slate-200">
                        {formatInstant(s.scannedAt)}
                      </td>
                      <td className="py-3 pr-3 text-slate-300">{s.scanType}</td>
                      <td className="py-3 pr-3 text-slate-300">
                        {s.originalFilename || '—'}
                      </td>
                      <td className="py-3 pr-3">
                        <div className="flex flex-wrap gap-2">
                          <Badge tone="high">High: {s.highRiskCount}</Badge>
                          <Badge tone="medium">Med: {s.mediumRiskCount}</Badge>
                          <Badge tone="low">Low: {s.lowRiskCount}</Badge>
                        </div>
                      </td>
                      <td className="py-3 pr-3 text-slate-200">
                        {s.totalFindings}
                      </td>
                      <td className="py-3 pr-0 text-right">
                        <Button
                          type="button"
                          variant="ghost"
                          onClick={() => navigate(`/app/scans/${s.id}`)}
                        >
                          View
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

