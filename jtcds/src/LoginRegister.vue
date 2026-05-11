<template>
  <div class="auth-page">
    <!-- 背景 -->
    <div class="background">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>

    <!-- 主容器 -->
    <div class="auth-container">
      <!-- 左侧内容 -->
      <div class="auth-left">
        <div class="brand">
          <div class="brand-logo">
            <div class="logo-circle">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L13.09 8.26L20 9L13.09 9.74L12 16L10.91 9.74L4 9L10.91 8.26L12 2Z" fill="currentColor"/>
              </svg>
            </div>
          </div>
          <h1 class="brand-title">今天吃点啥</h1>
          <p class="brand-subtitle">智能外卖比价，让美食更实惠</p>
        </div>
        
        <div class="features">
          <div class="feature">
            <div class="feature-icon">💰</div>
            <div class="feature-text">
              <h3>智能比价</h3>
              <p>实时对比各平台价格，帮你省钱</p>
            </div>
          </div>
          <div class="feature">
            <div class="feature-icon">🎯</div>
            <div class="feature-text">
              <h3>精准推荐</h3>
              <p>基于你的喜好，推荐最优选择</p>
            </div>
          </div>
          <div class="feature">
            <div class="feature-icon">⚡</div>
            <div class="feature-text">
              <h3>快速便捷</h3>
              <p>一键搜索，秒速找到最佳优惠</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单 -->
      <div class="auth-right">
        <div class="form-container">
          <!-- 标签切换 -->
          <div class="tabs">
            <button 
              :class="['tab', { active: activeTab === 'login' }]"
              @click="activeTab = 'login'"
            >
              登录
            </button>
            <button 
              :class="['tab', { active: activeTab === 'register' }]"
              @click="activeTab = 'register'"
            >
              注册
            </button>
          </div>

          <!-- 登录表单 -->
          <form v-if="activeTab === 'login'" @submit.prevent="onLogin" class="form">
            <div class="form-header">
              <h2>欢迎回来</h2>
              <p>登录您的账户继续使用</p>
            </div>

            <div class="form-group">
              <input
                v-model="loginForm.account"
                type="text"
                placeholder="手机号或邮箱"
                class="form-input"
                required
              />
            </div>

            <div class="form-group">
              <div class="input-with-icon">
                <input
                  v-model="loginForm.password"
                  :type="showLoginPwd ? 'text' : 'password'"
                  placeholder="密码"
                  class="form-input"
                  required
                />
                <button
                  type="button"
                  class="input-icon"
                  @click="showLoginPwd = !showLoginPwd"
                >
                  <svg v-if="showLoginPwd" viewBox="0 0 24 24" fill="none">
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" stroke="currentColor" stroke-width="2"/>
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none">
                    <path d="m1 1 22 22" stroke="currentColor" stroke-width="2"/>
                    <path d="M6.71 6.71C4.68 8.1 3 10.6 3 12s1.68 3.9 3.71 5.29" stroke="currentColor" stroke-width="2"/>
                    <path d="M17.29 17.29C19.32 15.9 21 13.4 21 12s-1.68-3.9-3.71-5.29" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </button>
              </div>
            </div>

            <button type="submit" class="btn-primary" :disabled="!loginForm.account || !loginForm.password">
              <span v-if="!loading">登录</span>
              <div v-else class="loading-dots">
                <span></span><span></span><span></span>
              </div>
            </button>

            <p class="form-footer">
              还没有账户？
              <button type="button" @click="activeTab = 'register'" class="link-btn">
                立即注册
              </button>
            </p>
          </form>

          <!-- 注册表单 -->
          <form v-else @submit.prevent="onRegister" class="form">
            <div class="form-header">
              <h2>创建账户</h2>
              <p>加入我们，开始省钱之旅</p>
            </div>

            <div class="form-group">
              <input
                v-model="registerForm.phone"
                type="tel"
                placeholder="手机号"
                class="form-input"
                required
              />
            </div>

            <div class="form-group">
              <input
                v-model="registerForm.email"
                type="email"
                placeholder="邮箱地址"
                class="form-input"
                required
              />
            </div>

            <div class="form-group">
              <div class="input-with-icon">
                <input
                  v-model="registerForm.password"
                  :type="showRegPwd ? 'text' : 'password'"
                  placeholder="设置密码"
                  class="form-input"
                  required
                />
                <button
                  type="button"
                  class="input-icon"
                  @click="showRegPwd = !showRegPwd"
                >
                  <svg v-if="showRegPwd" viewBox="0 0 24 24" fill="none">
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" stroke="currentColor" stroke-width="2"/>
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none">
                    <path d="m1 1 22 22" stroke="currentColor" stroke-width="2"/>
                    <path d="M6.71 6.71C4.68 8.1 3 10.6 3 12s1.68 3.9 3.71 5.29" stroke="currentColor" stroke-width="2"/>
                    <path d="M17.29 17.29C19.32 15.9 21 13.4 21 12s-1.68-3.9-3.71-5.29" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </button>
              </div>
            </div>

            <div class="form-group">
              <div class="input-with-icon">
                <input
                  v-model="registerForm.confirm"
                  :type="showRegConfirm ? 'text' : 'password'"
                  placeholder="确认密码"
                  class="form-input"
                  required
                />
                <button
                  type="button"
                  class="input-icon"
                  @click="showRegConfirm = !showRegConfirm"
                >
                  <svg v-if="showRegConfirm" viewBox="0 0 24 24" fill="none">
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" stroke="currentColor" stroke-width="2"/>
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none">
                    <path d="m1 1 22 22" stroke="currentColor" stroke-width="2"/>
                    <path d="M6.71 6.71C4.68 8.1 3 10.6 3 12s1.68 3.9 3.71 5.29" stroke="currentColor" stroke-width="2"/>
                    <path d="M17.29 17.29C19.32 15.9 21 13.4 21 12s-1.68-3.9-3.71-5.29" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </button>
              </div>
            </div>

            <div class="form-group">
              <div class="code-group">
                <input
                  v-model="registerForm.code"
                  type="text"
                  placeholder="验证码"
                  class="form-input code-input"
                  required
                />
                <button
                  type="button"
                  class="btn-code"
                  @click="sendCode"
                  :disabled="codeSent || !registerForm.email"
                >
                  {{ codeSent ? '已发送' : '发送验证码' }}
                </button>
              </div>
            </div>

            <button type="submit" class="btn-primary" :disabled="!canRegister">
              <span v-if="!loading">创建账户</span>
              <div v-else class="loading-dots">
                <span></span><span></span><span></span>
              </div>
            </button>

            <p class="form-footer">
              已有账户？
              <button type="button" @click="activeTab = 'login'" class="link-btn">
                立即登录
              </button>
            </p>
          </form>
        </div>
      </div>
    </div>

    <!-- 全局加载遮罩 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner">
        <div class="spinner"></div>
        <p>{{ loadingText }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { apiLogin, apiRegister, apiSendCode } from '@/services/authApi'

const router = useRouter()
const userStore = useUserStore()

// 当前激活的tab，login 或 register
const activeTab = ref<'login' | 'register'>('login')

// 密码显示状态
const showLoginPwd = ref(false)
const showRegPwd = ref(false)
const showRegConfirm = ref(false)

// 加载状态
const loading = ref(false)
const loadingText = ref('')

// 登录表单数据
const loginForm = ref({
  account: '',
  password: '',
})

// 登录表单提交事件
async function onLogin() {
  loading.value = true
  loadingText.value = '正在登录...'
  
  try {
    const response = await apiLogin({
      username: loginForm.value.account,
      password: loginForm.value.password,
    })
    console.log('Login response:', response) // 调试日志
    if (response.success) {
      // 后端直接返回token，不是在data.token中
      const token = response.token || response.data?.token
      if (token) {
        loadingText.value = '登录成功，正在跳转...'
        userStore.login(token)
        setTimeout(() => {
          router.push('/home')
        }, 1000)
      } else {
        alert('登录响应中缺少token')
      }
    } else {
      alert(`登录失败: ${response.message}`)
    }
  } catch (error) {
    console.error('Login error:', error)
    alert('登录请求失败，请检查网络或联系管理员。')
  } finally {
    loading.value = false
  }
}

// 注册表单数据
const registerForm = ref({
  phone: '', // This will be used as username
  email: '',
  password: '',
  confirm: '',
  code: '',
})

// 验证码发送状态
const codeSent = ref(false)

// 注册表单提交事件
async function onRegister() {
  if (registerForm.value.password !== registerForm.value.confirm) {
    alert('两次输入的密码不一致！')
    return
  }
  
  loading.value = true
  loadingText.value = '正在注册...'
  
  try {
    const response = await apiRegister({
      username: registerForm.value.phone, // Using phone as username
      email: registerForm.value.email,
      password: registerForm.value.password,
      code: registerForm.value.code,
    })
    if (response.success) {
      loadingText.value = '注册成功！'
      setTimeout(() => {
        alert('注册成功！现在可以登录了。')
        activeTab.value = 'login'
        // 清空注册表单
        registerForm.value = {
          phone: '',
          email: '',
          password: '',
          confirm: '',
          code: '',
        }
        codeSent.value = false
      }, 1000)
    } else {
      alert(`注册失败: ${response.message}`)
    }
  } catch (error) {
    console.error('Register error:', error)
    alert('注册请求失败，请稍后重试。')
  } finally {
    loading.value = false
  }
}

// 发送验证码事件
async function sendCode() {
  const email = registerForm.value.email
  if (!email) {
    alert('请先填写邮箱')
    return
  }
  
  loading.value = true
  loadingText.value = '正在发送验证码...'
  
  try {
    const response = await apiSendCode(email, 1) // 1表示注册类型
    if (response.success) {
      codeSent.value = true
      loadingText.value = '验证码发送成功！'
      setTimeout(() => {
        alert('验证码已发送，请注意查收。')
        // 60秒后重置按钮
        setTimeout(() => (codeSent.value = false), 60 * 1000)
      }, 500)
    } else {
      alert(`验证码发送失败: ${response.message}`)
    }
  } catch (e) {
    console.error(e)
    alert('验证码发送失败')
  } finally {
    loading.value = false
  }
}

// 注册按钮可用性校验
const canRegister = computed(() => {
  const f = registerForm.value
  return f.phone && f.email && f.password && f.confirm && f.code && f.password === f.confirm
})
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.auth-page {
  min-height: 100vh;
  position: relative;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

/* 背景装饰 */
.background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -150px;
  animation: float 8s ease-in-out infinite;
}

.shape-2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  left: -100px;
  animation: float 6s ease-in-out infinite reverse;
}

.shape-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 10%;
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(180deg); }
}

/* 主容器 */
.auth-container {
  display: flex;
  min-height: 100vh;
  position: relative;
  z-index: 1;
}

/* 左侧内容 */
.auth-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 80px 60px;
  color: white;
}

.brand {
  margin-bottom: 60px;
}

.brand-logo {
  margin-bottom: 30px;
}

.logo-circle {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.logo-circle svg {
  width: 28px;
  height: 28px;
  color: white;
}

.brand-title {
  font-size: 3rem;
  font-weight: 700;
  margin: 0 0 16px 0;
  background: linear-gradient(135deg, #ffffff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-subtitle {
  font-size: 1.25rem;
  opacity: 0.9;
  margin: 0;
  font-weight: 300;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.feature {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.feature-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.feature-text h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.feature-text p {
  font-size: 1rem;
  opacity: 0.8;
  margin: 0;
  line-height: 1.5;
}

/* 右侧表单 */
.auth-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
}

.form-container {
  width: 100%;
  max-width: 400px;
}

/* 标签切换 */
.tabs {
  display: flex;
  background: #f8fafc;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 40px;
}

.tab {
  flex: 1;
  padding: 12px 24px;
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 1rem;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab.active {
  background: white;
  color: #1e293b;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 表单 */
.form {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-header h2 {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.form-header p {
  color: #64748b;
  font-size: 1rem;
  margin: 0;
}

.form-group {
  margin-bottom: 24px;
}

.form-input {
  width: 100%;
  padding: 16px 20px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  background: white;
  color: #1e293b;
  transition: all 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-input::placeholder {
  color: #94a3b8;
}

.input-with-icon {
  position: relative;
}

.input-icon {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  background: transparent;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: color 0.2s ease;
}

.input-icon:hover {
  color: #667eea;
}

.input-icon svg {
  width: 20px;
  height: 20px;
}

.code-group {
  display: flex;
  gap: 12px;
}

.code-input {
  flex: 1;
}

.btn-code {
  padding: 16px 20px;
  border: 2px solid #e2e8f0;
  background: white;
  color: #667eea;
  font-size: 0.9rem;
  font-weight: 500;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.btn-code:hover:not(:disabled) {
  border-color: #667eea;
  background: #667eea;
  color: white;
}

.btn-code:disabled {
  color: #94a3b8;
  cursor: not-allowed;
}

.btn-primary {
  width: 100%;
  padding: 16px;
  border: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin: 32px 0 24px 0;
  position: relative;
  overflow: hidden;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.loading-dots {
  display: flex;
  justify-content: center;
  gap: 4px;
}

.loading-dots span {
  width: 6px;
  height: 6px;
  background: white;
  border-radius: 50%;
  animation: bounce 1.4s ease-in-out infinite both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.form-footer {
  text-align: center;
  color: #64748b;
  font-size: 0.9rem;
}

.link-btn {
  border: none;
  background: transparent;
  color: #667eea;
  font-weight: 500;
  cursor: pointer;
  text-decoration: underline;
  margin-left: 4px;
}

.link-btn:hover {
  color: #5a67d8;
}

/* 加载遮罩 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.loading-spinner {
  background: white;
  padding: 40px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f1f5f9;
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-spinner p {
  color: #1e293b;
  font-weight: 500;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .auth-container {
    flex-direction: column;
  }
  
  .auth-left {
    padding: 60px 40px 40px;
  }
  
  .brand-title {
    font-size: 2.5rem;
  }
  
  .features {
    flex-direction: row;
    gap: 24px;
    overflow-x: auto;
  }
  
  .feature {
    min-width: 200px;
  }
  
  .auth-right {
    padding: 40px;
  }
}

@media (max-width: 768px) {
  .auth-left {
    padding: 40px 20px 20px;
  }
  
  .brand-title {
    font-size: 2rem;
  }
  
  .features {
    display: none;
  }
  
  .auth-right {
    padding: 20px;
  }
  
  .form-container {
    max-width: none;
  }
}

@media (max-width: 480px) {
  .form-header h2 {
    font-size: 1.75rem;
  }
  
  .code-group {
    flex-direction: column;
  }
  
  .btn-code {
    width: 100%;
  }
}
</style>
