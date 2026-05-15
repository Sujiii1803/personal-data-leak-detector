import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { Trash2 } from 'lucide-react'
import { deleteScan, getHistory } from '../app/api/scanApi'
import { formatInstant } from '../app/utils/format'
import { Badge } from '../ui/components/Badge'
import { Button } from '../ui/components/Button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/components/Card'
import { Spinner } from '../ui/components/Spinner'

export function ScanHistoryPage() {
  const navigate = useNavigate()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(false)

  async function load() {
    setLoading(true)
    try {
      const data = await getHistory()
      setItems(Array.isArray(data) ? data : [])
    } catch (err) {
      toast.error('Failed to load scan history')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  async function onDelete(id) {
    if (!confirm('Delete this scan from history?')) return
    try {
      await deleteScan(id)
      toast.success('Deleted')
      await load()
    } catch (err) {
      toast.error('Delete failed')
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-slate-50">Scan history</h1>
        <p className="mt-1 text-sm text-slate-300">
          Review previous scans, risk counts, and details.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Previous scans</CardTitle>
          <CardDescription>All results saved for your account.</CardDescription>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="flex items-center gap-2 text-sm text-slate-300">
              <Spinner /> Loading…
            </div>
          ) : items.length === 0 ? (
            <div className="text-sm text-slate-400">No scans found.</div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead className="text-xs text-slate-400">
                  <tr className="border-b border-slate-900/60">
                    <th className="py-2 pr-3">Date</th>
                    <th className="py-2 pr-3">Type</th>
                    <th className="py-2 pr-3">Filename</th>
                    <th className="py-2 pr-3">Risk counts</th>
                    <th className="py-2 pr-3">Total</th>
                    <th className="py-2 pr-0 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {items.map((s) => (
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
                      <td className="py-3 pr-0">
                        <div className="flex justify-end gap-2">
                          <Button
                            type="button"
                            variant="secondary"
                            onClick={() => navigate(`/app/scans/${s.id}`)}
                          >
                            View details
                          </Button>
                          <Button
                            type="button"
                            variant="danger"
                            onClick={() => onDelete(s.id)}
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
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

