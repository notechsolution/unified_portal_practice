<template>
  <div class="credential-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统凭证管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加凭证
          </el-button>
        </div>
      </template>

      <el-table :data="credentials" v-loading="loading" style="width: 100%">
        <el-table-column prop="systemName" label="系统名称" width="150" />
        <el-table-column prop="systemId" label="系统ID" width="120" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="systemType" label="系统类型" width="100">
          <template #default="scope">
            <el-tag :type="getSystemTypeTag(scope.row.systemType)">
              {{ getSystemTypeLabel(scope.row.systemType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="80">
          <template #default="scope">
            <el-switch
              v-model="scope.row.enabled"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lastUsedAt" label="最后使用" width="150">
          <template #default="scope">
            {{ formatDate(scope.row.lastUsedAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="autoLoginCount" label="登录次数" width="80" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleAutoLogin(scope.row)"
            >
              <el-icon><Link /></el-icon>
              自动登录
            </el-button>
            <el-button
              type="warning"
              size="small"
              @click="handleTestCredential(scope.row)"
            >
              <el-icon><Connection /></el-icon>
              测试
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="handleEdit(scope.row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑凭证对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingCredential ? '编辑凭证' : '添加凭证'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="credentialFormRef"
        :model="credentialForm"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="系统名称" prop="systemName">
          <el-input v-model="credentialForm.systemName" placeholder="请输入系统名称" />
        </el-form-item>
        <el-form-item label="系统ID" prop="systemId">
          <el-input v-model="credentialForm.systemId" placeholder="请输入系统唯一标识" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="credentialForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="credentialForm.password"
            type="password"
            :placeholder="editingCredential ? '请输入新密码（留空则不修改）' : '请输入密码'"
            show-password
          />
          <template #label>
            <span>
              密码
            </span>
          </template>
        </el-form-item>
        <el-form-item label="登录URL" prop="loginUrl">
          <el-input v-model="credentialForm.loginUrl" placeholder="请输入系统登录URL" />
        </el-form-item>
        <el-form-item label="系统类型" prop="systemType">
          <el-select v-model="credentialForm.systemType" placeholder="请选择系统类型">
            <el-option label="Web系统" value="WEB" />
          </el-select>
        </el-form-item>
        
        <!-- XPath配置 -->
        <el-divider>登录元素XPath配置</el-divider>
        <el-alert
          title="XPath配置说明"
          type="info"
          :closable="false"
          style="margin-bottom: 15px;"
        >
          <template #default>
            <div>
              <p>请配置目标系统登录页面的元素XPath，用于自动填充用户名密码：</p>
              <ul style="margin: 5px 0; padding-left: 20px;">
                <li>用户名输入框XPath：定位用户名输入框</li>
                <li>密码输入框XPath：定位密码输入框</li>
                <li>提交按钮XPath：定位登录提交按钮</li>
                <li>登录表单XPath：定位整个登录表单（可选）</li>
              </ul>
              <p style="margin-top: 5px;">示例：//input[@name='username'] 或 //input[@type='text']</p>
            </div>
          </template>
        </el-alert>
        
        <el-form-item label="用户名XPath" prop="usernameXPath">
          <el-input v-model="credentialForm.usernameXPath" placeholder="例如: //input[@name='username']" />
        </el-form-item>
        <el-form-item label="密码XPath" prop="passwordXPath">
          <el-input v-model="credentialForm.passwordXPath" placeholder="例如: //input[@name='password']" />
        </el-form-item>
        <el-form-item label="提交按钮XPath" prop="submitButtonXPath">
          <el-input v-model="credentialForm.submitButtonXPath" placeholder="例如: //button[@type='submit']" />
        </el-form-item>
        <el-form-item label="登录表单XPath" prop="loginFormXPath">
          <el-input v-model="credentialForm.loginFormXPath" placeholder="例如: //form[@id='loginForm'] (可选)" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="credentialForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入系统描述"
          />
        </el-form-item>
        <el-form-item label="启用状态" prop="enabled">
          <el-switch v-model="credentialForm.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Link, Connection, Edit, Delete } from '@element-plus/icons-vue'
import {
  getUserCredentials,
  createCredential,
  updateCredential,
  deleteCredential,
  performAutoLogin,
  testCredential
} from '@/api/credentials'

const credentials = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const submitLoading = ref(false)
const editingCredential = ref(null)
const credentialFormRef = ref()

const credentialForm = reactive({
  systemName: '',
  systemId: '',
  username: '',
  password: '',
  loginUrl: '',
  systemType: 'WEB_FORM',
  description: '',
  enabled: true,
  usernameXPath: '',
  passwordXPath: '',
  submitButtonXPath: '',
  loginFormXPath: ''
})

// 动态表单验证规则
const getFormRules = () => ({
  systemName: [
    { required: true, message: '请输入系统名称', trigger: 'blur' }
  ],
  systemId: [
    { required: true, message: '请输入系统ID', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { 
      required: !editingCredential.value, // 只有添加模式下才必填
      message: '请输入密码', 
      trigger: 'blur' 
    }
  ],
  loginUrl: [
    { required: true, message: '请输入登录URL', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL', trigger: 'blur' }
  ],
  systemType: [
    { required: true, message: '请选择系统类型', trigger: 'change' }
  ],
  usernameXPath: [
    { required: true, message: '请输入用户名输入框XPath', trigger: 'blur' }
  ],
  passwordXPath: [
    { required: true, message: '请输入密码输入框XPath', trigger: 'blur' }
  ],
  submitButtonXPath: [
    { required: true, message: '请输入提交按钮XPath', trigger: 'blur' }
  ]
})

// 响应式的表单验证规则
const formRules = computed(() => getFormRules())

const fetchCredentials = async () => {
  loading.value = true
  try {
    const response = await getUserCredentials()
    console.log('获取凭证列表响应:', response)
    if (response) {
      credentials.value = response
    } else {
      ElMessage.error('获取凭证列表失败')
    }
  } catch (error) {
    ElMessage.error('获取凭证列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const handleAutoLogin = async (row) => {
  try {
    const response = await performAutoLogin({
      systemId: row.systemId
    })
    console.log('自动登录响应:', response)
    
    if (response && response.success) {
      ElMessage.success('正在准备自动登录...')
      
      // 获取XPath配置数据
      const xpathConfig = response.additionalData
      if (!xpathConfig) {
        ElMessage.error('未获取到XPath配置信息')
        return
      }
      
      // 打开新标签页并注入JavaScript进行自动登录
      performAutoLoginWithJavaScript(xpathConfig)
      
      // 更新最后使用时间和登录次数
      row.lastUsedAt = new Date().toISOString()
      row.autoLoginCount++
      
    } else {
      ElMessage.error('自动登录失败: ' + (response.data?.message || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('自动登录失败: ' + error.message)
  }
}

const performAutoLoginWithJavaScript = (xpathConfig) => {
  // 打开新标签页
  const newWindow = window.open(xpathConfig.loginUrl, '_blank')
  
  if (!newWindow) {
    ElMessage.error('无法打开新窗口，请检查浏览器弹窗拦截设置')
    return
  }
  
  // 等待页面加载完成，然后注入JavaScript
  setTimeout(() => {
    try {
      // 构建自动登录的JavaScript代码 - 使用数据传递方式避免引号问题
      const loginData = {
        username: xpathConfig.username,
        password: xpathConfig.password,
        usernameXPath: xpathConfig.usernameXPath,
        passwordXPath: xpathConfig.passwordXPath,
        submitButtonXPath: xpathConfig.submitButtonXPath
      }
      
      const autoLoginScript = `
        (function() {
          // 从全局变量获取登录数据
          const loginData = window.autoLoginData;
          console.log('自动登录脚本开始执行');
          console.log('当前页面URL:', window.location.href);
          console.log('登录数据:', loginData);
          
          // 等待DOM加载完成
          function waitForElement(xpath, timeout = 10000) {
            return new Promise((resolve, reject) => {
              const startTime = Date.now();
              console.log('开始等待元素，XPath:', xpath);
              
              function checkElement() {
                try {
                  const result = document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
                  const element = result.singleNodeValue;
                  
                  if (element) {
                    console.log('找到元素:', xpath, element);
                    resolve(element);
                  } else if (Date.now() - startTime > timeout) {
                    console.error('元素未找到，XPath:', xpath);
                    reject(new Error('元素未找到: ' + xpath));
                  } else {
                    setTimeout(checkElement, 100);
                  }
                } catch (error) {
                  console.error('XPath执行错误:', error);
                  reject(error);
                }
              }
              
              checkElement();
            });
          }
          
          async function performAutoLogin() {
            try {
              console.log('开始自动登录流程...');
              console.log('用户名XPath:', loginData.usernameXPath);
              console.log('密码XPath:', loginData.passwordXPath);
              console.log('提交按钮XPath:', loginData.submitButtonXPath);
              
              // 等待并填充用户名
              console.log('正在查找用户名输入框...');
              const usernameElement = await waitForElement(loginData.usernameXPath);
              console.log('找到用户名输入框:', usernameElement);
              usernameElement.value = loginData.username;
              usernameElement.dispatchEvent(new Event('input', { bubbles: true }));
              usernameElement.dispatchEvent(new Event('change', { bubbles: true }));
              console.log('用户名已填充，值:', usernameElement.value);
              
              // 等待并填充密码
              console.log('正在查找密码输入框...');
              const passwordElement = await waitForElement(loginData.passwordXPath);
              console.log('找到密码输入框:', passwordElement);
              passwordElement.value = loginData.password;
              passwordElement.dispatchEvent(new Event('input', { bubbles: true }));
              passwordElement.dispatchEvent(new Event('change', { bubbles: true }));
              console.log('密码已填充');
              
              // 等待一小段时间确保表单验证完成
              await new Promise(resolve => setTimeout(resolve, 500));
              
              // 点击提交按钮
              const submitButton = await waitForElement(loginData.submitButtonXPath);
              submitButton.click();
              console.log('登录按钮已点击');
              
              console.log('自动登录流程完成');
              
            } catch (error) {
              console.error('自动登录失败:', error);
              alert('自动登录失败: ' + error.message + '\\n\\n请手动完成登录，或检查XPath配置是否正确。');
            }
          }
          
          // 如果页面已经加载完成，立即执行；否则等待加载完成
          if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', performAutoLogin);
          } else {
            performAutoLogin();
          }
        })();
      `
      
      // 在新窗口中执行脚本，使用数据传递方式
      try {
        if (newWindow && newWindow.document) {
          console.log('准备注入自动登录脚本...')
          console.log('XPath配置:', xpathConfig)
          
          // 先将数据存储到新窗口的全局变量中
          newWindow.autoLoginData = loginData
          
          // 创建script元素来注入代码
          const script = newWindow.document.createElement('script')
          script.textContent = autoLoginScript
          newWindow.document.head.appendChild(script)
          
          console.log('自动登录脚本已注入到新窗口')
        } else {
          console.error('新窗口未正确加载')
          ElMessage.error('新窗口未正确加载，无法注入脚本')
        }
      } catch (error) {
        console.error('注入脚本失败:', error)
        ElMessage.error('注入脚本失败: ' + error.message)
      }
      
      ElMessage.success('自动登录脚本已注入，请在新窗口中查看登录结果')
      
    } catch (error) {
      console.error('注入JavaScript失败:', error)
      ElMessage.error('注入自动登录脚本失败: ' + error.message)
    }
  }, 1000) // 等待1秒让页面开始加载
}

const handleTestCredential = async (row) => {
  try {
    const response = await testCredential(row.id)
    
    if (response.data && response.data.success) {
      ElMessage.success('凭证测试成功')
    } else {
      ElMessage.error('凭证测试失败: ' + (response.data?.message || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('凭证测试失败: ' + error.message)
  }
}

const handleStatusChange = async (row) => {
  try {
    await updateCredential(row.id, { enabled: row.enabled })
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败: ' + error.message)
    // 恢复原状态
    row.enabled = !row.enabled
  }
}

const handleEdit = (row) => {
  editingCredential.value = row
  Object.assign(credentialForm, {
    systemName: row.systemName,
    systemId: row.systemId,
    username: row.username,
    password: '', // 密码不显示，需要重新输入
    loginUrl: row.loginUrl,
    systemType: row.systemType,
    description: row.description,
    enabled: row.enabled,
    usernameXPath: row.usernameXPath || '',
    passwordXPath: row.passwordXPath || '',
    submitButtonXPath: row.submitButtonXPath || '',
    loginFormXPath: row.loginFormXPath || ''
  })
  showAddDialog.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除系统 "${row.systemName}" 的凭证吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteCredential(row.id)
    ElMessage.success('删除成功')
    fetchCredentials()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message)
    }
  }
}

const handleSubmit = async () => {
  await credentialFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (editingCredential.value) {
          // 编辑模式下，如果密码为空，则移除密码字段
          const updateData = { ...credentialForm }
          if (!updateData.password) {
            delete updateData.password
          }
          
          await updateCredential(editingCredential.value.id, updateData)
          ElMessage.success('更新成功')
        } else {
          await createCredential(credentialForm)
          ElMessage.success('创建成功')
        }
        showAddDialog.value = false
        fetchCredentials()
      } catch (error) {
        ElMessage.error(editingCredential.value ? '更新失败' : '创建失败' + ': ' + error.message)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const resetForm = () => {
  editingCredential.value = null
  Object.assign(credentialForm, {
    systemName: '',
    systemId: '',
    username: '',
    password: '',
    loginUrl: '',
    systemType: 'WEB_FORM',
    description: '',
    enabled: true,
    usernameXPath: '',
    passwordXPath: '',
    submitButtonXPath: '',
    loginFormXPath: ''
  })
  credentialFormRef.value?.resetFields()
}

const getSystemTypeLabel = (type) => {
  const labels = {
    'WEB_FORM': 'Web表单'
  }
  return labels[type] || type
}

const getSystemTypeTag = (type) => {
  const tags = {
    'WEB_FORM': 'primary'
  }
  return tags[type] || 'info'
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchCredentials()
})
</script>

<style scoped>
.credential-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>