<template>
  <div>
    <div style="display:flex; gap:8px; margin-bottom:8px; align-items:center;">
      <el-select v-model="status" placeholder="状态" clearable style="width:160px">
        <el-option label="CREATED" value="CREATED" />
        <el-option label="PAID" value="PAID" />
        <el-option label="SHIPPED" value="SHIPPED" />
        <el-option label="COMPLETED" value="COMPLETED" />
        <el-option label="CANCELLED" value="CANCELLED" />
      </el-select>
      <el-input v-model.number="userId" placeholder="用户ID(可选)" style="max-width:160px" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="orders" v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="订单号" width="120" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="totalAmount" label="金额" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="360">
        <template #default="{ row }">
          <el-button v-for="s in allowedTargets(row.status)" :key="s" size="small" @click="updateStatus(row, s)">{{ s }}</el-button>
        </template>
      </el-table-column>
    </el-table>
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
import api from '../../utils/http'
import { ElMessage } from 'element-plus'

const orders = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const status = ref('')
const userId = ref()

async function load() {
  loading.value = true
  try {
    const params = { page: page.value - 1, size: size.value }
    if (status.value) params.status = status.value
    if (userId.value) params.userId = userId.value
    const { data } = await api.get('/api/admin/orders', { params })
    orders.value = data.items || []
    total.value = data.totalElements || 0
  } finally { loading.value = false }
}

function allowedTargets(current) {
  switch (current) {
    case 'CREATED': return ['CANCELLED']
    case 'PAID': return ['SHIPPED', 'CANCELLED']
    case 'SHIPPED': return ['COMPLETED', 'CANCELLED']
    default: return []
  }
}

async function updateStatus(row, s) {
  await api.put(`/api/admin/orders/${row.id}/status`, { status: s })
  ElMessage.success('状态已更新')
  load()
}

onMounted(load)
</script>

