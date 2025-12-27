<template>
  <div>
    <h2 style="margin-bottom:12px;">我的购物车</h2>
    <el-table :data="items" v-loading="loading" style="width: 100%" row-key="productId">
      <el-table-column prop="productName" label="商品" />
      <el-table-column label="数量">
        <template #default="{ row }">
          <el-input-number :model-value="row.quantity" @change="(v)=>update(row,v)" :min="0" />
        </template>
      </el-table-column>
      <el-table-column label="单价">
        <template #default="{ row }">¥ {{ toMoney(row.price) }}</template>
      </el-table-column>
      <el-table-column label="小计">
        <template #default="{ row }">¥ {{ toMoney(row.subtotal) }}</template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="danger" link :loading="removing.has(row.productId)" @click="remove(row)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top:12px; display:flex; justify-content:space-between; align-items:center;">
      <div class="subtle">共 {{ totalCount }} 件商品</div>
      <div style="display:flex; align-items:center; gap:12px;">
        <div>合计：<strong style="color: var(--c-primary);">¥ {{ toMoney(totalAmount) }}</strong></div>
        <el-button type="primary" @click="checkout" :disabled="items.length===0">去结算</el-button>
      </div>
    </div>
  </div>
  
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '../utils/http'
import { ElMessage } from 'element-plus'

const items = ref([])
const loading = ref(false)
const removing = new Set()

const totalAmount = computed(() => items.value.reduce((sum, it) => sum + toNum(it.price) * toNum(it.quantity), 0))
const totalCount = computed(() => items.value.reduce((sum, it) => sum + toNum(it.quantity), 0))

function toNum(v){ const n = Number(v); return Number.isFinite(n) ? n : 0 }
function toMoney(v){ return toNum(v).toFixed(2) }

async function load() {
  loading.value = true
  try {
    const { data } = await api.get('/api/cart')
    items.value = (data || []).map(it => ({ ...it }))
  } finally { loading.value = false }
}

async function update(row, v) {
  if (v <= 0) { await remove(row); return }
  const old = { q: row.quantity, s: row.subtotal }
  row.quantity = v
  row.subtotal = toNum(row.price) * toNum(row.quantity)
  try {
    await api.post('/api/cart/items', { productId: row.productId, quantity: v })
  } catch (e) {
    // revert on failure
    row.quantity = old.q
    row.subtotal = old.s
    ElMessage.error('更新数量失败')
  }
}

async function remove(row) {
  // optimistic remove
  const prev = items.value.slice()
  items.value = items.value.filter(it => it.productId !== row.productId)
  removing.add(row.productId)
  try {
    // use unified endpoint: quantity=0 means remove
    await api.post('/api/cart/items', { productId: row.productId, quantity: 0 })
    ElMessage.success('已移除')
  } catch (e) {
    items.value = prev
    ElMessage.error('移除失败')
  } finally {
    removing.delete(row.productId)
  }
}

async function checkout() {
  await api.post('/api/orders', {})
  ElMessage.success('下单成功')
  await load()
}

onMounted(load)
</script>
