import { http } from './http'

export async function register({ email, password }) {
  await http.post('/api/auth/register', { email, password })
}

export async function login({ email, password }) {
  const res = await http.post('/api/auth/login', { email, password })
  return res.data
}

