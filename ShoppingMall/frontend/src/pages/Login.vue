<template>
  <div class="wrap">
    <el-card class="box">
      <h2 style="text-align:center;margin-bottom:16px;">登录</h2>
      <el-form :model="form" @keyup.enter="onSubmit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" autocomplete="current-password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
  
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

async function onSubmit() {
  loading.value = true
  try {
    const res = await auth.login(form.username, form.password)
    if (res.success) {
      ElMessage.success('登录成功')
      const redirect = route.query.redirect || '/'
      router.replace(redirect)
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.wrap { display:flex; justify-content:center; padding-top:60px; }
.box { width: 360px; }
</style>

