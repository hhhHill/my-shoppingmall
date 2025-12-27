<template>
  <div>
    <!-- 首屏标题与 banner -->
    <section style="padding: var(--sp-7) 0 var(--sp-6);">
      <div class="h1">发现好物</div>
      <div class="subtle" style="margin-top: var(--sp-3);">极简 · 高级 · 留白设计</div>
      <div class="ui-card hoverable clickable" @click="goCategory('季末特惠')" style="margin-top: var(--sp-6); padding: var(--sp-7); text-align:center;">
        <div class="h2">季末特惠</div>
        <div class="subtle" style="margin-top: var(--sp-3);">全场精选，低至 8 折</div>
      </div>
    </section>

    <!-- 顶部搜索条 -->
    <section style="padding-bottom: var(--sp-6); display:flex; gap: var(--sp-4); align-items:center;">
      <input class="ui-input" v-model="q" placeholder="搜索商品…" @keyup.enter="load" style="max-width: 360px;" />
      <button class="ui-btn is-primary" @click="load">搜索</button>
    </section>

    <!-- 类目入口（占位）-->
    <section style="padding-bottom: var(--sp-6);">
      <div class="grid grid-4">
        <div class="ui-card hoverable clickable" @click="goCategory('数码电器')" style="padding: var(--sp-6);">数码电器</div>
        <div class="ui-card hoverable clickable" @click="goCategory('美妆个护')" style="padding: var(--sp-6);">美妆个护</div>
        <div class="ui-card hoverable clickable" @click="goCategory('家居生活')" style="padding: var(--sp-6);">家居生活</div>
        <div class="ui-card hoverable clickable" @click="goCategory('服饰鞋包')" style="padding: var(--sp-6);">服饰鞋包</div>
      </div>
    </section>

    <!-- 商品列表 -->
    <section>
      <div class="h2" style="margin-bottom: var(--sp-5);">为你推荐</div>
      <div v-if="loading" class="grid grid-4">
        <div v-for="i in 8" :key="i" class="ui-card" style="padding: var(--sp-5);">
          <div class="product-media skeleton"></div>
          <div class="skeleton" style="height:18px; margin-top: var(--sp-4);"></div>
          <div class="skeleton" style="height:14px; width:60%; margin-top: var(--sp-3);"></div>
        </div>
      </div>
      <div v-else-if="error" class="state">
        <div class="icon">⚠️</div>
        加载失败，请稍后重试
        <div style="margin-top: var(--sp-4);"><button class="ui-btn is-outline" @click="load">重试</button></div>
      </div>
      <div v-else-if="products.length === 0" class="state">
        <div class="icon">🕊️</div>
        暂无商品
      </div>
      <div v-else class="grid grid-4">
        <ProductCard v-for="p in products" :key="p.id" :product="p" />
      </div>
      <div style="margin-top: var(--sp-6); text-align:center;">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="prev, pager, next"
          @current-change="load"
          @size-change="load"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../utils/http'
import ProductCard from '../components/ProductCard.vue'

const q = ref('')
const loading = ref(false)
const error = ref(false)
const products = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)
const route = useRoute()
const router = useRouter()
const category = ref(route.query.category || '')

async function load() {
  loading.value = true
  error.value = false
  try {
    const { data } = await api.get('/api/products', { params: { page: page.value - 1, size: size.value, keyword: q.value || undefined, category: category.value || undefined }})
    // data is PageResponse
    products.value = data.items || []
    total.value = data.totalElements || 0
  } catch (e) {
    error.value = true
  } finally {
    loading.value = false
  }
}

onMounted(load)

watch(() => route.query.category, (val) => {
  category.value = val || ''
  page.value = 1
  load()
})

function goCategory(name) {
  if (category.value === name) return
  q.value = ''
  page.value = 1
  router.push({ name: 'home', query: { ...route.query, category: name } })
}
</script>

<style scoped>
.clickable { cursor: pointer; }
</style>
