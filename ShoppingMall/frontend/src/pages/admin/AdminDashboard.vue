<template>
  <div>
    <h2 style="margin-bottom:12px;">后台管理</h2>
    <el-tabs v-model="tab">
      <el-tab-pane label="商品管理" name="products">
        <AdminProducts />
      </el-tab-pane>
      <el-tab-pane label="订单管理" name="orders">
        <AdminOrders />
      </el-tab-pane>
      <el-tab-pane label="用户管理" name="users">
        <AdminUsers />
      </el-tab-pane>
      <el-tab-pane label="销售统计" name="stats">
        <AdminStats />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AdminProducts from './AdminProducts.vue'
import AdminUsers from './AdminUsers.vue'
import AdminStats from './AdminStats.vue'
import AdminOrders from './AdminOrders.vue'

const tab = ref('products')
</script>
