<script setup lang="ts">
import AppNavigation from './components/AppNavigation.vue'
</script>

<template>
  <div class="app">
    <AppNavigation />
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<style>
* {
  box-sizing: border-box;
}

.app {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  color: #1e293b;
  transition: all 0.3s ease;
}

.app-main {
  min-height: calc(100vh - 72px);
}

@media (max-width: 768px) {
  .app-main {
    min-height: calc(100vh - 64px);
    padding-bottom: 80px; /* 为底部导航留出空间 */
  }
}
</style>
