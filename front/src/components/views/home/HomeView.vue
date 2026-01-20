<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <template #header>
            <div class="card-header">
              <span>欢迎使用 Vue Demo 应用</span>
            </div>
          </template>
          <div class="welcome-content">
            <h2>欢迎使用现代化的前后端分离应用</h2>
            <p>这是一个基于 Vue.js 3.x 和 Spring Boot 3.x 的演示项目</p>
            <div class="feature-list">
              <el-row :gutter="20">
                <el-col :span="8">
                  <div class="feature-item">
                    <el-icon size="48" color="#409eff"><Promotion /></el-icon>
                    <h3>现代化技术栈</h3>
                    <p>Vue 3 + Element Plus + Pinia + Vite</p>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="feature-item">
                    <el-icon size="48" color="#67c23a"><Lock /></el-icon>
                    <h3>安全可靠</h3>
                    <p>Spring Security + JWT 身份认证</p>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="feature-item">
                    <el-icon size="48" color="#e6a23c"><DataLine /></el-icon>
                    <h3>数据可视化</h3>
                    <p>丰富的图表和数据分析功能</p>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>快速开始</span>
          </template>
          <div class="quick-start">
            <el-button type="primary" @click="goToDashboard">进入仪表板</el-button>
            <el-button @click="goToProfile">查看用户资料</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>系统信息</span>
          </template>
          <div class="system-info">
            <p><strong>前端版本:</strong> 1.0.0</p>
            <p><strong>后端版本:</strong> 1.0.0</p>
            <p><strong>Vue版本:</strong> 3.x</p>
            <p><strong>Element Plus:</strong> 2.x</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Promotion, Lock, DataLine } from '@element-plus/icons-vue'

const router = useRouter()

const goToDashboard = () => {
  router.push('/dashboard')
}

const goToProfile = () => {
  router.push('/profile')
}
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.welcome-content {
  text-align: center;
  padding: 20px;
}

.welcome-content h2 {
  color: #333;
  margin-bottom: 10px;
}

.welcome-content p {
  color: #666;
  margin-bottom: 30px;
}

.feature-list {
  margin-top: 30px;
}

.feature-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
}

.feature-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.feature-item h3 {
  margin: 15px 0 10px;
  color: #333;
}

.feature-item p {
  color: #666;
  font-size: 14px;
}

.quick-start {
  text-align: center;
  padding: 20px;
}

.system-info {
  padding: 20px;
}

.system-info p {
  margin-bottom: 10px;
  color: #666;
}
</style>