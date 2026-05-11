<template>
  <div class="home-page">
    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 搜索区域 -->
      <section class="search-section">
        <div class="search-container">
          <div class="search-box">
            <div class="search-input-wrapper">
              <svg class="search-icon" viewBox="0 0 24 24" fill="none">
                <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
                <path d="m21 21-4.35-4.35" stroke="currentColor" stroke-width="2"/>
              </svg>
              <input
                v-model="q"
                class="search-input"
                placeholder="搜索你想吃的美食..."
                @keyup.enter="onSearch"
              />
              <button class="voice-btn" title="语音搜索">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M12 2a3 3 0 0 0-3 3v6a3 3 0 0 0 6 0V5a3 3 0 0 0-3-3Z" stroke="currentColor" stroke-width="2"/>
                  <path d="M19 10v1a7 7 0 0 1-14 0v-1" stroke="currentColor" stroke-width="2"/>
                  <line x1="12" y1="19" x2="12" y2="23" stroke="currentColor" stroke-width="2"/>
                  <line x1="8" y1="23" x2="16" y2="23" stroke="currentColor" stroke-width="2"/>
                </svg>
              </button>
            </div>
            <button class="search-btn" @click="onSearch">
              <svg viewBox="0 0 24 24" fill="none">
                <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
                <path d="m21 21-4.35-4.35" stroke="currentColor" stroke-width="2"/>
              </svg>
            </button>
          </div>
        </div>
      </section>

      <!-- 分类导航 -->
      <section class="categories-section">
        <div class="categories-container">
          <h3 class="section-title">美食分类</h3>
          <div class="categories-pills">
            <button
              v-for="c in categories"
              :key="c.categoryId"
              class="category-pill"
              :class="{ active: currentCategory === c.categoryId }"
              @click="filterCategory(c.categoryId)"
            >
              <span class="category-emoji">{{ getCategoryEmoji(c.categoryName) }}</span>
              <span class="category-text">{{ c.categoryName }}</span>
            </button>
          </div>
        </div>
      </section>

      <!-- 菜品展示区域 -->
      <section class="dishes-section">
        <div class="dishes-container">
          <div class="dishes-grid" v-if="paginatedItems.length > 0">
            <article v-for="item in paginatedItems" :key="item.dishId" class="dish-card">
              <div class="dish-image">
                <img :src="item.mainImage || placeholderImage" :alt="item.dishName" />
                <div class="dish-badges">
                  <span v-if="item.isHot" class="badge hot">🔥 热门</span>
                  <span v-if="item.isRecommend" class="badge recommend">⭐ 推荐</span>
                </div>
              </div>
              
              <div class="dish-content">
                <h4 class="dish-title">{{ item.dishName }}</h4>
                <div class="dish-tags">
                  <span v-for="tag in item.tagList?.slice(0, 3)" :key="tag" class="tag">
                    {{ tag }}
                  </span>
                </div>
                <div class="dish-footer">
                  <div class="price-info">
                    <span class="price">￥{{ item.minPrice }}</span>
                    <span class="price-suffix">起</span>
                  </div>
                  <button class="compare-btn" @click="goCompare(item)">
                    <svg viewBox="0 0 24 24" fill="none">
                      <path d="M9 11H5a2 2 0 0 0-2 2v7a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2v-7a2 2 0 0 0-2-2Z" stroke="currentColor" stroke-width="2"/>
                      <path d="M19 4H15a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2Z" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    比价
                  </button>
                </div>
              </div>
            </article>
          </div>

          <!-- 分页组件 -->
          <div v-if="filteredItems.length > 0" class="pagination">
            <div class="pagination-info">
              <div class="pagination-stats">
                共 {{ filteredItems.length }} 个菜品，第 {{ currentPage }} / {{ totalPages }} 页
              </div>
              <div class="page-size-selector">
                <label>每页显示：</label>
                <select v-model="pageSize" @change="onPageSizeChange" class="page-size-select">
                  <option :value="6">6条</option>
                  <option :value="12">12条</option>
                  <option :value="24">24条</option>
                  <option :value="48">48条</option>
                </select>
              </div>
            </div>
            <div class="pagination-controls" v-if="totalPages > 1">
              <button 
                class="pagination-btn" 
                :disabled="currentPage === 1"
                @click="goToPage(currentPage - 1)"
              >
                <svg viewBox="0 0 24 24" fill="none">
                  <polyline points="15,18 9,12 15,6" stroke="currentColor" stroke-width="2"/>
                </svg>
                上一页
              </button>
              
              <div class="pagination-numbers">
                <template v-for="page in visiblePages" :key="page">
                  <button
                    v-if="page > 0"
                    class="pagination-number"
                    :class="{ active: page === currentPage }"
                    @click="goToPage(page)"
                  >
                    {{ page }}
                  </button>
                  <span v-else class="pagination-ellipsis">...</span>
                </template>
              </div>
              
              <button 
                class="pagination-btn" 
                :disabled="currentPage === totalPages"
                @click="goToPage(currentPage + 1)"
              >
                下一页
                <svg viewBox="0 0 24 24" fill="none">
                  <polyline points="9,18 15,12 9,6" stroke="currentColor" stroke-width="2"/>
                </svg>
              </button>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <div class="empty-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <path d="M8 14s1.5 2 4 2 4-2 4-2" stroke="currentColor" stroke-width="2"/>
                <line x1="9" y1="9" x2="9.01" y2="9" stroke="currentColor" stroke-width="2"/>
                <line x1="15" y1="9" x2="15.01" y2="9" stroke="currentColor" stroke-width="2"/>
              </svg>
            </div>
            <h4>暂无相关美食</h4>
            <p>试试搜索其他关键词或选择不同分类</p>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHomeData, searchDishes, type Category, type Dish } from '@/services/homeApi'

defineOptions({ name: 'HomePage' })

const router = useRouter()

// 分页相关
const currentPage = ref(1)
const pageSize = ref(6) // 每页显示6个菜品，便于测试分页功能

// 占位图片
const placeholderImage = '/images/default.jpg'

// 搜索关键字
const q = ref('')
async function onSearch() {
  if (!q.value.trim()) {
    // 如果搜索词为空，重新加载首页数据
    loadHomeData()
    return
  }
  try {
    const res = await searchDishes(q.value.trim())
    if (res.success) {
      items.value = res.data
      // 搜索时重置到第一页
      currentPage.value = 1
    } else {
      alert(`搜索失败: ${res.message}`)
    }
  } catch (error) {
    console.error('Search error:', error)
    alert('搜索请求失败')
  }
}

// 类目
const categories = ref<Category[]>([])
const currentCategory = ref<number>(0)

function filterCategory(key: number) {
  if (currentCategory.value === key) {
    currentCategory.value = 0
  } else {
    currentCategory.value = key
  }
  // 切换分类时重置到第一页
  currentPage.value = 1
}

// 分页方法
function goToPage(page: number) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    // 滚动到顶部
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

// 每页条数变化处理
function onPageSizeChange() {
  // 重置到第一页
  currentPage.value = 1
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 列表数据
const items = ref<Dish[]>([])
const allItems = ref<Dish[]>([]) // 保存从首页加载的所有菜品

const filteredItems = computed(() => {
  if (currentCategory.value === 0) {
    return items.value
  }
  return allItems.value.filter((i) => i.categoryId === currentCategory.value)
})

// 分页计算属性
const totalPages = computed(() => {
  return Math.ceil(filteredItems.value.length / pageSize.value)
})

const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredItems.value.slice(start, end)
})

const visiblePages = computed(() => {
  const total = totalPages.value
  const current = currentPage.value
  const pages: number[] = []
  
  if (total <= 7) {
    // 如果总页数小于等于7，显示所有页码
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // 总是显示第一页
    pages.push(1)
    
    if (current <= 4) {
      // 当前页在前面时
      for (let i = 2; i <= 5; i++) {
        pages.push(i)
      }
      pages.push(-1) // 省略号
      pages.push(total)
    } else if (current >= total - 3) {
      // 当前页在后面时
      pages.push(-1) // 省略号
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      // 当前页在中间时
      pages.push(-1) // 省略号
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push(-1) // 省略号
      pages.push(total)
    }
  }
  
  return pages
})

function goCompare(item: Dish) {
  router.push(`/price?dishId=${item.dishId}`)
}

// 获取分类对应的emoji
function getCategoryEmoji(categoryName: string): string {
  const emojiMap: Record<string, string> = {
    '全部': '🍽️',
    '火锅': '🍲',
    '烧烤': '🍖',
    '面条': '🍜',
    '便当': '🍱',
    '披萨': '🍕',
    '汉堡': '🍔',
    '寿司': '🍣',
    '奶茶': '🧋',
    '川菜': '🌶️',
    '粤菜': '🦐',
    '湘菜': '🌶️',
    '鲁菜': '🐟',
    '苏菜': '🦀',
    '浙菜': '🐠',
    '闽菜': '🦪',
    '徽菜': '🐷',
    '日料': '🍱',
    '韩料': '🥘',
    '西餐': '🥩',
    '甜品': '🍰',
    '饮品': '🥤',
    '小食': '�',
    '早餐': '🥐',
    '夜宵': '�'
  }
  return emojiMap[categoryName] || '🍽️'
}

// 加载首页数据
async function loadHomeData() {
  try {
    const res = await getHomeData()
    if (res.code === 200) {
      categories.value = [
        { categoryId: 0, categoryName: '全部', categoryIcon: '/assets/全部.svg' } as Category,
        ...res.data.categories.map(cat => ({
          ...cat,
          categoryIcon: `/assets/${cat.categoryName}.svg`
        })),
      ]
      const allDishes = [...(res.data.hotDishes || []), ...(res.data.recommendDishes || [])]
      // 处理图片路径和去重
      const processedDishes = allDishes.map(dish => ({
        ...dish,
        mainImage: dish.mainImage?.startsWith('/') ? dish.mainImage : `/images/${dish.mainImage || 'default.jpg'}`
      }))
      const uniqueDishes = Array.from(new Map(processedDishes.map((d) => [d.dishId, d])).values())
      items.value = uniqueDishes
      allItems.value = uniqueDishes
    } else {
      alert(`加载首页数据失败: ${res.message}`)
    }
  } catch (error) {
    console.error('Failed to load home data:', error)
    alert('网络错误，无法加载首页数据')
  }
}

onMounted(() => {
  // 加载数据
  loadHomeData()
})
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.home-page {
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  color: #1e293b;
  transition: all 0.3s ease;
}

/* 主要内容区域 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 搜索区域 */
.search-section {
  padding: 24px 0;
}

.search-container {
  max-width: 600px;
  margin: 0 auto;
}

.search-box {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input-wrapper {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 16px;
  padding: 0 20px;
  transition: all 0.2s ease;
}

.search-input-wrapper:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-icon {
  width: 20px;
  height: 20px;
  color: #94a3b8;
  margin-right: 12px;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  padding: 14px 0;
  font-size: 1rem;
  color: #1e293b;
  background: transparent;
}

.search-input::placeholder {
  color: #94a3b8;
}

.voice-btn {
  border: none;
  background: transparent;
  color: #94a3b8;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: color 0.2s ease;
}

.voice-btn:hover {
  color: #667eea;
}

.voice-btn svg {
  width: 20px;
  height: 20px;
}

.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 14px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 14px 0 rgba(102, 126, 234, 0.3);
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 25px 0 rgba(102, 126, 234, 0.4);
}

.search-btn svg {
  width: 20px;
  height: 20px;
}

/* 分类导航 */
.categories-section {
  padding: 40px 0;
}

.categories-container {
  margin-bottom: 20px;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 24px;
  color: #1e293b;
}

.categories-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: flex-start;
}

.category-pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.95rem;
  font-weight: 500;
  color: #64748b;
  white-space: nowrap;
}

.category-pill:hover {
  border-color: #667eea;
  background: #f8fafc;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.category-pill.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

.category-emoji {
  font-size: 1.1rem;
  line-height: 1;
}

.category-text {
  font-weight: 500;
}

/* 菜品展示区域 */
.dishes-section {
  padding: 20px 0 120px;
}

.dishes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.dish-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.dish-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.dish-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.dish-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.dish-card:hover .dish-image img {
  transform: scale(1.05);
}

.dish-badges {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  backdrop-filter: blur(10px);
}

.badge.hot {
  background: rgba(239, 68, 68, 0.9);
  color: white;
}

.badge.recommend {
  background: rgba(245, 158, 11, 0.9);
  color: white;
}

.dish-content {
  padding: 20px;
}

.dish-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin: 0 0 12px;
  color: #1e293b;
  line-height: 1.4;
}

.dish-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 16px;
}

.tag {
  padding: 4px 8px;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 500;
}

.dish-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price {
  font-size: 1.25rem;
  font-weight: 700;
  color: #dc2626;
}

.price-suffix {
  font-size: 0.875rem;
  color: #64748b;
}

.compare-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.compare-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.compare-btn svg {
  width: 16px;
  height: 16px;
}

/* 分页组件 */
.pagination {
  margin-top: 40px;
  padding: 20px 0;
}

.pagination-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 8px;
}

.pagination-stats {
  color: #64748b;
  font-size: 0.9rem;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 0.9rem;
}

.page-size-selector label {
  font-weight: 500;
}

.page-size-select {
  padding: 6px 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  color: #1e293b;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-size-select:hover {
  border-color: #667eea;
}

.page-size-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.pagination-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  color: #64748b;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-btn:hover:not(:disabled) {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-1px);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.pagination-btn svg {
  width: 16px;
  height: 16px;
}

.pagination-numbers {
  display: flex;
  align-items: center;
  gap: 4px;
  margin: 0 16px;
}

.pagination-number {
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 8px;
  color: #64748b;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-number:hover {
  background: #f1f5f9;
  color: #1e293b;
}

.pagination-number.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.pagination-ellipsis {
  padding: 0 8px;
  color: #94a3b8;
  font-size: 0.9rem;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #64748b;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  color: #cbd5e1;
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-state h4 {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0 0 8px;
  color: #475569;
}

.empty-state p {
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    padding: 0 16px;
  }
  
  .search-section {
    padding: 20px 0;
  }
  
  .search-box {
    flex-direction: column;
  }
  
  .search-btn {
    width: 100%;
    justify-content: center;
  }
  
  .categories-pills {
    gap: 8px;
    justify-content: center;
  }
  
  .category-pill {
    padding: 10px 16px;
    font-size: 0.9rem;
  }
  
  .category-emoji {
    font-size: 1rem;
  }
  
  .dishes-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .dishes-section {
    padding: 16px 0 100px;
  }
  
  .pagination {
    margin-top: 24px;
    gap: 6px;
  }
  
  .pagination-info {
    flex-direction: column;
    gap: 12px;
    align-items: center;
  }
  
  .pagination-btn {
    padding: 10px 12px;
    font-size: 0.85rem;
  }
  
  .pagination-numbers {
    margin: 0 8px;
    gap: 2px;
  }
  
  .pagination-number {
    width: 36px;
    height: 36px;
    font-size: 0.85rem;
  }
  
  .page-size-selector {
    font-size: 0.85rem;
  }
  
  .page-size-select {
    padding: 4px 8px;
    font-size: 0.85rem;
  }
}

@media (max-width: 480px) {
  .search-section {
    padding: 16px 0;
  }
  
  .categories-pills {
    gap: 6px;
  }
  
  .category-pill {
    padding: 8px 14px;
    font-size: 0.85rem;
  }
  
  .category-emoji {
    font-size: 0.95rem;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dish-card {
  animation: fadeInUp 0.5s ease forwards;
}

.dish-card:nth-child(1) { animation-delay: 0.1s; }
.dish-card:nth-child(2) { animation-delay: 0.2s; }
.dish-card:nth-child(3) { animation-delay: 0.3s; }
.dish-card:nth-child(4) { animation-delay: 0.4s; }
</style>
