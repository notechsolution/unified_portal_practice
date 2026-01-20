import request from '@/utils/request'

export function getUserCredentials() {
  return request({
    url: '/credentials',
    method: 'get'
  })
}

export function createCredential(data) {
  return request({
    url: '/credentials',
    method: 'post',
    data
  })
}

export function updateCredential(id, data) {
  return request({
    url: `/credentials/${id}`,
    method: 'put',
    data
  })
}

export function deleteCredential(id) {
  return request({
    url: `/credentials/${id}`,
    method: 'delete'
  })
}

export function performAutoLogin(data) {
  return request({
    url: '/credentials/auto-login',
    method: 'post',
    data
  })
}

export function testCredential(id) {
  return request({
    url: `/credentials/${id}/test`,
    method: 'post'
  })
}