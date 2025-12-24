<template>
  <div>
    <Navbar />
    <div style="padding:16px; max-width: 1200px; margin: 0 auto;">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
</script>

<style>
body { margin: 0; }
</style>

