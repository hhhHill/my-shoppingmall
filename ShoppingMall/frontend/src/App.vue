<template>
  <div>
    <Navbar />
    <main class="container" style="padding: var(--sp-6) 0 var(--sp-8);">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
</script>

<style>
body { margin: 0; }
</style>
