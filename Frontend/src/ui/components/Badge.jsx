import { cn } from '../lib/cn'

export function Badge({ tone = 'neutral', className, ...props }) {
  const tones = {
    neutral: 'bg-slate-900/70 text-slate-200 border-slate-800',
    high: 'bg-rose-500/15 text-rose-200 border-rose-500/20',
    medium: 'bg-amber-400/15 text-amber-100 border-amber-400/20',
    low: 'bg-emerald-400/15 text-emerald-100 border-emerald-400/20',
    info: 'bg-cyber-500/15 text-cyan-100 border-cyber-500/20',
  }

  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full border px-2.5 py-1 text-xs font-medium',
        tones[tone] ?? tones.neutral,
        className,
      )}
      {...props}
    />
  )
}

