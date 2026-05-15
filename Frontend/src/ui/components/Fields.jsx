import { cn } from '../lib/cn'

export function Label({ className, ...props }) {
  return (
    <label
      className={cn('text-sm font-medium text-slate-200', className)}
      {...props}
    />
  )
}

export function Input({ className, ...props }) {
  return (
    <input
      className={cn(
        'h-10 w-full rounded-lg border border-slate-700 bg-slate-900/60 px-3 text-sm text-slate-100 outline-none placeholder:text-slate-400 focus:border-cyber-500/60 focus:ring-2 focus:ring-cyber-500/20',
        className,
      )}
      {...props}
    />
  )
}

export function Textarea({ className, ...props }) {
  return (
    <textarea
      className={cn(
        'min-h-40 w-full resize-y rounded-lg border border-slate-700 bg-slate-900/60 px-3 py-2 text-sm text-slate-100 outline-none placeholder:text-slate-400 focus:border-cyber-500/60 focus:ring-2 focus:ring-cyber-500/20',
        className,
      )}
      {...props}
    />
  )
}

