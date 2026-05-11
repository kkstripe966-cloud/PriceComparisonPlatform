<template>
  <div class="blindbox-page" :class="{ dark: step === 'result' }">
    <!-- 入口页：时空美食盲盒 + 两个入口按钮 -->
    <section v-if="step === 'entry'" class="entry">
      <div class="entry-mid">
        <div class="center-brand">
          <span class="brand-dot"></span>
          <div>
            <div class="center-name">时空美食盲盒</div>
            <div class="center-sub">Space-Time Gourmet Capsule</div>
          </div>
        </div>
        <div class="timer-strip">
          <div class="time-list">
            <template v-for="(part, idx) in timerParts" :key="idx">
              <div class="time-cell">{{ part }}</div>
              <span v-if="idx < timerParts.length - 1" class="time-sep">:</span>
            </template>
          </div>
          <div class="mode-pill">{{ modeIcon }} {{ modeText }}</div>
        </div>
      </div>
      <div class="hero">
        <img class="mascot" src="/assets/login-register.jpg" alt="盲盒主图" />
      </div>
      <div class="actions">
        <button class="action secondary" @click="startRandom">
          <span class="icon">🎲</span>
          <span class="text">随机穿越</span>
        </button>
        <button class="action primary" @click="toPrecise">
          <span class="icon">🎯</span>
          <span class="text">精准匹配</span>
        </button>
      </div>
    </section>

    <!-- 精准匹配页：预算范围 + 口味偏好 + 开始匹配 -->
    <section v-else-if="step === 'precise'" class="precise">
      <header class="precise-top">
        <div class="title">精准匹配</div>
        <button class="home-btn" @click="backToEntry" title="返回首页">
          <img class="home-icon" src="/assets/首页.svg" alt="首页" />
        </button>
      </header>
      <div class="budget">
        <div class="label">预算范围</div>
        <div class="grid">
          <button
            v-for="b in budgets"
            :key="b.key"
            class="chip"
            :class="{ active: selectedBudget === b.key }"
            @click="selectedBudget = b.key"
          >
            <span class="chip-icon">
              <img v-if="isIconPath(b.icon)" class="chip-img" :src="b.icon" :alt="b.name" />
              <span v-else>{{ b.icon }}</span>
            </span>
            <span class="chip-text">{{ b.name }}</span>
          </button>
        </div>
      </div>
      <div class="flavors">
        <div class="label">想吃什么味</div>
        <div class="cards">
          <button
            v-for="f in flavors"
            :key="f.key"
            class="pref-card"
            :class="{ active: selectedFlavors.has(f.key) }"
            @click="toggleFlavor(f.key)"
          >
            <div class="pref-icon">{{ f.icon }}</div>
            <div class="pref-text">{{ f.name }}</div>
          </button>
        </div>
      </div>
      <footer class="precise-footer">
        <button class="match-btn" @click="startPrecise">开始匹配</button>
      </footer>
    </section>

    <!-- 精准匹配结果页（浅色卡片列表） -->
    <section v-else-if="step === 'preciseResult'" class="precise-result">
      <header class="list-top">
        <button class="back" @click="backToEntry">←</button>
        <div class="context">
          <span class="ctx-left">{{ budgetName }}</span>
          <span class="ctx-right">{{ flavorName }}</span>
        </div>
      </header>
      <div class="rec-list">
        <article
          v-for="(dish, idx) in recommendations"
          :key="dish.id"
          class="rec-card"
          @click="goCompare(dish)"
        >
          <div class="badge">No.{{ idx + 1 }} 推荐</div>
          <div class="title">{{ dish.title }}</div>
          <div class="subtitle">精选食材，风味兼具</div>
          <div class="tags">
            <div class="tag" v-for="t in dish.tags" :key="t">
              <span class="dot"></span>
              <span class="txt">{{ t }}</span>
            </div>
          </div>
          <div class="price-corner">
            <span class="price">￥{{ dish.from }}</span>
          </div>
        </article>
      </div>
      <footer class="list-footer">点击卡片查看详情或直接下单</footer>
    </section>

    <!-- 随机匹配：加载页 -->
    <section v-else-if="step === 'loading'" class="loading">
      <div class="spinner"></div>
      <div class="loading-text">穿越时空中…</div>
    </section>

    <!-- 匹配结果页（暗色风格） -->
    <section v-else class="result">
      <header class="result-top">
        <div class="brand">
          <img class="brand-logo" src="/assets/logo.svg" alt="Logo" /><span class="name"
            >随机穿越</span
          >
        </div>
        <div class="right"><span class="rate">预算偏好</span><span class="num">85%</span></div>
      </header>
      <div class="result-hero">
        <img class="mascot" :src="selectedItem?.imageUrl || '/assets/login-register.jpg'" :alt="selectedItem?.title || '匹配主图'" />
      </div>
      <article class="result-card" v-if="selectedItem">
        <div class="dish-title">{{ selectedItem.title }}</div>
        <ul class="dish-desc">
          <li v-for="t in selectedItem.tags" :key="t">{{ t }}</li>
        </ul>
        <div class="price">￥{{ selectedItem.from.toFixed(1) }}</div>
      </article>
      <footer class="result-actions">
        <button class="ok-btn" @click="goCompare(selectedItem!)">✓ 就吃这个</button>
        <button class="redo-btn" @click="redo">重新抽取</button>
      </footer>
      <button class="float-home" @click="backToEntry" title="返回">
        <img class="float-home-icon" src="/assets/首页.svg" alt="首页" />
      </button>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { items as itemsData, type FoodItem } from '../data/items'
import { getAllDishes } from '@/services/homeApi'
import {
  drawRandom as apiDrawRandom,
  redraw as apiRedraw,
  getTimeMode as apiGetTimeMode,
  type ApiResponse,
} from '@/services/blindboxApi'

const router = useRouter()
const items = ref<FoodItem[]>(itemsData) // 使用响应式数据

type Step = 'entry' | 'precise' | 'preciseResult' | 'loading' | 'result'
const step = ref<Step>('entry')
const selectedItem = ref<FoodItem | null>(null)
const recommendations = ref<FoodItem[]>([])
const clock = ref(new Date())
const timeMode = ref<string>('day')
const modeIcon = computed(() => (timeMode.value?.toLowerCase() === 'night' ? '🌙' : '☀'))
const modeText = computed(() =>
  timeMode.value?.toLowerCase() === 'night' ? '夜间模式' : '日间模式',
)
const timerParts = computed(() => {
  const d = clock.value
  return [d.getHours(), d.getMinutes(), d.getSeconds()].map((n) => String(n).padStart(2, '0'))
})
let clockHandle: number | undefined
onMounted(async () => {
  clockHandle = window.setInterval(() => {
    clock.value = new Date()
  }, 1000)
  
  // 获取时间模式（忽略错误，保持默认）
  try {
    const res: ApiResponse<string> = await apiGetTimeMode()
    if (res && res.code === 200) {
      timeMode.value = res.data
    }
  } catch (_e: unknown) {
    // 静默失败，保持默认 day
    void _e
  }
  
  // 加载真实的菜品数据
  await loadDishesData()
})
onUnmounted(() => {
  if (clockHandle) window.clearInterval(clockHandle)
})

// 加载真实的菜品数据
async function loadDishesData() {
  try {
    // 获取所有菜品，而不仅仅是热门菜品
    const response = await fetch('http://localhost:8081/api/home/index')
    const result = await response.json()
    
    if (response.ok && result.code === 200) {
      const data = result.data
      // 合并热门菜品和推荐菜品，获取更多样化的数据
      const allDishes = [...(data.hotDishes || []), ...(data.recommendDishes || [])]
      
      // 去重
      const uniqueDishes = Array.from(new Map(allDishes.map((d: any) => [d.dishId, d])).values())
      
      // 将后端菜品数据转换为FoodItem格式
      const convertedItems: FoodItem[] = uniqueDishes.map((dish: any) => ({
        id: String(dish.dishId),
        title: dish.dishName,
        imageUrl: dish.mainImage || '/images/default.jpg',
        tags: dish.tagList || [],
        from: dish.minPrice || 0,
        heat: dish.viewCount || 0,
        category: dish.categoryName || 'unknown',
      }))
      
      if (convertedItems.length > 0) {
        items.value = convertedItems
        console.log('盲盒数据加载成功，共', convertedItems.length, '个菜品')
        console.log('菜品列表:', convertedItems.map(item => item.title))
      }
    }
  } catch (error) {
    console.warn('加载菜品数据失败，使用默认数据:', error)
    // 保持使用默认的itemsData
  }
}

function backToEntry() {
  step.value = 'entry'
}
function toPrecise() {
  step.value = 'precise'
}
async function startRandom() {
  step.value = 'loading'
  const defaultMin = 20
  const defaultMax = 50
  try {
    const res = await apiDrawRandom(defaultMin, defaultMax)
    // 统一响应：{ code, message, data: { success, dish, ... }, timestamp }
    if (res.code === 200 && res.data) {
      const mapped = mapDishToFoodItem(res.data.dish)
      selectedItem.value = mapped
      step.value = 'result'
      return
    }
    throw new Error(res.message || '抽取失败')
  } catch (_e: unknown) {
    // 失败回退到本地随机，保证交互可用
    void _e
    selectedItem.value = pickRandom(items.value)
    step.value = 'result'
  }
}

function mapDishToFoodItem(dish: unknown): FoodItem {
  const obj: Record<string, unknown> =
    dish && typeof dish === 'object' ? (dish as Record<string, unknown>) : {}
  const title =
    (typeof obj.dishName === 'string' && obj.dishName) ||
    (typeof obj.title === 'string' && obj.title) ||
    '神秘料理'
  let tagList: string[] = []
  if (Array.isArray(obj.tagList)) tagList = obj.tagList as string[]
  else if (Array.isArray(obj.tags)) tagList = obj.tags as string[]
  else if (typeof obj.tags === 'string')
    tagList = String(obj.tags)
      .split(/[,\s]+/)
      .filter(Boolean)
  const price =
    (typeof obj.minPrice === 'number' && (obj.minPrice as number)) ||
    (typeof obj.avgPrice === 'number' && (obj.avgPrice as number)) ||
    (typeof obj.from === 'number' && (obj.from as number)) ||
    0
  const idVal =
    (typeof obj.dishId === 'number' && String(obj.dishId)) ||
    (typeof obj.id === 'number' && String(obj.id)) ||
    (typeof obj.id === 'string' && (obj.id as string)) ||
    String(Math.floor(Math.random() * 1000000))
  const imageUrl =
    (typeof obj.mainImage === 'string' && (obj.mainImage as string)) ||
    (typeof obj.img === 'string' && (obj.img as string)) ||
    (typeof obj.imageUrl === 'string' && (obj.imageUrl as string)) ||
    '/assets/login-register.jpg'
  return {
    id: idVal,
    title,
    imageUrl,
    tags: tagList,
    from: price,
    heat: 0,
    category: 'unknown',
  }
}

// 精准匹配数据
const budgets = [
  { key: 'economy', name: '经济实惠', icon: '💰' },
  { key: 'value', name: '性价比', icon: '/assets/性价比.svg' },
  { key: 'quality', name: '品质大餐', icon: '👑' },
  { key: 'luxury', name: '奢华享受', icon: '💎' },
]

function isIconPath(icon: string) {
  return icon.startsWith('/assets/') || icon.startsWith('http')
}
const selectedBudget = ref<string | null>(null)
const flavors = [
  { key: 'spicy', name: '川渝麻辣', icon: '🔥' },
  { key: 'light', name: '时式清淡', icon: '🥗' },
  { key: 'healthy', name: '健康轻食', icon: '🍃' },
  { key: 'meat', name: '大口吃肉', icon: '🍖' },
  { key: 'drink', name: '快乐速饮', icon: '📡' },
  { key: 'tea', name: '下午茶', icon: '☕' },
  { key: 'exotic', name: '异国风味', icon: '💠' },
]
const selectedFlavors = ref<Set<string>>(new Set())
function toggleFlavor(key: string) {
  const s = new Set(selectedFlavors.value)
  if (s.has(key)) s.delete(key)
  else s.add(key)
  selectedFlavors.value = s
}

const budgetName = computed(
  () => budgets.find((b) => b.key === selectedBudget.value)?.name || '综合推荐',
)
const flavorName = computed(() => {
  const [first] = Array.from(selectedFlavors.value)
  const f = flavors.find((x) => x.key === first)
  return f ? f.name : '全部口味'
})

function flavorMatches(item: FoodItem, key: string) {
  const title = item.title.toLowerCase()
  const tags = item.tags.join(' ').toLowerCase()
  
  let matches = false
  switch (key) {
    case 'spicy':
      matches = /辣|麻辣|川菜|四川/.test(title) || /辣|麻辣|川菜|四川/.test(tags)
      break
    case 'light':
      matches = /清淡|素|沙拉|轻食|蔬菜|西兰花/.test(title) || /清淡|轻食|素食|蔬菜/.test(tags)
      break
    case 'healthy':
      matches = /轻食|沙拉|低卡|健康|蔬菜|果汁|绿豆/.test(title) || /健康|轻食|维生素|营养/.test(tags)
      break
    case 'meat':
      matches = /牛|猪|鸡|肉|排|堡|红烧|回锅|里脊/.test(title) || /牛|鸡|猪|肉|红烧/.test(tags)
      break
    case 'drink':
      matches = /奶茶|茶|饮|咖啡|果汁|拿铁|柠檬|绿豆/.test(title) || /饮品|奶茶|咖啡|果汁/.test(tags)
      break
    case 'tea':
      matches = /下午茶|茶|甜品|布丁|蛋糕|提拉米苏/.test(title) || /甜品|茶|咖啡|意式/.test(tags)
      break
    case 'exotic':
      matches = /寿司|披萨|汉堡|意面|意大利|牛排|西餐/.test(title) || /西式|意式|西餐/.test(tags)
      break
    default:
      matches = false
  }
  
  if (matches) {
    console.log(`口味匹配: ${item.title} 匹配 ${key} (标签: ${item.tags.join(', ')})`)
  }
  
  return matches
}

function pickRandom<T>(arr: T[]): T {
  return arr[Math.floor(Math.random() * arr.length)]!
}

function startPrecise() {
  const s = Array.from(selectedFlavors.value)
  let candidates = items.value.slice()
  
  console.log('开始精准匹配，总菜品数:', candidates.length)
  console.log('选择的口味:', s)
  console.log('选择的预算:', selectedBudget.value)
  
  if (s.length > 0) {
    candidates = candidates.filter((i) => s.some((k) => flavorMatches(i, k)))
    console.log('口味匹配后的候选菜品:', candidates.map(c => c.title))
  }
  
  // 预算优先级：economy 最便宜，luxury 最贵，value 取中位，quality 偏高
  if (candidates.length === 0) {
    console.log('没有匹配的菜品，使用全部菜品')
    candidates = items.value.slice()
  }
  
  // 按价格排序
  candidates.sort((a, b) => a.from - b.from)
  console.log('排序后的候选菜品:', candidates.map(c => `${c.title}(¥${c.from})`))
  
  let chosen: FoodItem
  switch (selectedBudget.value) {
    case 'economy':
      chosen = candidates[0]!
      break
    case 'value':
      chosen = candidates[Math.floor(candidates.length / 2)]!
      break
    case 'quality':
      chosen = candidates[Math.min(candidates.length - 1, Math.floor(candidates.length * 0.75))]!
      break
    case 'luxury':
      chosen = candidates[candidates.length - 1]!
      break
    default:
      // 如果没有选择预算，随机选择
      chosen = pickRandom(candidates)
  }
  
  console.log('最终选择的菜品:', chosen.title)
  
  // 生成 Top3 推荐列表
  let list: FoodItem[] = []
  switch (selectedBudget.value) {
    case 'economy':
      list = candidates.slice(0, 3)
      break
    case 'luxury':
      list = candidates.slice(-3)
      break
    case 'quality':
      list = candidates.slice(
        Math.max(0, candidates.length - 4),
        Math.max(0, candidates.length - 1),
      )
      break
    case 'value':
      const mid = Math.floor(candidates.length / 2)
      list = candidates.slice(Math.max(0, mid - 1), Math.min(candidates.length, mid + 2))
      break
    default:
      // 无预算选择则随机抽取 3 个不同项
      const pool = candidates.slice()
      while (list.length < 3 && pool.length > 0) {
        const pick = pool.splice(Math.floor(Math.random() * pool.length), 1)[0]
        if (pick) list.push(pick)
      }
  }
  
  recommendations.value = list.length ? list : [chosen]
  selectedItem.value = chosen
  step.value = 'preciseResult'
}

async function redo() {
  step.value = 'loading'
  
  try {
    let res: ApiResponse<any>
    
    // 重新抽取保持当前模式：若来自精准匹配则精准，否则随机
    if (selectedBudget.value || selectedFlavors.value.size > 0) {
      // 精准匹配重新抽取
      res = await apiRedraw('PRECISE', {
        budgetType: selectedBudget.value || undefined,
        flavorTypes: Array.from(selectedFlavors.value),
        minPrice: 20,
        maxPrice: 50
      })
    } else {
      // 随机重新抽取
      res = await apiRedraw('RANDOM', {
        minPrice: 20,
        maxPrice: 50
      })
    }
    
    // 统一响应处理
    if (res.code === 200 && res.data) {
      const mapped = mapDishToFoodItem(res.data.dish)
      selectedItem.value = mapped
      step.value = 'result'
      return
    }
    throw new Error(res.message || '重新抽取失败')
  } catch (_e: unknown) {
    // 失败回退到本地随机，保证交互可用
    void _e
    console.warn('API重新抽取失败，使用本地数据:', _e)
    
    // 本地重新抽取逻辑
    if (selectedBudget.value || selectedFlavors.value.size > 0) {
      startPrecise()
    } else {
      selectedItem.value = pickRandom(items.value)
      step.value = 'result'
    }
  }
}

function goHome() {
  router.push('/home')
}
function goCompare(dish: FoodItem) {
  router.push(`/price?dishId=${dish.id}`)
}
</script>

<style scoped>
.precise-result {
  padding: 12px 16px 24px;
}
.list-top {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 8px;
}
.back {
  border: none;
  background: #fff;
  border-radius: 10px;
  padding: 6px 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
}
.context {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  color: #a0aec0;
  font-weight: 700;
}
.ctx-left {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.ctx-right {
  color: #ef4444;
}
.rec-list {
  display: grid;
  gap: 16px;
}
.rec-card {
  position: relative;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  padding: 16px;
  overflow: hidden;
}
.rec-card .badge {
  background: #ffefef;
  color: #ff6b6b;
  border-radius: 10px;
  padding: 4px 8px;
  font-size: 12px;
  display: inline-block;
  margin-bottom: 8px;
}
.rec-card .title {
  font-size: 20px;
  font-weight: 800;
  color: #f59e0b;
}
.rec-card .subtitle {
  margin-top: 6px;
  color: #9ca3af;
}
.rec-card .tags {
  display: flex;
  gap: 20px;
  padding-top: 12px;
}
.rec-card .tag {
  display: grid;
  place-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 12px;
}
.rec-card .dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, #fff, #e6f0ff);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.06);
}
.rec-card .txt {
  color: #64748b;
}
.price-corner {
  position: absolute;
  right: 0;
  top: 0;
  width: 140px;
  height: 100px;
  background: radial-gradient(120px 100px at 0% 100%, #f7d6b5, #fbe7d3);
  border-bottom-left-radius: 100px;
  display: grid;
  place-items: center;
}
.price {
  color: #2b2b2b;
  font-weight: 800;
}
.list-footer {
  text-align: center;
  color: #b0b7c3;
  padding-top: 12px;
}
.blindbox-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #fffaf3, #f7f2ff);
}
.blindbox-page.dark {
  background: #0b1625;
}
/* 入口页 */
.entry {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 32px;
  text-align: center;
  padding: 48px 16px 72px;
}
.entry-mid {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.center-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #e69b00;
  font-weight: 700;
}
.brand-dot {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: linear-gradient(135deg, #ffd54f, #ffb347);
  display: inline-block;
}
.center-name {
  font-size: 24px;
}
.center-sub {
  font-size: 12px;
  color: #e5a646;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}
.timer-strip {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}
.time-list {
  display: flex;
  align-items: center;
  gap: 12px;
}
.time-cell {
  width: 52px;
  height: 48px;
  border-radius: 14px;
  background: #fff;
  box-shadow: inset 0 -4px 10px rgba(0, 0, 0, 0.06);
  display: grid;
  place-items: center;
  font-weight: 700;
  font-size: 20px;
  color: #1f2937;
}
.time-sep {
  font-size: 20px;
  font-weight: 700;
  color: #d4a373;
}
.mode-pill {
  padding: 4px 12px;
  border-radius: 999px;
  background: #fff5e6;
  color: #d97706;
  font-size: 12px;
}
.hero {
  display: grid;
  place-items: center;
}
.mascot {
  width: 360px;
  height: 240px;
  object-fit: cover;
  border-radius: 32px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.12);
}
.actions {
  display: flex;
  gap: 24px;
  justify-content: center;
}
.action {
  width: 220px;
  height: 110px;
  border: none;
  border-radius: 28px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);
  display: grid;
  place-items: center;
  grid-template-columns: 32px auto;
  gap: 8px;
  cursor: pointer;
}
.action.primary {
  background: #ffe0e8;
  color: #d94673;
}
.action.secondary {
  background: #ebe9ff;
  color: #5b6bd5;
}
.action .icon {
  font-size: 20px;
}
.action .text {
  font-size: 16px;
  font-weight: 700;
}

/* 精准匹配 */
.precise-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
}
.precise-top .title {
  font-size: 18px;
  font-weight: 700;
  color: #6b7280;
}
.home-btn {
  border: none;
  background: #fff;
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  display: grid;
  place-items: center;
}
.home-icon {
  width: 20px;
  height: 20px;
  display: block;
}
.budget,
.flavors {
  padding: 8px 16px;
}
.label {
  color: #6b7280;
  margin-bottom: 8px;
  font-weight: 700;
}
.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}
.chip {
  border: none;
  background: #fff;
  border-radius: 16px;
  padding: 12px;
  display: grid;
  place-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
}
.chip.active {
  outline: 2px solid #93c5fd;
}
.chip-icon {
  font-size: 18px;
  display: grid;
  place-items: center;
}
.chip-img {
  width: 22px;
  height: 22px;
  display: block;
}
.chip-text {
  font-size: 14px;
  color: #6b7280;
}
.cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}
.pref-card {
  background: #fff;
  border: none;
  border-radius: 16px;
  height: 140px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: grid;
  place-items: center;
  cursor: pointer;
}
.pref-card.active {
  outline: 2px solid #fbcfe8;
}
.pref-icon {
  font-size: 22px;
  color: #64748b;
}
.pref-text {
  font-size: 14px;
  color: #64748b;
}
.precise-footer {
  position: sticky;
  bottom: 0;
  padding: 12px 16px 20px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0), #fff);
}
.match-btn {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 12px;
  background: #c8d7e3;
  color: #2c3e50;
  font-weight: 700;
  cursor: pointer;
}

/* 加载页 */
.loading {
  min-height: 60vh;
  display: grid;
  place-items: center;
  color: #fff;
}
.spinner {
  width: 56px;
  height: 56px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
.loading-text {
  margin-top: 12px;
  color: #e5e7eb;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 结果页（暗色） */
.result-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  color: #a7f3d0;
}
.result-top .brand {
  color: #a7f3d0;
}
.result-top .brand-logo {
  width: 18px;
  height: 18px;
  display: inline-block;
  margin-right: 6px;
}
.right .rate {
  margin-right: 6px;
  color: #93c5fd;
}
.result-hero {
  display: grid;
  place-items: center;
  padding: 12px 0 8px;
}
.result-card {
  margin: 0 16px;
  background: #122033;
  color: #e5e7eb;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.35);
  position: relative;
}
.dish-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 8px;
}
.dish-desc {
  margin: 0;
  padding-left: 18px;
}
.price {
  position: absolute;
  right: 16px;
  top: 16px;
  color: #ffd54f;
  font-weight: 700;
}
.result-actions {
  display: grid;
  gap: 12px;
  padding: 16px;
}
.ok-btn {
  height: 48px;
  border: none;
  border-radius: 12px;
  background: #10b981;
  color: #083b2b;
  font-weight: 700;
  cursor: pointer;
}
.redo-btn {
  height: 44px;
  border: none;
  border-radius: 12px;
  background: #334155;
  color: #e5e7eb;
  cursor: pointer;
}
.float-home {
  position: fixed;
  right: 16px;
  bottom: 16px;
  border: none;
  background: #0b1625;
  color: #e5e7eb;
  border-radius: 14px;
  padding: 10px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.35);
  cursor: pointer;
}
.float-home-icon {
  width: 20px;
  height: 20px;
  display: block;
}
</style>
