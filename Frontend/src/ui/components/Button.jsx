import { cn } from '../lib/cn'

export function Button({
  as: Comp = 'button',
  variant = 'primary',
  size = 'md',
  className,
  ...props
}) {
  const base =
    'inline-flex items-center justify-center gap-2 rounded-lg font-medium transition disabled:opacity-60 disabled:cursor-not-allowed focus:outline-none focus-visible:ring-2 focus-visible:ring-cyber-400/60 focus-visible:ring-offset-2 focus-visible:ring-offset-slate-950'

  const variants = {
    primary:
      'bg-cyber-500 text-slate-900 hover:bg-cyber-400 shadow-[0_0_0_1px_rgba(34,211,238,0.15)]',
    secondary:
      'bg-slate-900/70 text-slate-100 hover:bg-slate-800 border border-slate-700',
    ghost: 'text-slate-200 hover:bg-slate-800/80 hover:text-white',
    danger:
      'bg-rose-500/90 text-white hover:bg-rose-500 border border-rose-400/20',
  }

  const sizes = {
    sm: 'h-9 px-3 text-sm',
    md: 'h-10 px-4 text-sm',
    lg: 'h-12 px-5 text-base',
  }

  return (
    <Comp
      className={cn(base, variants[variant], sizes[size], className)}
      {...props}
    />
  )
}

