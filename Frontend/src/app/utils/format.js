export function formatInstant(instant) {
  if (!instant) return ''
  const d = new Date(instant)
  if (Number.isNaN(d.getTime())) return String(instant)
  return d.toLocaleString()
}

export function riskTone(riskLevel) {
  const v = String(riskLevel || '').toUpperCase()
  if (v === 'HIGH') return 'high'
  if (v === 'MEDIUM') return 'medium'
  if (v === 'LOW') return 'low'
  return 'neutral'
}

