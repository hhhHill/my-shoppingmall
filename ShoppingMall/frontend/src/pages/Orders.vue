<template>
  <div>
    <h2 style="margin-bottom:12px;">我的订单</h2>
    <el-table :data="orders" v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="订单号" width="120" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="totalAmount" label="金额" />
      <el-table-column prop="createdAt" label="创建时间" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../utils/http'

const orders = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const { data } = await api.get('/api/orders')
    orders.value = data?.data || []
  } finally { loading.value = false }
}

onMounted(load)
</script>

