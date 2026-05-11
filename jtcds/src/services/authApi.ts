import type { LoginData, RegisterData } from '@/types'
import api from './http'

export interface UserInfo {
  id: number
  username: string
  email: string
  // Add other user properties as needed
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

export const apiLogin = (data: LoginData): Promise<ApiResponse<{ token: string }>> => {
  const params = new URLSearchParams()
  params.append('username', data.username)
  params.append('password', data.password)
  return api.post('/api/user/login', params, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  })
}

export const apiRegister = (data: RegisterData): Promise<ApiResponse<null>> => {
  const params = new URLSearchParams()
  params.append('phone', data.username) // 后端期望phone参数
  params.append('email', data.email)
  params.append('password', data.password)
  params.append('confirmPassword', data.password) // 后端需要确认密码
  params.append('code', data.code)
  return api.post('/api/user/register', params, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  })
}

export const apiSendCode = (account: string, type: number = 1): Promise<ApiResponse<null>> => {
  const params = new URLSearchParams()
  params.append('account', account)
  params.append('type', type.toString())
  return api.post('/api/user/sendCode', params, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  })
}

export const getUserInfo = (): Promise<ApiResponse<UserInfo>> => {
  return api.get('/api/user/info')
}

// 检查手机号是否已注册
export const checkPhone = (phone: string): Promise<ApiResponse<boolean>> => {
  return api.get('/api/user/checkPhone', { params: { phone } })
}

// 检查邮箱是否已注册
export const checkEmail = (email: string): Promise<ApiResponse<boolean>> => {
  return api.get('/api/user/checkEmail', { params: { email } })
}

// 测试后端连接
export const testConnection = (): Promise<ApiResponse<string>> => {
  return api.get('/api/user/test')
}
