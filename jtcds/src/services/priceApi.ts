import api from './http'

// 统一响应：兼容 success/code 两种格式
export type ApiEnvelope<T> = {
  data: T
  message?: string
  success?: boolean
  code?: number
}

export interface PriceItem {
  platform: string
  platformName: string
  originalPrice: number
  currentPrice: number
  deliveryFee?: number
  finalPrice: number
  discount?: number
  couponInfo?: string
  rating?: number
  salesMonth?: number
  isBestChoice?: boolean
}

export interface SimilarProduct {
  productId: number
  productName: string
  minPrice: number
}

export interface PriceDetail {
  productId: number
  productName: string
  productDescription?: string
  productImage?: string
  category?: string
  tags?: string[]
  popularity?: number
  priceList: PriceItem[]
  lowestPrice: number
  lowestPricePlatform: string
  averagePrice?: number
  saveAmount?: number
  savePercent?: number
  similarProducts?: SimilarProduct[]
  warningMessage?: string
  buySuggestion?: string
}

const BASE = '/api/price'

export const checkHealth = (): Promise<ApiEnvelope<{ status: string }>> => {
  return api.get(`${BASE}/health`)
}

export const getPriceDetail = (productId: number): Promise<ApiEnvelope<PriceDetail>> => {
  return api.get(`${BASE}/detail/${productId}`)
}

export const getHotProducts = (limit = 10): Promise<ApiEnvelope<SimilarProduct[]>> => {
  return api.get(`${BASE}/hot`, { params: { limit } })
}

export const refreshPrice = (productId: number): Promise<ApiEnvelope<null>> => {
  return api.post(`${BASE}/refresh/${productId}`)
}

// 搜索商品
export const searchProducts = (keyword: string): Promise<ApiEnvelope<SimilarProduct[]>> => {
  return api.get(`${BASE}/search`, { params: { keyword } })
}

// 获取价格趋势
export const getPriceTrend = (productId: number, platform: string, days = 7): Promise<ApiEnvelope<any>> => {
  return api.get(`${BASE}/trend/${productId}/${platform}`, { params: { days } })
}

// 获取最优购买方案
export const getBestPlan = (productId: number): Promise<ApiEnvelope<any>> => {
  return api.get(`${BASE}/best-plan/${productId}`)
}

// 获取所有平台价格对比
export const compareAllPrices = (productId: number): Promise<ApiEnvelope<any>> => {
  return api.get(`${BASE}/compare/${productId}`)
}
