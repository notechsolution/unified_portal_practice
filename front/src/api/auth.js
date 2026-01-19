import request from '@/utils/request'

export const authApi = {
  // 登录
  login(data) {
    return request.post('/auth/login', data)
  },
  
  // 注册
  register(data) {
    return request.post('/auth/register', data)
  },
  
  // 获取当前用户信息
  getCurrentUser() {
    return request.get('/auth/current')
  },
  
  // 登出
  logout() {
    return request.post('/auth/logout')
  }
}
