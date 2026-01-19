<template>
  <div class="dashboard-container">
    <el-container class="full-container">
      <el-aside width="200px">
        <div class="logo-section">
          <h3>Unified Portal</h3>
        </div>
        <el-menu
          default-active="1"
          class="el-menu-vertical"
          @select="handleSelect"
        >
          <el-menu-item index="1">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="2">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="3">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container class="main-container">
        <el-header>
          <h2>控制台</h2>
          <div class="user-info">
            <span>{{ userStore.user?.username || '用户' }}</span>
            <el-button type="danger" size="small" @click="handleLogout">
              退出登录
            </el-button>
          </div>
        </el-header>
        
        <el-main>
          <el-card>
            <h3>欢迎使用系统</h3>
            <el-row :gutter="20" style="margin-top: 20px">
              <el-col :span="8">
                <el-statistic title="用户总数" :value="268500" />
              </el-col>
              <el-col :span="8">
                <el-statistic title="今日访问" :value="79800" />
              </el-col>
              <el-col :span="8">
                <el-statistic title="在线用户" :value="12600" />
              </el-col>
            </el-row>
          </el-card>
        </el-main>
        
        <el-footer>
          <div class="footer-content">
            <p>&copy; 2025 Unified Portal. All rights reserved.</p>
            <p>技术支持：Vue 3 + Spring Boot 3 + MongoDB</p>
          </div>
        </el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, User, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleSelect = (index) => {
  console.log('选中菜单:', index)
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.dashboard-container {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.full-container {
  height: 100%;
}

.el-aside {
  height: 100%;
  background-color: #ccd6e0;
  color: #fff;
  display: flex;
  flex-direction: column;
}

.logo-section {
  padding: 20px;
  text-align: center;
  background-color: #434a50;
  border-bottom: 1px solid #3a3f44;
}

.logo-section h3 {
  margin: 0;
  color: #fff;
  font-size: 18px;
  font-weight: 500;
}

.el-menu {
  flex: 1;
  border-right: none;
  background-color: #edf0f3;
}

.el-menu-vertical {
  height: 100%;
}

.main-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  height: 60px;
  padding: 0 20px;
  flex-shrink: 0;
}

.el-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info span {
  color: #606266;
  font-size: 14px;
}

.el-main {
  flex: 1;
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

.el-footer {
  height: 60px;
  background-color: #fff;
  border-top: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.footer-content {
  text-align: center;
}

.footer-content p {
  margin: 2px 0;
  font-size: 12px;
  color: #909399;
}

.footer-content p:first-child {
  font-weight: 500;
  color: #606266;
}
</style>
