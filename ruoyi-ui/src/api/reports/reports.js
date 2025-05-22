import request from '@/utils/request'

export function listReports() {
  return request({
    url: '/reports/reports/list',
    method: 'get'
  })
}