<template>
  <div class="settings-container">
    <el-card class="settings-card">
      <template #header>
        <span>系统设置</span>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本设置" name="basic">
          <el-form
            ref="basicFormRef"
            :model="basicForm"
            :rules="basicRules"
            label-width="120px"
          >
            <el-form-item label="网站标题" prop="siteTitle">
              <el-input v-model="basicForm.siteTitle" placeholder="请输入网站标题" />
            </el-form-item>
            
            <el-form-item label="网站描述" prop="siteDescription">
              <el-input
                v-model="basicForm.siteDescription"
                type="textarea"
                :rows="3"
                placeholder="请输入网站描述"
              />
            </el-form-item>
            
            <el-form-item label="网站Logo" prop="siteLogo">
              <el-upload
                class="logo-uploader"
                action="/api/upload/logo"
                :show-file-list="false"
                :on-success="handleLogoSuccess"
                :before-upload="beforeLogoUpload"
              >
                <img v-if="basicForm.siteLogo" :src="basicForm.siteLogo" class="logo" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            
            <el-form-item label="备案号" prop="icpNumber">
              <el-input v-model="basicForm.icpNumber" placeholder="请输入备案号" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSaveBasic" :loading="basicLoading">
                保存设置
              </el-button>
              <el-button @click="resetBasicForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="邮件设置" name="email">
          <el-form
            ref="emailFormRef"
            :model="emailForm"
            :rules="emailRules"
            label-width="120px"
          >
            <el-form-item label="SMTP服务器" prop="smtpHost">
              <el-input v-model="emailForm.smtpHost" placeholder="请输入SMTP服务器地址" />
            </el-form-item>
            
            <el-form-item label="SMTP端口" prop="smtpPort">
              <el-input-number v-model="emailForm.smtpPort" :min="1" :max="65535" />
            </el-form-item>
            
            <el-form-item label="发件人邮箱" prop="fromEmail">
              <el-input v-model="emailForm.fromEmail" placeholder="请输入发件人邮箱" />
            </el-form-item>
            
            <el-form-item label="发件人名称" prop="fromName">
              <el-input v-model="emailForm.fromName" placeholder="请输入发件人名称" />
            </el-form-item>
            
            <el-form-item label="用户名" prop="username">
              <el-input v-model="emailForm.username" placeholder="请输入用户名" />
            </el-form-item>
            
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="emailForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSaveEmail" :loading="emailLoading">
                保存设置
              </el-button>
              <el-button @click="testEmailConnection">测试连接</el-button>
              <el-button @click="resetEmailForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="安全设置" name="security">
          <el-form
            ref="securityFormRef"
            :model="securityForm"
            :rules="securityRules"
            label-width="120px"
          >
            <el-form-item label="登录失败次数" prop="maxLoginAttempts">
              <el-input-number v-model="securityForm.maxLoginAttempts" :min="3" :max="10" />
              <span class="form-tip">超过此次数将锁定账号</span>
            </el-form-item>
            
            <el-form-item label="锁定时间(分钟)" prop="lockoutDuration">
              <el-input-number v-model="securityForm.lockoutDuration" :min="5" :max="60" />
              <span class="form-tip">账号锁定持续时间</span>
            </el-form-item>
            
            <el-form-item label="密码有效期(天)" prop="passwordExpiryDays">
              <el-input-number v-model="securityForm.passwordExpiryDays" :min="30" :max="365" />
              <span class="form-tip">密码过期时间</span>
            </el-form-item>
            
            <el-form-item label="会话超时(分钟)" prop="sessionTimeout">
              <el-input-number v-model="securityForm.sessionTimeout" :min="15" :max="480" />
              <span class="form-tip">用户会话超时时间</span>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSaveSecurity" :loading="securityLoading">
                保存设置
              </el-button>
              <el-button @click="resetSecurityForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const activeTab = ref('basic')

// 基本设置表单
const basicFormRef = ref()
const basicLoading = ref(false)

const basicForm = reactive({
  siteTitle: 'Vue Demo 应用',
  siteDescription: '基于 Vue.js 3.x 和 Spring Boot 3.x 的现代化应用',
  siteLogo: '',
  icpNumber: ''
})

const basicRules = {
  siteTitle: [
    { required: true, message: '请输入网站标题', trigger: 'blur' },
    { min: 3, max: 50, message: '网站标题长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  siteDescription: [
    { max: 200, message: '网站描述不能超过 200 个字符', trigger: 'blur' }
  ]
}

// 邮件设置表单
const emailFormRef = ref()
const emailLoading = ref(false)

const emailForm = reactive({
  smtpHost: '',
  smtpPort: 587,
  fromEmail: '',
  fromName: '',
  username: '',
  password: ''
})

const emailRules = {
  smtpHost: [
    { required: true, message: '请输入SMTP服务器地址', trigger: 'blur' }
  ],
  smtpPort: [
    { required: true, message: '请输入SMTP端口', trigger: 'blur' }
  ],
  fromEmail: [
    { required: true, message: '请输入发件人邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  fromName: [
    { required: true, message: '请输入发件人名称', trigger: 'blur' }
  ]
}

// 安全设置表单
const securityFormRef = ref()
const securityLoading = ref(false)

const securityForm = reactive({
  maxLoginAttempts: 5,
  lockoutDuration: 30,
  passwordExpiryDays: 90,
  sessionTimeout: 120
})

const securityRules = {
  maxLoginAttempts: [
    { required: true, message: '请输入登录失败次数限制', trigger: 'blur' }
  ],
  lockoutDuration: [
    { required: true, message: '请输入锁定时间', trigger: 'blur' }
  ],
  passwordExpiryDays: [
    { required: true, message: '请输入密码有效期', trigger: 'blur' }
  ],
  sessionTimeout: [
    { required: true, message: '请输入会话超时时间', trigger: 'blur' }
  ]
}

// 文件上传处理
const handleLogoSuccess = (response, file) => {
  basicForm.siteLogo = URL.createObjectURL(file.raw)
}

const beforeLogoUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
  }
  return isImage && isLt2M
}

// 保存设置
const handleSaveBasic = async () => {
  if (!basicFormRef.value) return
  
  await basicFormRef.value.validate(async (valid) => {
    if (valid) {
      basicLoading.value = true
      try {
        // 这里调用API保存基本设置
        ElMessage.success('基本设置保存成功')
      } catch (error) {
        ElMessage.error('保存失败，请重试')
      } finally {
        basicLoading.value = false
      }
    }
  })
}

const handleSaveEmail = async () => {
  if (!emailFormRef.value) return
  
  await emailFormRef.value.validate(async (valid) => {
    if (valid) {
      emailLoading.value = true
      try {
        // 这里调用API保存邮件设置
        ElMessage.success('邮件设置保存成功')
      } catch (error) {
        ElMessage.error('保存失败，请重试')
      } finally {
        emailLoading.value = false
      }
    }
  })
}

const handleSaveSecurity = async () => {
  if (!securityFormRef.value) return
  
  await securityFormRef.value.validate(async (valid) => {
    if (valid) {
      securityLoading.value = true
      try {
        // 这里调用API保存安全设置
        ElMessage.success('安全设置保存成功')
      } catch (error) {
        ElMessage.error('保存失败，请重试')
      } finally {
        securityLoading.value = false
      }
    }
  })
}

// 测试邮件连接
const testEmailConnection = async () => {
  if (!emailFormRef.value) return
  
  await emailFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里调用API测试邮件连接
        ElMessage.success('邮件连接测试成功')
      } catch (error) {
        ElMessage.error('邮件连接测试失败')
      }
    }
  })
}

// 重置表单
const resetBasicForm = () => {
  if (basicFormRef.value) {
    basicFormRef.value.resetFields()
  }
}

const resetEmailForm = () => {
  if (emailFormRef.value) {
    emailFormRef.value.resetFields()
  }
}

const resetSecurityForm = () => {
  if (securityFormRef.value) {
    securityFormRef.value.resetFields()
  }
}

// 加载设置
const loadSettings = () => {
  // 这里调用API加载设置数据
  console.log('Loading settings...')
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.settings-container {
  padding: 20px;
}

.settings-card {
  max-width: 800px;
  margin: 0 auto;
}

.form-tip {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}

.logo-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
}

.logo-uploader:hover {
  border-color: #409eff;
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.logo {
  width: 100px;
  height: 100px;
  display: block;
}
</style>