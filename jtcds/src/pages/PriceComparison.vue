<template>
  <div class="price-page">
    <!-- 顶部导航与标题区域 -->
    <header class="page-header">
      <button class="back-btn" @click="$router.back()">←</button>
      <div class="title-wrap">
        <!-- 菜品名称（后端对接：从接口获取） -->
        <h1 class="dish-title">{{ dish.title || '加载中...' }}</h1>
        <!-- 菜品标签（后端对接：从接口获取） -->
        <div class="tags">
          <span v-for="t in dish.tags" :key="t" class="tag">{{ t }}</span>
        </div>
      </div>
      <div class="cart-btn">外卖</div>
    </header>

    <!-- 主内容区：左图右信息卡 -->
    <section class="hero">
      <!-- 菜品图片（后端对接：图片URL） -->
      <div class="hero-image">
        <!-- TODO: 后端对接：将 src 替换为后端返回的图片地址 -->
        <img :src="dish.imageUrl || placeholderImage" alt="菜品图片" />
        <div class="image-badges">
          <span class="badge hot">热销</span>
          <span class="badge new">新品</span>
        </div>
      </div>
      <!-- 右侧信息卡 -->
      <div class="hero-info">
        <h2 class="hero-title">{{ dish.title || '加载中...' }}</h2>
        <p class="hero-desc">{{ dish.desc || '正在获取菜品信息...' }}</p>

        <!-- 特色标签（后端对接） -->
        <div class="feature-tags">
          <span class="f-tag">快餐</span>
          <span class="f-tag">鸡肉</span>
          <span class="f-tag danger">热销</span>
          <span class="f-tag stat">{{ dish.stat || '9.2万' }}</span>
        </div>

        <!-- 全网最低价卡片（后端对接：平台名与价格） -->
        <div class="lowest-card">
          <div class="lowest-title">全网最低价</div>
          <div class="lowest-price">￥{{ lowest.platformPrice || '18.8' }}</div>
          <button class="buy-btn">去购买</button>
        </div>
      </div>
    </section>

    <!-- 最佳选择（后端对接：推荐平台与差价） -->
    <section class="best-choice">
      <div class="best-label">最佳选择</div>
      <div class="best-content">
        <div class="best-platform">{{ best.platform || '美团外卖' }}</div>
        <div class="best-price">￥{{ best.price || '18.8' }}</div>
        <div class="save">节省 {{ best.save || '￥6.5 (25.7%)' }}</div>
      </div>
    </section>

    <!-- 平台价格列表（后端对接：各平台价格明细） -->
    <section class="platform-list">
      <div class="list-header">
        <div>平台</div>
        <div>优惠明细</div>
        <div>最终实付</div>
        <div>操作</div>
      </div>
      <div v-for="row in platforms" :key="row.id" class="list-row">
        <div class="plat-name">
          <span class="plat-badge" :class="row.badgeClass">{{ row.badge }}</span>
          <span>{{ row.name }}</span>
        </div>
        <div class="discounts">
          <span v-for="d in row.discounts" :key="d" class="discount">{{ d }}</span>
        </div>
        <div class="final">￥{{ row.final }}</div>
        <div class="ops">
          <button class="go-btn">去购买</button>
        </div>
      </div>
    </section>

    <!-- 优惠优化建议（后端对接：文案与建议项） -->
    <section class="suggestions">
      <h3>优惠优化建议</h3>
      <div class="suggest-grid">
        <div class="suggest-card">
          <div class="icon">🛒</div>
          <div class="text">组合优惠最佳</div>
          <div class="sub">在美团购买，使用 满减+会员券</div>
        </div>
        <div class="suggest-card">
          <div class="icon">⏳</div>
          <div class="text">建议等待</div>
          <div class="sub">历史数据显示 周末下午优惠更大</div>
        </div>
      </div>
    </section>

    <!-- 附近更优选择（后端对接：推荐菜品与价格） -->
    <section class="nearby">
      <h3>附近更优选择</h3>
      <div class="nearby-list">
        <div v-for="n in nearby" :key="n.id" class="near-item">
          <img :src="n.imageUrl || placeholderImage" alt="推荐菜品" />
          <div class="near-info">
            <div class="near-title">{{ n.title }}</div>
            <div class="near-price">￥{{ n.price }}</div>
            <div class="near-save">比当前便宜 {{ n.save }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部操作条 -->
    <footer class="footer-bar">
      <button class="refresh-btn" @click="refreshData">刷新价格</button>
      <button class="share-btn">分享</button>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getPriceDetail, refreshPrice, type PriceDetail } from '@/services/priceApi'

// 占位图片（后端对接后可移除）
const placeholderImage = '/assets/login-register.jpg'

// 后端数据模型占位
const dish = ref<{
  title?: string
  desc?: string
  imageUrl?: string
  tags: string[]
  stat?: string
}>({
  title: '加载中...',
  desc: '正在获取菜品信息...',
  imageUrl: '',
  tags: [],
  stat: '0',
})

const lowest = ref<{ platformPrice?: string }>({ platformPrice: '18.8' })
const best = ref<{ platform?: string; price?: string; save?: string }>({
  platform: '美团外卖',
  price: '18.8',
  save: '￥6.5 (25.7%)',
})

type PlatformRow = {
  id: string
  name: string
  badge: string
  badgeClass: string
  discounts: string[]
  final: string
}

const platforms = ref<PlatformRow[]>([
  {
    id: 'mt',
    name: '美团外卖',
    badge: '美',
    badgeClass: 'mt',
    discounts: ['满20减2', '会员红包x3'],
    final: '18.8',
  },
  {
    id: 'elm',
    name: '饿了么',
    badge: '饿',
    badgeClass: 'elm',
    discounts: ['店铺满减x2'],
    final: '22',
  },
])

const nearby = ref<{ id: string; title: string; price: string; save: string; imageUrl?: string }[]>(
  [{ id: 'n1', title: '香辣鸡腿饭套餐', price: '15.9', save: '￥4.9', imageUrl: '' }],
)

const route = useRoute()
const loading = ref(false)
const error = ref<string | null>(null)

function getErrorMessage(e: unknown): string {
  if (e instanceof Error) return e.message
  if (typeof e === 'string') return e
  return '网络错误'
}

async function loadDetail() {
  try {
    loading.value = true
    error.value = null
    const productIdParam = route.query.dishId || route.params.productId
    const productId = Number(productIdParam || 1)
    
    // 首先获取菜品基本信息
    try {
      const dishResponse = await fetch(`http://localhost:8081/api/home/dish/${productId}`)
      const dishResult = await dishResponse.json()
      
      if (dishResponse.ok && dishResult.code === 200) {
        const dishData = dishResult.data
        dish.value = {
          title: dishData.dishName,
          desc: dishData.description || '暂无描述',
          imageUrl: dishData.mainImage,
          tags: dishData.tagList || [],
          stat: (dishData.viewCount || 0).toLocaleString('zh-CN'),
        }
      }
    } catch (dishError) {
      console.warn('获取菜品信息失败:', dishError)
    }
    
    // 然后尝试获取价格信息
    try {
      const res = await getPriceDetail(productId)
      const ok = res.success === true || res.code === 200
      if (ok) {
        const data = res.data as PriceDetail
        // 如果有价格信息，更新相关数据
        lowest.value = { platformPrice: String(data.lowestPrice) }
        best.value = {
          platform: data.lowestPricePlatform,
          price: String(data.lowestPrice),
          save:
            data.saveAmount != null && data.savePercent != null
              ? `￥${data.saveAmount} (${data.savePercent}%)`
              : undefined,
        }
        platforms.value = data.priceList.map((p) => ({
          id: p.platform,
          name: p.platformName,
          badge: p.platformName?.[0] ?? '',
          badgeClass: p.platform,
          discounts: [p.couponInfo ? p.couponInfo : '—'],
          final: String(p.finalPrice),
        }))
        nearby.value = (data.similarProducts || []).map((s) => ({
          id: String(s.productId),
          title: s.productName,
          price: String(s.minPrice),
          save: '',
          imageUrl: '',
        }))
      } else {
        // 如果没有价格信息，使用菜品的基本价格信息
        console.warn('价格信息不可用，使用菜品基本信息')
      }
    } catch (priceError) {
      console.warn('获取价格信息失败:', priceError)
      // 价格信息获取失败时，保持菜品基本信息显示
    }
    
  } catch (e: unknown) {
    error.value = getErrorMessage(e)
  } finally {
    loading.value = false
  }
}

async function refreshData() {
  try {
    const productIdParam = route.query.dishId || route.params.productId
    const productId = Number(productIdParam || 1)
    await refreshPrice(productId)
    await loadDetail()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.price-page {
  background: #fff;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
}
.back-btn {
  border: none;
  background: transparent;
  font-size: 20px;
  cursor: pointer;
}
.title-wrap {
  text-align: center;
}
.dish-title {
  font-size: 22px;
  margin: 0;
}
.tags {
  margin-top: 8px;
}
.tag {
  display: inline-block;
  margin-right: 8px;
  padding: 4px 8px;
  background: #f6f6ff;
  border-radius: 12px;
  color: #666;
}
.cart-btn {
  background: #fdf7f2;
  padding: 8px 12px;
  border-radius: 16px;
}

.hero {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  padding: 16px 24px;
}
.hero-image {
  position: relative;
}
.hero-image img {
  width: 100%;
  max-width: 300px;
  height: 200px;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}
.image-badges {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 8px;
}
.badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #fff;
}
.badge.hot {
  background: #ff8a65;
}
.badge.new {
  background: #a5b6ff;
}

.hero-info {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}
.hero-title {
  margin: 0 0 6px;
  font-size: 20px;
}
.hero-desc {
  margin: 0 0 12px;
  color: #777;
}
.feature-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.f-tag {
  padding: 4px 8px;
  background: #f6f6ff;
  border-radius: 12px;
  color: #666;
}
.f-tag.danger {
  background: #ffe6e6;
  color: #ff4d4f;
}
.f-tag.stat {
  background: #eafbea;
  color: #2e7d32;
}

.lowest-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: 16px;
  padding: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}
.lowest-title {
  color: #888;
}
.lowest-price {
  font-size: 22px;
  font-weight: 700;
  color: #ff6f00;
}
.buy-btn {
  margin-left: auto;
  padding: 8px 12px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(90deg, #ffb68a, #ffe6d0);
  color: #fff;
  cursor: pointer;
}

.best-choice {
  margin: 12px 24px;
  padding: 12px;
  background: #effaf1;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.best-label {
  background: #2e7d32;
  color: #fff;
  padding: 6px 10px;
  border-radius: 12px;
}
.best-content {
  display: flex;
  gap: 16px;
  align-items: baseline;
}
.best-price {
  font-size: 18px;
  font-weight: 600;
  color: #2e7d32;
}
.save {
  color: #2e7d32;
}

.platform-list {
  margin: 16px 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.04);
}
.list-header,
.list-row {
  display: grid;
  grid-template-columns: 1.5fr 2fr 1fr 1fr;
  gap: 12px;
  padding: 12px 16px;
}
.list-header {
  color: #999;
  border-bottom: 1px solid #f0f0f0;
}
.list-row {
  align-items: center;
}
.plat-badge {
  display: inline-block;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  text-align: center;
  line-height: 22px;
  color: #fff;
  margin-right: 8px;
  font-size: 12px;
}
.plat-badge.mt {
  background: #fdd835;
}
.plat-badge.elm {
  background: #42a5f5;
}
.discount {
  display: inline-block;
  margin-right: 8px;
  padding: 4px 8px;
  background: #f6f6ff;
  border-radius: 12px;
}
.final {
  font-weight: 600;
}
.go-btn {
  padding: 6px 10px;
  border-radius: 12px;
  border: none;
  background: #ff7043;
  color: #fff;
  cursor: pointer;
}

.suggestions {
  margin: 16px 24px;
}
.suggest-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}
.suggest-card {
  background: #fff;
  border-radius: 16px;
  padding: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.04);
}
.suggest-card .icon {
  font-size: 24px;
}
.suggest-card .text {
  font-weight: 600;
}
.suggest-card .sub {
  color: #777;
}

.nearby {
  margin: 16px 24px;
}
.nearby-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.near-item {
  background: #fff;
  border-radius: 16px;
  padding: 10px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.04);
  display: flex;
  gap: 10px;
}
.near-item img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 12px;
}
.near-title {
  font-weight: 600;
}
.near-price {
  color: #ff6f00;
  font-weight: 700;
}
.near-save {
  color: #2e7d32;
}

.footer-bar {
  position: sticky;
  bottom: 0;
  background: #fff;
  padding: 12px 24px;
  display: flex;
  gap: 12px;
  box-shadow: 0 -6px 16px rgba(0, 0, 0, 0.04);
}
.refresh-btn,
.share-btn {
  padding: 8px 12px;
  border-radius: 12px;
  border: none;
  cursor: pointer;
}
.refresh-btn {
  background: #e0f2f1;
}
.share-btn {
  background: #e3f2fd;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .hero-image img {
    max-width: 100%;
    height: 180px;
  }
  
  .nearby-list {
    grid-template-columns: 1fr;
  }
  
  .suggest-grid {
    grid-template-columns: 1fr;
  }
  
  .near-item {
    flex-direction: row;
    align-items: center;
  }
}
</style>
