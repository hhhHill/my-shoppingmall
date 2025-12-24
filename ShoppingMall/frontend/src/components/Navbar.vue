<template>
  <el-menu mode="horizontal" :ellipsis="false" router>
    <el-menu-item index="/">商城</el-menu-item>
    <div style="flex:1"></div>
    <el-menu-item index="/">商品</el-menu-item>
    <el-menu-item index="/cart">购物车</el-menu-item>
    <el-menu-item index="/orders">订单</el-menu-item>
    <el-sub-menu v-if="auth.user?.role==='ADMIN'" index="admin">
      <template #title>后台管理</template>
      <el-menu-item index="/admin">控制台</el-menu-item>
    </el-sub-menu>
    <el-menu-item v-if="!auth.isAuthenticated" index="/login">登录</el-menu-item>
    <el-sub-menu v-else index="user">
      <template #title>{{ auth.user?.username }}</template>
      <el-menu-item @click="onLogout">退出登录</el-menu-item>
    </el-sub-menu>
  </el-menu>
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
.el-menu { padding: 0 16px; }
</style>

