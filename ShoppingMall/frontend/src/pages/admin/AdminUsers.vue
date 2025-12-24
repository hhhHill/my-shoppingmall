<template>
  <div>
    <el-table :data="users" v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="role" label="角色" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../utils/http'

const users = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const { data } = await api.get('/api/admin/users')
    users.value = data?.data || []
  } finally { loading.value = false }
}

onMounted(load)
</script>

