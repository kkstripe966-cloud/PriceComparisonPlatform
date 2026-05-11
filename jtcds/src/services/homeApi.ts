import api from './http'
import type { ApiResponse } from './authApi'

export interface Category {
  categoryId: number
  categoryName: string
  categoryIcon: string
  sortOrder: number
  isActive: boolean
  createTime: string
}

export interface Dish {
  dishId: number
  dishName: string
  description?: string
  mainImage: string
  minPrice: number
  maxPrice?: number
  avgPrice: number
  categoryId: number
  categoryName: string
  tags: string
  tagList: string[]
  isHot: boolean
  isRecommend: boolean
  viewCount: number
  compareCount: number
  status: number
  createTime: string
  updateTime: string
}

interface HomeData {
  categories: Category[]
  hotDishes: Dish[]
  recommendDishes: Dish[]  // 修正字段名
}

export const getHomeData = (): Promise<ApiResponse<HomeData>> => {
  return api.get('/api/home/index')
}

export const getCategories = (): Promise<ApiResponse<Category[]>> => {
  return api.get('/api/home/categories')
}

export const getHotDishes = (limit = 3): Promise<ApiResponse<Dish[]>> => {
  return api.get('/api/home/dishes/hot', { params: { limit } })
}

export const getRecommendedDishes = (limit = 5): Promise<ApiResponse<Dish[]>> => {
  return api.get('/api/home/dishes/recommend', { params: { limit } })
}

export const searchDishes = (
  keyword: string,
  page = 1,
  pageSize = 10,
): Promise<ApiResponse<Dish[]>> => {
  return api.get('/api/home/dishes/search', { params: { keyword, page, pageSize } })
}

export const getDishDetail = (dishId: number): Promise<ApiResponse<Dish>> => {
  return api.get(`/api/home/dish/${dishId}`)
}

export const recordCompare = (dishId: number): Promise<ApiResponse<null>> => {
  return api.post('/api/home/record/compare', null, { params: { dishId } })
}
