<template>
  <el-card class="card" shadow="hover" @click="$router.push(`/product/${product.id}`)">
    <img v-if="product.imageUrl" :src="product.imageUrl" class="img" />
    <div class="name">{{ product.name }}</div>
    <div class="price">￥{{ Number(product.price).toFixed(2) }}</div>
  </el-card>
</template>

<script setup>
defineProps({ product: { type: Object, required: true } })
</script>

<style scoped>
.card { cursor: pointer; }
.img { width: 100%; height: 160px; object-fit: cover; }
.name { margin-top: 8px; font-weight: 600; }
.price { color: #f56c6c; margin-top: 4px; }
</style>

