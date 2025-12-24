<template>
  <div>
    <div style="display:flex; justify-content:space-between; margin-bottom:8px;">
      <el-input v-model="q" placeholder="搜索商品" style="max-width:320px;" @keyup.enter="load" />
      <el-button type="primary" @click="showCreate=true">新建商品</el-button>
    </div>
    <el-table :data="products" v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="price" label="价格" />
      <el-table-column prop="stock" label="库存" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="edit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showCreate" title="新建商品" width="500">
      <el-form :model="form" label-width="80">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="价格"><el-input v-model.number="form.price" /></el-form-item>
        <el-form-item label="库存"><el-input v-model.number="form.stock" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="图片"><el-input v-model="form.imageUrl" /></el-form-item>
        <el-form-item label="描述"><el-input type="textarea" v-model="form.description" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate=false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '../../utils/http'
import { ElMessage, ElMessageBox } from 'element-plus'

const q = ref('')
const products = ref([])
const loading = ref(false)
const showCreate = ref(false)
const saving = ref(false)
const form = reactive({ name: '', price: 0, stock: 0, category: '', imageUrl: '', description: '' })

async function load() {
  loading.value = true
  try {
    const { data } = await api.get('/api/products', { params: { keyword: q.value || undefined, page: 0, size: 1000 } })
    products.value = data?.items || []
  } finally { loading.value = false }
}

function edit(row) {
  Object.assign(form, row)
  showCreate.value = true
}

async function del(row) {
  await ElMessageBox.confirm('删除后不可恢复，确认吗？', '提示')
  await api.delete(`/api/admin/products/${row.id}`)
  ElMessage.success('已删除')
  load()
}

async function save() {
  saving.value = true
  try {
    if (form.id) {
      await api.put(`/api/admin/products/${form.id}`, form)
    } else {
      await api.post('/api/admin/products', form)
    }
    ElMessage.success('已保存')
    showCreate.value = false
    Object.assign(form, { name: '', price: 0, stock: 0, category: '', imageUrl: '', description: '', id: undefined })
    load()
  } finally { saving.value = false }
}

onMounted(load)
</script>
