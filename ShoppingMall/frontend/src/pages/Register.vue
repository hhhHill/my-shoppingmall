<template>
  <div class="wrap">
    <el-card class="box">
      <h2 style="text-align:center;margin-bottom:16px;">注册</h2>
      <el-form :model="form" @keyup.enter="onSubmit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" autocomplete="email"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" autocomplete="new-password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const form = reactive({ username: '', email: '', password: '' })
const loading = ref(false)
const auth = useAuthStore()
const router = useRouter()

async function onSubmit() {
  loading.value = true
  try {
    await auth.register(form.username, form.password, form.email)
    ElMessage.success('注册成功，请登录')
    router.replace({ name: 'login' })
  } catch (e) {
    ElMessage.error(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.wrap { display:flex; justify-content:center; padding-top:60px; }
.box { width: 360px; }
</style>

