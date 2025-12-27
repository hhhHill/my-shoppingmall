<template>
  <article class="ui-card hoverable card" @click="$router.push(`/product/${product.id}`)" role="button" tabindex="0">
    <div class="product-media">
      <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name" loading="lazy" />
    </div>
    <h3 class="title">{{ product.name }}</h3>
    <div class="meta">
      <span class="price">¥ {{ Number(product.price).toFixed(2) }}</span>
      <span v-if="product.stock!==undefined && product.stock<=5" class="ui-tag is-danger">库存紧张</span>
    </div>
  </article>
</template>

<script setup>
defineProps({ product: { type: Object, required: true } })
</script>

<style scoped>
.card{ cursor: pointer; padding: var(--sp-5); }
.title{ margin-top: var(--sp-4); font-weight: var(--fw-semibold); font-size: var(--fs-h3); line-height: 1.4; display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden; }
.meta{ display:flex; align-items:center; gap: var(--sp-4); margin-top: var(--sp-3); }
.price{ color: var(--c-primary); font-weight: var(--fw-semibold); }
</style>
