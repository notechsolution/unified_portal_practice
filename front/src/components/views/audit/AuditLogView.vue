<template>
  <div class="audit-log-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>安全审计日志</span>
          <el-button type="primary" @click="exportLogs" :loading="exportLoading">
            <el-icon><Download /></el-icon>
            导出日志
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.action" placeholder="请选择操作类型" clearable>
            <el-option label="访问凭证" value="ACCESS_CREDENTIAL" />
            <el-option label="自动登录" value="AUTO_LOGIN" />
            <el-option label="修改凭证" value="MODIFY_CREDENTIAL" />
            <el-option label="创建凭证" value="CREATE_CREDENTIAL" />
            <el-option label="删除凭证" value="DELETE_CREDENTIAL" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            clearable
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 统计信息 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="总操作次数" :value="stats.totalOperations" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="成功次数" :value="stats.successCount" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="失败次数" :value="stats.failedCount" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <el-statistic title="成功率" :value="stats.successRate" suffix="%" />
          </el-card>
        </el-col>
      </el-row>

      <!-- 审计日志表格 -->
      <el-table
        :data="auditLogs"
        v-loading="loading"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'timestamp', order: 'descending' }"
      >
        <el-table-column prop="timestamp" label="时间" width="180" sortable>
          <template #default="scope">
            {{ formatDateTime(scope.row.timestamp) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="action" label="操作类型" width="120">
          <template #default="scope">
            <el-tag :type="getActionType(scope.row.action)">
              {{ getActionLabel(scope.row.action) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="systemId" label="目标系统" width="150" />
        
        <el-table-column prop="credentialId" label="凭证ID" width="200" />
        
        <el-table-column prop="success" label="状态" width="80">
          <template #default="scope">
            <el-icon v-if="scope.row.success" style="color: #67c23a">
              <CircleCheck />
            </el-icon>
            <el-icon v-else style="color: #f56c6c">
              <CircleClose />
            </el-icon>
          </template>
        </el-table-column>
        
        <el-table-column prop="message" label="详细信息" min-width="200" />
        
        <el-table-column prop="ipAddress" label="IP地址" width="120" />
        
        <el-table-column prop="userAgent" label="用户代理" min-width="200" show-overflow-tooltip />
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Download, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { getUserAuditLogs } from '@/api/audit'

// 数据
const auditLogs = ref([])
const loading = ref(false)
const exportLoading = ref(false)

// 搜索表单
const searchForm = reactive({
  action: '',
  dateRange: null
})

// 分页
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 统计信息
const stats = reactive({
  totalOperations: 0,
  successCount: 0,
  failedCount: 0,
  successRate: 0
})

// 方法
const fetchAuditLogs = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.append('page', pagination.current - 1)
    params.append('size', pagination.size)
    
    if (searchForm.action) {
      params.append('action', searchForm.action)
    }
    
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.append('startDate', searchForm.dateRange[0])
      params.append('endDate', searchForm.dateRange[1])
    }

    const response = await getUserAuditLogs(params)
    
    if (response.success) {
      auditLogs.value = response.data
      pagination.total = response.data.length // 这里假设后端返回了总数
      calculateStats()
    } else {
      ElMessage.error('获取审计日志失败')
    }
  } catch (error) {
    console.error('获取审计日志失败:', error)
    ElMessage.error('获取审计日志失败')
  } finally {
    loading.value = false
  }
}

const calculateStats = () => {
  const logs = auditLogs.value
  stats.totalOperations = logs.length
  stats.successCount = logs.filter(log => log.success).length
  stats.failedCount = stats.totalOperations - stats.successCount
  stats.successRate = stats.totalOperations > 0 
    ? Math.round((stats.successCount / stats.totalOperations) * 100) 
    : 0
}

const handleSearch = () => {
  pagination.current = 1
  fetchAuditLogs()
}

const resetSearch = () => {
  searchForm.action = ''
  searchForm.dateRange = null
  handleSearch()
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchAuditLogs()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  fetchAuditLogs()
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getActionType = (action) => {
  const typeMap = {
    'ACCESS_CREDENTIAL': 'info',
    'AUTO_LOGIN': 'warning',
    'MODIFY_CREDENTIAL': 'danger',
    'CREATE_CREDENTIAL': 'success',
    'DELETE_CREDENTIAL': 'danger'
  }
  return typeMap[action] || 'info'
}

const getActionLabel = (action) => {
  const labelMap = {
    'ACCESS_CREDENTIAL': '访问凭证',
    'AUTO_LOGIN': '自动登录',
    'MODIFY_CREDENTIAL': '修改凭证',
    'CREATE_CREDENTIAL': '创建凭证',
    'DELETE_CREDENTIAL': '删除凭证'
  }
  return labelMap[action] || action
}

const exportLogs = async () => {
  exportLoading.value = true
  try {
    // 获取所有日志数据
    const params = new URLSearchParams()
    if (searchForm.action) {
      params.append('action', searchForm.action)
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.append('startDate', searchForm.dateRange[0])
      params.append('endDate', searchForm.dateRange[1])
    }

    const response = await getUserAuditLogs(params)
    
    if (response.success) {
      const data = response.data
      const csvContent = convertToCSV(data)
      downloadCSV(csvContent, `audit_logs_${new Date().toISOString().slice(0, 10)}.csv`)
      ElMessage.success('日志导出成功')
    } else {
      ElMessage.error('导出日志失败')
    }
  } catch (error) {
    console.error('导出日志失败:', error)
    ElMessage.error('导出日志失败')
  } finally {
    exportLoading.value = false
  }
}

const convertToCSV = (data) => {
  const headers = ['时间', '操作类型', '目标系统', '凭证ID', '状态', '详细信息', 'IP地址', '用户代理']
  const rows = data.map(item => [
    formatDateTime(item.timestamp),
    getActionLabel(item.action),
    item.systemId || '',
    item.credentialId || '',
    item.success ? '成功' : '失败',
    item.message || '',
    item.ipAddress || '',
    item.userAgent || ''
  ])
  
  return [headers, ...rows].map(row => 
    row.map(field => `"${field}"`).join(',')
  ).join('\n')
}

const downloadCSV = (content, filename) => {
  const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', filename)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 生命周期
onMounted(() => {
  fetchAuditLogs()
})
</script>

<style scoped>
.audit-log-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-statistic__head) {
  margin-bottom: 8px;
}

:deep(.el-statistic__content) {
  font-size: 24px;
}
</style>