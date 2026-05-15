import { http } from './http'

export async function scanText(text) {
  const res = await http.post('/api/scan/text', { text })
  return res.data
}

export async function scanFile(file) {
  const form = new FormData()
  form.append('file', file)
  const res = await http.post('/api/scan/file', form)
  return res.data
}

export async function getHistory() {
  const res = await http.get('/api/scan/history')
  return res.data
}

export async function getScanById(id) {
  const res = await http.get(`/api/scan/${id}`)
  return res.data
}

export async function deleteScan(id) {
  await http.delete(`/api/scan/${id}`)
}

