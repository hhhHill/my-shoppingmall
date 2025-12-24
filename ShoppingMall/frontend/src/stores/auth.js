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
      if (data.success) {
        this.token = data.data.token
        this.user = { id: data.data.id, username: data.data.username, role: data.data.role }
        localStorage.setItem('token', this.token)
        localStorage.setItem('user', JSON.stringify(this.user))
      }
      return data
    },
    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})

