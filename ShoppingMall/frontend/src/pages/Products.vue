<template>
  <div>
    <div style="display:flex; gap:8px; margin-bottom:12px;">
      <el-input v-model="q" placeholder="搜索商品" clearable style="max-width:360px" />
      <el-button type="primary" @click="load">搜索</el-button>
    </div>
    <el-row :gutter="12">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="p in products" :key="p.id" style="margin-bottom:12px;">
        <ProductCard :product="p" />
      </el-col>
    </el-row>
    <el-empty v-if="!loading && products.length===0" description="暂无商品" />
    <div style="margin-top:12px; text-align:center;">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="prev, pager, next"
        @current-change="load"
        @size-change="load"
      />
    </div>
  </div>
  
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../utils/http'
import ProductCard from '../components/ProductCard.vue'

const q = ref('')
const loading = ref(false)
const products = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const { data } = await api.get('/api/products', { params: { page: page.value - 1, size: size.value, keyword: q.value || undefined }})
    // data is PageResponse
    products.value = data.items || []
    total.value = data.totalElements || 0
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
