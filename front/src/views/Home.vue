<template>
  <div class="home-container">
    <el-container>
      <el-header>
        <h1>欢迎来到 Unified Portal</h1>
        <div class="header-actions">
          <el-button type="success" @click="goToRegister">注册</el-button>
          <el-button type="primary" @click="goToLogin">登录</el-button>
        </div>
      </el-header>
      <el-main>
        <el-card class="intro-card">
          <h2>项目介绍</h2>
          <p>这是一个前后端一体的演示项目</p>
          <ul>
            <li>前端：Vue.js 3.x + Element Plus + Pinia + Vite</li>
            <li>后端：Spring Boot 3.x + Spring Security + MongoDB</li>
          </ul>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.home-container {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  color: white;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.el-main {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.intro-card {
  max-width: 600px;
  width: 100%;
}

h2 {
  margin-bottom: 20px;
  color: #409eff;
}

ul {
  margin-top: 15px;
  padding-left: 20px;
}

li {
  margin: 10px 0;
  line-height: 1.6;
}
</style>
