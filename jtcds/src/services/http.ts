import axios, { type AxiosResponse } from 'axios'

const baseURL = import.meta.env.VITE_MAIN_API_BASE_URL || 'http://192.168.31.237:8081'

const api = axios.create({
  baseURL,
  timeout: 10000,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// By typing the response here, we tell TS that the promise will resolve with the data object directly
api.interceptors.response.use(
  (response: AxiosResponse) => {
    // 处理不同的响应格式
    const responseData = response.data
    
    // 如果后端返回的是标准格式 {code, message, data}
    if (responseData && responseData.hasOwnProperty('code')) {
      return {
        success: responseData.code === 200,
        code: responseData.code,
        message: responseData.message,
        data: responseData.data
      }
    }
    
    // 如果后端返回的是 {success, message, data} 格式
    if (responseData && responseData.hasOwnProperty('success')) {
      return responseData
    }
    
    // 如果是直接返回数据，包装成标准格式
    return {
      success: true,
      code: 200,
      message: 'success',
      data: responseData
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  },
)

export default api
