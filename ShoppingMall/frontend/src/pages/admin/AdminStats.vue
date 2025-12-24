<template>
  <div>
    <el-card>
      <div style="display:flex; gap:16px;">
        <el-statistic title="用户数" :value="stats.totalUsers" />
        <el-statistic title="订单数" :value="stats.totalOrders" />
        <el-statistic title="商品数" :value="stats.totalProducts" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../utils/http'

const stats = ref({ totalUsers: 0, totalOrders: 0, totalProducts: 0 })

async function load() {
  const { data } = await api.get('/api/admin/stats')
  stats.value = data?.data || stats.value
}

onMounted(load)
</script>

