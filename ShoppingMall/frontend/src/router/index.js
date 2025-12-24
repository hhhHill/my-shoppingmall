import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const Products = () => import('../pages/Products.vue')
const ProductDetail = () => import('../pages/ProductDetail.vue')
const Login = () => import('../pages/Login.vue')
const Cart = () => import('../pages/Cart.vue')
const Orders = () => import('../pages/Orders.vue')
const AdminDashboard = () => import('../pages/admin/AdminDashboard.vue')

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: Products },
    { path: '/login', name: 'login', component: Login },
    { path: '/product/:id', name: 'product', component: ProductDetail },
    { path: '/cart', name: 'cart', component: Cart, meta: { requiresAuth: true } },
    { path: '/orders', name: 'orders', component: Orders, meta: { requiresAuth: true } },
    { path: '/admin', name: 'admin', component: AdminDashboard, meta: { requiresAuth: true, requiresAdmin: true } },
  ]
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && auth.user?.role !== 'ADMIN') {
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router

