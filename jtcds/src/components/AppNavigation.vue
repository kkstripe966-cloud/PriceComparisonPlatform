<template>
  <header class="app-header" v-if="shouldShowNavigation">
    <div class="header-content">
      <!-- 品牌区域 -->
      <div class="brand">
        <div class="logo">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L13.09 8.26L20 9L13.09 9.74L12 16L10.91 9.74L4 9L10.91 8.26L12 2Z" fill="currentColor"/>
          </svg>
        </div>
        <h1 class="brand-title">今天吃点啥</h1>
      </div>

      <!-- 主导航菜单 (PC端) -->
      <nav class="main-nav">
        <RouterLink class="nav-link" to="/home">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2"/>
            <polyline points="9,22 9,12 15,12 15,22" stroke="currentColor" stroke-width="2"/>
          </svg>
          <span>首页</span>
        </RouterLink>
        <RouterLink class="nav-link" to="/blindbox">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" stroke="currentColor" stroke-width="2"/>
            <polyline points="3.27,6.96 12,12.01 20.73,6.96" stroke="currentColor" stroke-width="2"/>
            <line x1="12" y1="22.08" x2="12" y2="12" stroke="currentColor" stroke-width="2"/>
          </svg>
          <span>盲盒</span>
        </RouterLink>
      </nav>

      <!-- 用户操作区域 -->
      <div class="user-actions">
        <!-- 通知按钮 -->
        <button class="action-btn" @click="toggleNotifications" title="通知">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" stroke-width="2"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="2"/>
          </svg>
          <span v-if="notificationCount > 0" class="notification-badge">{{ notificationCount }}</span>
        </button>
        
        <!-- 用户菜单 -->
        <div class="user-menu" @mouseenter="onUserMenuEnter" @mouseleave="onUserMenuLeave">
          <button class="action-btn user-btn" @click="toggleUserMenu">
            <div class="user-avatar">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2"/>
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
              </svg>
            </div>
            <span class="user-name">用户</span>
            <svg class="dropdown-arrow" viewBox="0 0 24 24" fill="none">
              <polyline points="6,9 12,15 18,9" stroke="currentColor" stroke-width="2"/>
            </svg>
          </button>
          <div class="dropdown" v-show="userMenuOpen">
            <div class="dropdown-header">
              <div class="user-info">
                <div class="user-avatar-large">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2"/>
                    <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </div>
                <div class="user-details">
                  <div class="user-name-large">用户</div>
                  <div class="user-email">欢迎使用今天吃点啥</div>
                </div>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <button class="dropdown-item logout-item" @click="onLogout">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" stroke="currentColor" stroke-width="2"/>
                <polyline points="16,17 21,12 16,7" stroke="currentColor" stroke-width="2"/>
                <line x1="21" y1="12" x2="9" y2="12" stroke="currentColor" stroke-width="2"/>
              </svg>
              登出
            </button>
          </div>
        </div>
      </div>
    </div>
  </header>

  <!-- 底部导航 (移动端) -->
  <nav class="bottom-nav" v-if="shouldShowNavigation">
    <RouterLink class="nav-item" to="/home">
      <svg viewBox="0 0 24 24" fill="none">
        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" stroke="currentColor" stroke-width="2"/>
        <polyline points="9,22 9,12 15,12 15,22" stroke="currentColor" stroke-width="2"/>
      </svg>
      <span>首页</span>
    </RouterLink>
    <RouterLink class="nav-item" to="/blindbox">
      <svg viewBox="0 0 24 24" fill="none">
        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" stroke="currentColor" stroke-width="2"/>
        <polyline points="3.27,6.96 12,12.01 20.73,6.96" stroke="currentColor" stroke-width="2"/>
        <line x1="12" y1="22.08" x2="12" y2="12" stroke="currentColor" stroke-width="2"/>
      </svg>
      <span>盲盒</span>
    </RouterLink>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink, useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 用户菜单状态
const userMenuOpen = ref(false)
const isUserMenuPinned = ref(false)
const notificationCount = ref(3) // 示例通知数量

// 判断是否显示导航栏（登录页面不显示）
const shouldShowNavigation = computed(() => {
  return route.name !== 'LoginRegister'
})

// 导航功能
function toggleNotifications() {
  // TODO: 实现通知功能
  console.log('Toggle notifications')
}

// 用户菜单
function onUserMenuEnter() {
  userMenuOpen.value = true
}

function onUserMenuLeave() {
  if (!isUserMenuPinned.value) userMenuOpen.value = false
}

function toggleUserMenu() {
  userMenuOpen.value = true
  isUserMenuPinned.value = !isUserMenuPinned.value
}

function onLogout() {
  userStore.logout()
  router.push('/auth')
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

/* 顶部导航栏 */
.app-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.3s ease;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.logo {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.logo svg {
  width: 20px;
  height: 20px;
}

.brand-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 主导航菜单 */
.main-nav {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  justify-content: center;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 12px;
  text-decoration: none;
  color: #64748b;
  font-weight: 500;
  font-size: 0.95rem;
  transition: all 0.2s ease;
  position: relative;
}

.nav-link:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  transform: translateY(-1px);
}

.nav-link.router-link-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.nav-link svg {
  width: 18px;
  height: 18px;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.action-btn {
  width: 44px;
  height: 44px;
  border: none;
  background: transparent;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748b;
  transition: all 0.2s ease;
  position: relative;
}

.action-btn:hover {
  background: #f1f5f9;
  color: #1e293b;
  transform: translateY(-1px);
}

.action-btn svg {
  width: 20px;
  height: 20px;
}

/* 通知徽章 */
.notification-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #ef4444;
  color: white;
  font-size: 0.7rem;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 用户菜单按钮 */
.user-btn {
  width: auto;
  padding: 8px 16px;
  gap: 8px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.user-btn:hover {
  background: white;
  border-color: #667eea;
}

.user-avatar {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.user-avatar svg {
  width: 16px;
  height: 16px;
}

.user-name {
  font-size: 0.9rem;
  font-weight: 500;
  color: #1e293b;
}

.dropdown-arrow {
  width: 16px;
  height: 16px;
  transition: transform 0.2s ease;
}

.user-menu:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.user-menu {
  position: relative;
}

.dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
  min-width: 240px;
  overflow: hidden;
  z-index: 50;
}

.dropdown-header {
  padding: 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e2e8f0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar-large {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.user-avatar-large svg {
  width: 24px;
  height: 24px;
}

.user-details {
  flex: 1;
}

.user-name-large {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.user-email {
  font-size: 0.85rem;
  color: #64748b;
}

.dropdown-divider {
  height: 1px;
  background: #e2e8f0;
  margin: 8px 0;
}

.dropdown-item {
  width: 100%;
  padding: 14px 20px;
  border: none;
  background: transparent;
  text-align: left;
  cursor: pointer;
  color: #374151;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.2s ease;
}

.dropdown-item:hover {
  background: #f9fafb;
  color: #1e293b;
}

.dropdown-item.logout-item {
  color: #dc2626;
}

.dropdown-item.logout-item:hover {
  background: #fef2f2;
  color: #dc2626;
}

.dropdown-item svg {
  width: 18px;
  height: 18px;
}

/* 底部导航 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(226, 232, 240, 0.8);
  display: flex;
  padding: 8px 0;
  z-index: 50;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 8px;
  text-decoration: none;
  color: #64748b;
  transition: color 0.2s ease;
}

.nav-item.router-link-active {
  color: #667eea;
}

.nav-item svg {
  width: 24px;
  height: 24px;
}

.nav-item span {
  font-size: 0.75rem;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .header-content {
    gap: 16px;
  }
  
  .main-nav {
    display: none;
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
    height: 64px;
    gap: 12px;
  }
  
  .main-nav {
    display: none;
  }
  
  .user-btn {
    width: 40px;
    padding: 8px;
  }
  
  .user-name,
  .dropdown-arrow {
    display: none;
  }
  
  .bottom-nav {
    display: flex;
  }
}

@media (min-width: 769px) {
  .bottom-nav {
    display: none;
  }
}
</style>