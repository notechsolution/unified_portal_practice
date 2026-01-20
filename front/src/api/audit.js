import request from '@/utils/request'

export function getUserAuditLogs(params) {
  return request({
    url: '/api/audit/my-logs',
    method: 'get',
    params
  })
}