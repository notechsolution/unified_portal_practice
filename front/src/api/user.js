import request from '@/utils/request'

export const userApi = {
  // 获取用户列表
  getUserList(params) {
    return request.get('/user/list', { params })
  },
  
  // 获取用户详情
  getUserById(id) {
    return request.get(`/user/${id}`)
  },
  
  // 更新用户信息
  updateUser(id, data) {
    return request.put(`/user/${id}`, data)
  },
  
  // 删除用户
  deleteUser(id) {
    return request.delete(`/user/${id}`)
  }
}
