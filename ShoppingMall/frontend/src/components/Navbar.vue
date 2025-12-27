<template>
  <header class="site-header">
    <div class="container navbar">
      <div class="brand">Mall</div>
      <div class="spacer"></div>
      <nav style="flex: none;">
        <el-menu mode="horizontal" :ellipsis="false" router class="nav-el" :border="false">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/cart">购物车</el-menu-item>
          <el-menu-item index="/orders">订单</el-menu-item>
          <el-sub-menu v-if="auth.user?.role==='ADMIN'" index="admin">
            <template #title>后台管理</template>
            <el-menu-item index="/admin">控制台</el-menu-item>
          </el-sub-menu>
          <el-menu-item v-if="!auth.isAuthenticated" index="/login">登录</el-menu-item>
          <el-menu-item v-if="!auth.isAuthenticated" index="/register">注册</el-menu-item>
          <el-sub-menu v-else index="user">
            <template #title>{{ auth.user?.username }}</template>
            <el-menu-item @click="onLogout">退出登录</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

function onLogout() {
  auth.logout()
  router.push('/')
}
</script>

<style scoped>
.nav-el {
  --el-menu-bg-color: var(--c-surface);
  --el-menu-text-color: var(--c-text);
  --el-menu-active-color: var(--c-primary);
}
</style>
