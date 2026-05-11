export interface LoginData {
  username: string
  password: string
}

export interface RegisterData {
  username: string // 可以是手机号或邮箱
  password: string
  email: string
  code: string
}

// 统一API响应格式
export interface ApiResponse<T> {
  success: boolean
  code?: number
  message: string
  data: T
  timestamp?: number
}
