<template>
  <div>
    <div style="display:flex; gap:8px; margin-bottom:8px;">
      <el-date-picker v-model="range" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-card>
      <div style="display:flex; gap:20px; flex-wrap:wrap;">
        <el-statistic title="总订单数" :value="stats.totalOrders" />
        <el-statistic title="已支付订单数" :value="stats.paidOrders" />
        <el-statistic title="销售额" :value="Number(stats.salesAmount || 0).toFixed(2)" />
      </div>
    </el-card>
    <el-card style="margin-top:12px;">
      <template #header>热销商品 Top5</template>
      <el-table :data="stats.topProducts || []" style="width:100%">
        <el-table-column prop="productId" label="商品ID" />
        <el-table-column prop="productName" label="商品名" />
        <el-table-column prop="quantity" label="销量" />
      </el-table>
    </el-card>
  </div>
  
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../utils/http'

const stats = ref({ totalOrders: 0, paidOrders: 0, salesAmount: 0, topProducts: [] })
const range = ref([new Date(Date.now() - 29*24*3600*1000), new Date()])

async function load() {
  const [from, to] = range.value || []
  if (!from || !to) return
  const params = {
    from: from.toISOString().slice(0,10),
    to: to.toISOString().slice(0,10)
  }
  const { data } = await api.get('/api/admin/stats/sales', { params })
  stats.value = data || stats.value
}

onMounted(load)
</script>
