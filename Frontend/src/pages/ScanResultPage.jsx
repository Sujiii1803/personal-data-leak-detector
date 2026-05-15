import { useEffect, useMemo, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import toast from 'react-hot-toast'
import { Copy, Download, ArrowLeft } from 'lucide-react'
import { getScanById } from '../app/api/scanApi'
import { formatInstant, riskTone } from '../app/utils/format'
import { Badge } from '../ui/components/Badge'
import { Button } from '../ui/components/Button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/components/Card'
import { Spinner } from '../ui/components/Spinner'

function downloadText(filename, text) {
  const blob = new Blob([text ?? ''], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  a.remove()
  URL.revokeObjectURL(url)
}

export function ScanResultPage() {
  const { id } = useParams()
  const location = useLocation()
  const navigate = useNavigate()

  const [result, setResult] = useState(() => location.state?.result ?? null)
  const [loading, setLoading] = useState(!location.state?.result)

  useEffect(() => {
    let cancelled = false
    async function load() {
      if (result) return
      setLoading(true)
      try {
        const data = await getScanById(id)
        if (!cancelled) setResult(data)
      } catch (err) {
        toast.error('Failed to load scan result')
      } finally {
        if (!cancelled) setLoading(false)
      }
    }
    load()
    return () => {
      cancelled = true
    }
  }, [id, result])

  const riskSummary = useMemo(() => {
    if (!result) return null
    const total = result.totalFindings ?? 0
    const high = result.highRiskCount ?? 0
    const med = result.mediumRiskCount ?? 0
    const low = result.lowRiskCount ?? 0
    const score = high * 3 + med * 2 + low * 1
    const band = score >= 8 ? 'HIGH' : score >= 3 ? 'MEDIUM' : 'LOW'
    return { total, high, med, low, score, band }
  }, [result])

  async function onCopy() {
    try {
      await navigator.clipboard.writeText(result?.redactedText ?? '')
      toast.success('Copied redacted text')
    } catch {
      toast.error('Copy failed')
    }
  }

  function onDownload() {
    const base = result?.originalFilename
      ? `${result.originalFilename}.redacted.txt`
      : `scan-${result?.id ?? id}-redacted.txt`
    downloadText(base, result?.redactedText ?? '')
  }

  if (loading) {
    return (
      <div className="flex items-center gap-2 text-sm text-slate-300">
        <Spinner /> Loading scan…
      </div>
    )
  }

  if (!result) {
    return (
      <div className="space-y-3">
        <div className="text-sm text-slate-300">No result found.</div>
        <Button type="button" variant="secondary" onClick={() => navigate('/app/history')}>
          Back to history
        </Button>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div>
          <Button
            type="button"
            variant="ghost"
            className="-ml-2"
            onClick={() => navigate(-1)}
          >
            <ArrowLeft className="h-4 w-4" />
            Back
          </Button>
          <h1 className="text-2xl font-semibold text-slate-50">Scan result</h1>
          <p className="mt-1 text-sm text-slate-300">
            {formatInstant(result.scannedAt)} · {result.scanType}
            {result.originalFilename ? ` · ${result.originalFilename}` : ''}
          </p>
        </div>
        {riskSummary ? (
          <div className="flex flex-wrap items-center gap-2">
            <Badge tone={riskSummary.band === 'HIGH' ? 'high' : riskSummary.band === 'MEDIUM' ? 'medium' : 'low'}>
              Overall risk: {riskSummary.band}
            </Badge>
            <Badge tone="info">Findings: {riskSummary.total}</Badge>
          </div>
        ) : null}
      </div>

      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardContent className="p-5">
            <div className="text-xs text-slate-400">High</div>
            <div className="mt-1 text-2xl font-semibold text-rose-200">
              {result.highRiskCount}
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-5">
            <div className="text-xs text-slate-400">Medium</div>
            <div className="mt-1 text-2xl font-semibold text-amber-100">
              {result.mediumRiskCount}
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-5">
            <div className="text-xs text-slate-400">Low</div>
            <div className="mt-1 text-2xl font-semibold text-emerald-100">
              {result.lowRiskCount}
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-5">
            <div className="text-xs text-slate-400">Total findings</div>
            <div className="mt-1 text-2xl font-semibold text-slate-100">
              {result.totalFindings}
            </div>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Findings</CardTitle>
          <CardDescription>
            Detected PII patterns with locations and risk levels.
          </CardDescription>
        </CardHeader>
        <CardContent>
          {result.findings?.length ? (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="text-xs text-slate-400">
                  <tr className="border-b border-slate-900/60">
                    <th className="py-2 pr-3">Type</th>
                    <th className="py-2 pr-3">Matched value</th>
                    <th className="py-2 pr-3">Range</th>
                    <th className="py-2 pr-0">Risk</th>
                  </tr>
                </thead>
                <tbody>
                  {result.findings.map((f, idx) => (
                    <tr
                      key={`${f.type}-${f.startIndex}-${idx}`}
                      className="border-b border-slate-900/40 hover:bg-slate-900/20"
                    >
                      <td className="py-3 pr-3 font-medium text-slate-100">
                        {String(f.type)}
                      </td>
                      <td className="py-3 pr-3 font-mono text-xs text-slate-200">
                        {f.matchedValue}
                      </td>
                      <td className="py-3 pr-3 text-slate-300">
                        {f.startIndex}–{f.endIndex}
                      </td>
                      <td className="py-3 pr-0">
                        <Badge tone={riskTone(f.riskLevel)}>
                          {String(f.riskLevel)}
                        </Badge>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="text-sm text-slate-400">No findings.</div>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Redacted text preview</CardTitle>
          <CardDescription>
            Use this safe preview to share content without exposing sensitive data.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-3">
          <div className="rounded-xl border border-slate-800 bg-slate-950/60 p-4">
            <pre className="max-h-80 overflow-auto whitespace-pre-wrap break-words font-mono text-xs text-slate-100">
              {result.redactedText || ''}
            </pre>
          </div>
          <div className="flex flex-wrap gap-2">
            <Button type="button" variant="secondary" onClick={onCopy}>
              <Copy className="h-4 w-4" />
              Copy
            </Button>
            <Button type="button" variant="secondary" onClick={onDownload}>
              <Download className="h-4 w-4" />
              Download
            </Button>
            <Button type="button" variant="ghost" onClick={() => navigate('/app/history')}>
              View history
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

