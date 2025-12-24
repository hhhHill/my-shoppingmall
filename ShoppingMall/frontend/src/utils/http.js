import axios from 'axios'
import { useAuthStore } from '../stores/auth'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8080'
})

api.interceptors.request.use((config) => {
  try {
    const auth = useAuthStore()
    if (auth.token) {
      config.headers.Authorization = `Bearer ${auth.token}`
    }
  } catch (e) { /* pinia not ready on first import during setup */ }
  return config
})

api.interceptors.response.use(
  (resp) => {
    const payload = resp?.data
    if (payload && typeof payload === 'object' && 'code' in payload) {
      if (payload.code === 0) {
        // unwrap to data
        return { ...resp, data: payload.data }
      }
      const err = new Error(payload.message || 'Request failed')
      err.code = payload.code
      err.response = resp
      return Promise.reject(err)
    }
    return resp
  },
  (error) => {
    const status = error?.response?.status
    if (status === 401) {
      try { useAuthStore().logout() } catch {}
    }
    return Promise.reject(error)
  }
)

export default api
