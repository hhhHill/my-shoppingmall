<template>
  <div>
    <h2 style="margin-bottom:12px;">我的订单</h2>
    <el-table :data="orders" v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="订单号" width="120" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="totalAmount" label="金额" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button v-if="row.status==='CREATED'" size="small" type="primary" @click="pay(row)">支付</el-button>
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
import api from '../utils/http'
import { ElMessage } from 'element-plus'

const orders = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const { data } = await api.get('/api/orders', { params: { page: page.value - 1, size: size.value } })
    orders.value = data.items || []
    total.value = data.totalElements || 0
  } finally { loading.value = false }
}

onMounted(load)

async function pay(row) {
  await api.post(`/api/orders/${row.id}/pay`)
  ElMessage.success('支付成功')
  load()
}
</script>
