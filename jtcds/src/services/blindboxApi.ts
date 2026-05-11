import axios from 'axios'
import type { Dish as HomeDish } from './homeApi'
import type { RegisterData } from '@/types'

// 独立的 axios 实例，避免影响现有 8081 服务
const blindbox = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
})

// 请求拦截：附带 Token 与 JSON 头
blindbox.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers = config.headers ?? {}
      config.headers.Authorization = `Bearer ${token}`
    }
    config.headers = config.headers ?? {}
    config.headers['Content-Type'] = 'application/json'
    return config
  },
  (err) => Promise.reject(err),
)

// 响应拦截：统一错误处理
blindbox.interceptors.response.use(
  (res) => res.data,
  (err) => {
    if (typeof err?.message === 'string' && err.message.includes('timeout')) {
      console.error('请求超时，请检查网络！')
    } else if (err?.response?.data) {
      const payload = err.response.data as { code?: number; message?: string }
      const code = payload?.code ?? err.response.status
      const message = payload?.message ?? '未知错误'
      console.error(`后端错误：${code} - ${message}`)
    } else {
      console.error('请求失败：', err?.message ?? err)
    }
    return Promise.reject(err)
  },
)

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface Theme {
  id: number
  name: string
  dayName: string
  nightName: string
  dayIcon: string
  nightIcon: string
  description: string
  createdAt: string
}

export interface User {
  id: number
  username: string
  email: string
  coinBalance: number
  createdAt: string
  lastLogin?: string
}

// 复用 Home 的 Dish 结构作为返回菜品类型
export type Dish = HomeDish

export interface DrawResult {
  success: boolean
  dish: Dish | unknown
  timeMode: string
  priceTier: string
  drawTime: number
}

export interface DrawStats {
  total: number
  successRate: number
  [k: string]: number | unknown
}

export const getThemes = (): Promise<ApiResponse<Theme[]>> => blindbox.get('/themes')
export const getTheme = (id: number): Promise<ApiResponse<Theme>> => blindbox.get(`/themes/${id}`)
export const searchThemes = (name: string): Promise<ApiResponse<Theme[]>> =>
  blindbox.get(`/themes/name/${encodeURIComponent(name)}`)

export const drawRandom = (minPrice: number, maxPrice: number): Promise<ApiResponse<DrawResult>> =>
  blindbox.post('/draw/random', { minPrice, maxPrice })

export const redraw = (drawType: 'RANDOM' | 'PRECISE', options?: {
  budgetType?: string,
  flavorTypes?: string[],
  minPrice?: number,
  maxPrice?: number
}): Promise<ApiResponse<DrawResult>> =>
  blindbox.post('/draw/redraw', {
    drawType,
    budgetType: options?.budgetType,
    flavorTypes: options?.flavorTypes,
    minPrice: options?.minPrice,
    maxPrice: options?.maxPrice
  })

export const drawPrecise = (budgetType: string, flavorTypes: string[], minPrice?: number, maxPrice?: number): Promise<ApiResponse<DrawResult>> =>
  blindbox.post('/draw/precise', { budgetType, flavorTypes, minPrice, maxPrice })

export const getTimeMode = (): Promise<ApiResponse<string>> => blindbox.get('/draw/time-mode')
export const getDrawStats = (): Promise<ApiResponse<DrawStats>> => blindbox.get('/draw/stats')

// 登录/注册接口（按文档 /api/users/*）
export const register = (data: RegisterData): Promise<ApiResponse<User>> =>
  blindbox.post('/users/register', data)

export const login = (
  username: string,
  password: string,
): Promise<ApiResponse<{ token: string; user: User }>> =>
  blindbox.post('/users/login', { username, password })

export default {
  getThemes,
  getTheme,
  searchThemes,
  drawRandom,
  redraw,
  drawPrecise,
  getTimeMode,
  getDrawStats,
  register,
  login,
}
