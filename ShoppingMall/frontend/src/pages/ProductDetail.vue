<template>
  <div v-if="product">
    <el-row :gutter="20">
      <el-col :md="10">
        <img v-if="product.imageUrl" :src="product.imageUrl" style="width:100%; border-radius:8px;" />
      </el-col>
      <el-col :md="14">
        <h2>{{ product.name }}</h2>
        <p style="color:#f56c6c;font-size:20px;">￥{{ Number(product.price).toFixed(2) }}</p>
        <p style="white-space:pre-wrap;">{{ product.description }}</p>
        <div style="margin-top:16px; display:flex; gap:8px; align-items:center;">
          <el-input-number v-model="quantity" :min="1" :max="product.stock || 999" />
          <el-button type="primary" @click="addToCart">加入购物车</el-button>
        </div>
      </el-col>
    </el-row>
  </div>
  <el-skeleton v-else :rows="6" animated />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../utils/http'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const product = ref(null)
const quantity = ref(1)

async function load() {
  const { data } = await api.get(`/api/products/${route.params.id}`)
  product.value = data?.data
}

async function addToCart() {
  if (!auth.isAuthenticated) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  await api.post('/api/cart', { productId: product.value.id, quantity: quantity.value })
  ElMessage.success('已加入购物车')
}

onMounted(load)
</script>

