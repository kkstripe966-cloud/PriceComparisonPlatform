import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, removeToken, setToken } from '@/services/authStorage'
import { getUserInfo as fetchUserInfo, type UserInfo } from '@/services/authApi'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(getToken())
  const userInfo = ref<UserInfo | null>(null)

  const isLoggedIn = ref(!!token.value)

  function login(newToken: string) {
    token.value = newToken
    setToken(newToken)
    isLoggedIn.value = true
    fetchUser() // Fetch user info after login
  }

  function logout() {
    token.value = null
    userInfo.value = null
    removeToken()
    isLoggedIn.value = false
  }

  async function fetchUser() {
    if (token.value && !userInfo.value) {
      try {
        const response = await fetchUserInfo()
        if (response.success) {
          userInfo.value = response.data
        } else {
          // Token might be invalid, log out
          logout()
        }
      } catch (error) {
        console.error('Failed to fetch user info:', error)
        logout()
      }
    }
  }

  // Fetch user info on initial load if token exists
  if (token.value) {
    fetchUser()
  }

  return { token, userInfo, isLoggedIn, login, logout, fetchUser }
})
