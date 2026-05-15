import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { Button } from '../ui/components/Button'
import { Input, Label } from '../ui/components/Fields'
import { Spinner } from '../ui/components/Spinner'
import { AuthShell } from '../ui/layout/AuthShell'
import * as authApi from '../app/api/authApi'

export function RegisterPage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)

  async function onSubmit(e) {
    e.preventDefault()
    setLoading(true)
    try {
      await authApi.register({ email, password })
      toast.success('Account created. Please login.')
      navigate('/login')
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        'Registration failed'
      toast.error(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthShell
      title="Create your account"
      subtitle="Register to scan text/files and view your scan history."
      footer={
        <>
          Already have an account?{' '}
          <Link className="text-cyan-200 hover:underline" to="/login">
            Login
          </Link>
        </>
      }
    >
      <form className="space-y-4" onSubmit={onSubmit}>
        <div className="space-y-1.5">
          <Label htmlFor="email">Email</Label>
          <Input
            id="email"
            type="email"
            autoComplete="email"
            placeholder="you@example.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="space-y-1.5">
          <Label htmlFor="password">Password</Label>
          <Input
            id="password"
            type="password"
            autoComplete="new-password"
            placeholder="Min 8 characters"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            minLength={8}
            required
          />
        </div>
        <Button className="w-full" type="submit" disabled={loading}>
          {loading ? <Spinner /> : null}
          Create account
        </Button>
      </form>
    </AuthShell>
  )
}

