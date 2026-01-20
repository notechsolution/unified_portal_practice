import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  
  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  const userInfo = computed(() => user.value)
  
  // 方法
  const setToken = (newToken) => {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('token', newToken)
    } else {
      localStorage.removeItem('token')
    }
  }
  
  const setUser = (userData) => {
    user.value = userData
  }
  
  const loginAction = async (loginForm) => {
    try {
      const response = await login(loginForm)
      console.log('Login response:', response)
      // response now contains the data directly due to request.js interceptor
      const { token, user } = response
      
      setToken(token)
      setUser(user)
      
      return { success: true }
    } catch (error) {
      console.error('Login error:', error)
      return { success: false, message: error.message }
    }
  }
  
  const logoutAction = async () => {
    try {
      await logout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      setToken('')
      setUser(null)
    }
  }
  
  const initializeAuth = async () => {
    if (token.value) {
      try {
        const response = await getUserInfo()
        setUser(response.data)
      } catch (error) {
        setToken('')
        setUser(null)
      }
    }
  }
  
  const updateUserInfo = (userData) => {
    setUser(userData)
  }
  
  return {
    // 状态
    user,
    token,
    // 计算属性
    isAuthenticated,
    userInfo,
    // 方法
    setToken,
    setUser,
    loginAction,
    logoutAction,
    initializeAuth,
    updateUserInfo
  }
})