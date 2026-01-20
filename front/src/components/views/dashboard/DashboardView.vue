<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="48" color="#409eff"><User /></el-icon>
            </div>
            <div class="stat-info">
              <h3>用户总数</h3>
              <p class="stat-number">{{ stats.totalUsers }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="48" color="#67c23a"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <h3>今日访问</h3>
              <p class="stat-number">{{ stats.todayVisits }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="48" color="#e6a23c"><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <h3>总收入</h3>
              <p class="stat-number">¥{{ stats.totalRevenue }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="48" color="#f56c6c"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <h3>转化率</h3>
              <p class="stat-number">{{ stats.conversionRate }}%</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card title="访问趋势">
          <template #header>
            <span>访问趋势</span>
          </template>
          <div class="chart-placeholder">
            <p>图表组件待实现</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="用户分布">
          <template #header>
            <span>用户分布</span>
          </template>
          <div class="chart-placeholder">
            <p>图表组件待实现</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, TrendCharts, Coin, DataAnalysis } from '@element-plus/icons-vue'

const stats = ref({
  totalUsers: 1234,
  todayVisits: 567,
  totalRevenue: 89012,
  conversionRate: 23.5
})

onMounted(() => {
  // 这里可以加载真实的统计数据
  console.log('Dashboard mounted')
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  margin-right: 20px;
}

.stat-info h3 {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
  font-weight: normal;
}

.stat-number {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.chart-placeholder p {
  color: #999;
  font-size: 16px;
}
</style>