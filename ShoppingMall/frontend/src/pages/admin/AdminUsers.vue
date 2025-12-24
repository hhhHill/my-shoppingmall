<template>
  <div>
    <div style="display:flex; gap:8px; margin-bottom:8px;">
      <el-input v-model="keyword" placeholder="搜索用户名/邮箱" style="max-width:300px" @keyup.enter="load" />
      <el-button type="primary" @click="load">搜索</el-button>
    </div>
    <el-table :data="users" v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="角色" width="220">
        <template #default="{ row }">
          <el-select v-model="row.role" style="width:160px">
            <el-option label="USER" value="USER" />
            <el-option label="ADMIN" value="ADMIN" />
          </el-select>
          <el-button size="small" @click="saveRole(row)" style="margin-left:8px;">保存</el-button>
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

const users = ref([])
const loading = ref(false)
const keyword = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const { data } = await api.get('/api/admin/users', { params: { page: page.value - 1, size: size.value, keyword: keyword.value || undefined } })
    users.value = data.items || []
    total.value = data.totalElements || 0
  } finally { loading.value = false }
}

async function saveRole(row) {
  await api.put(`/api/admin/users/${row.id}/role`, { role: row.role })
  ElMessage.success('角色已更新')
}

onMounted(load)
</script>
