<template>
  <div class="api-test-page">
    <h1>API接口测试</h1>
    
    <div class="test-section">
      <h2>认证接口测试</h2>
      <div class="test-buttons">
        <button @click="testConnectionApi" :disabled="loading">测试连接</button>
        <button @click="testCheckEmail" :disabled="loading">检查邮箱</button>
        <button @click="testCheckPhone" :disabled="loading">检查手机号</button>
      </div>
      <div class="result" v-if="authResult">
        <h3>认证接口结果:</h3>
        <pre>{{ JSON.stringify(authResult, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h2>首页接口测试</h2>
      <div class="test-buttons">
        <button @click="testHomeData" :disabled="loading">获取首页数据</button>
        <button @click="testCategories" :disabled="loading">获取分类</button>
        <button @click="testHotDishes" :disabled="loading">获取热门菜品</button>
      </div>
      <div class="result" v-if="homeResult">
        <h3>首页接口结果:</h3>
        <pre>{{ JSON.stringify(homeResult, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h2>价格接口测试</h2>
      <div class="test-buttons">
        <button @click="testPriceHealth" :disabled="loading">健康检查</button>
        <button @click="testHotProducts" :disabled="loading">获取热门商品</button>
        <button @click="testPriceDetail" :disabled="loading">获取价格详情</button>
      </div>
      <div class="result" v-if="priceResult">
        <h3>价格接口结果:</h3>
        <pre>{{ JSON.stringify(priceResult, null, 2) }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { testConnection, checkEmail, checkPhone } from '@/services/authApi'
import { getHomeData, getCategories, getHotDishes } from '@/services/homeApi'
import { checkHealth, getHotProducts, getPriceDetail } from '@/services/priceApi'

const loading = ref(false)
const authResult = ref<any>(null)
const homeResult = ref<any>(null)
const priceResult = ref<any>(null)

const handleApiCall = async (apiCall: () => Promise<any>, resultRef: any) => {
  loading.value = true
  try {
    const result = await apiCall()
    resultRef.value = result
    console.log('API调用成功:', result)
  } catch (error) {
    resultRef.value = { error: error.message || '请求失败' }
    console.error('API调用失败:', error)
  } finally {
    loading.value = false
  }
}

// 认证接口测试
const testConnectionApi = () => handleApiCall(() => testConnection(), authResult)
const testCheckEmail = () => handleApiCall(() => checkEmail('test@example.com'), authResult)
const testCheckPhone = () => handleApiCall(() => checkPhone('13800138000'), authResult)

// 首页接口测试
const testHomeData = () => handleApiCall(() => getHomeData(), homeResult)
const testCategories = () => handleApiCall(() => getCategories(), homeResult)
const testHotDishes = () => handleApiCall(() => getHotDishes(5), homeResult)

// 价格接口测试
const testPriceHealth = () => handleApiCall(() => checkHealth(), priceResult)
const testHotProducts = () => handleApiCall(() => getHotProducts(5), priceResult)
const testPriceDetail = () => handleApiCall(() => getPriceDetail(1), priceResult)
</script>

<style scoped>
.api-test-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.test-buttons {
  margin: 15px 0;
}

.test-buttons button {
  margin-right: 10px;
  margin-bottom: 10px;
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.test-buttons button:hover {
  background: #0056b3;
}

.test-buttons button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.result {
  margin-top: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
}

.result pre {
  background: #fff;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  max-height: 300px;
  overflow-y: auto;
}
</style>