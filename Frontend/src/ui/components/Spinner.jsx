import { cn } from '../lib/cn'

export function Spinner({ className }) {
  return (
    <div
      className={cn(
        'h-5 w-5 animate-spin rounded-full border-2 border-slate-700 border-t-cyber-400',
        className,
      )}
      aria-label="Loading"
      role="status"
    />
  )
}

