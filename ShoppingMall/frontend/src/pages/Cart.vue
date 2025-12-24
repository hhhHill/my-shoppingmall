<template>
  <div>
    <h2 style="margin-bottom:12px;">我的购物车</h2>
    <el-table :data="items" v-loading="loading" style="width: 100%">
      <el-table-column prop="product.name" label="商品" />
      <el-table-column label="数量">
        <template #default="{ row }">
          <el-input-number v-model="row.quantity" @change="(v)=>update(row,v)" :min="1" />
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="danger" link @click="remove(row)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top:12px; text-align:right;">
      <el-button type="primary" @click="checkout" :disabled="items.length===0">去结算</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../utils/http'
import { ElMessage } from 'element-plus'

const items = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const { data } = await api.get('/api/cart')
    items.value = data?.data || []
  } finally { loading.value = false }
}

async function update(row, v) {
  await api.put(`/api/cart/${row.id}`, { productId: row.product.id, quantity: v })
}

async function remove(row) {
  await api.delete(`/api/cart/${row.id}`)
  ElMessage.success('已移除')
  load()
}

async function checkout() {
  await api.post('/api/orders', {})
  ElMessage.success('下单成功(示例)')
}

onMounted(load)
</script>

