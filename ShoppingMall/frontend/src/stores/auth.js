import { defineStore } from 'pinia'
import api from '../utils/http'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),
  getters: {
    isAuthenticated: (state) => !!state.token
  },
  actions: {
    async login(username, password) {
      const { data } = await api.post('/api/auth/login', { username, password })
      // data is unwrapped payload: { token, id, username, role }
      this.token = data.token
      this.user = { id: data.id, username: data.username, role: data.role }
      localStorage.setItem('token', this.token)
      localStorage.setItem('user', JSON.stringify(this.user))
      return data
    },
    async register(username, password, email) {
      await api.post('/api/auth/register', { username, password, email })
      return true
    },
    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
