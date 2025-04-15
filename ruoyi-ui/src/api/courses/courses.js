import request from '@/utils/request'

// 查询课程信息列表
export function listCourses(query) {
  return request({
    url: '/courses/courses/list',
    method: 'get',
    params: query
  })
}

// 查询课程信息详细
export function getCourses(courseId) {
  return request({
    url: '/courses/courses/' + courseId,
    method: 'get'
  })
}

// 新增课程信息
export function addCourses(data) {
  return request({
    url: '/courses/courses',
    method: 'post',
    data: data
  })
}

// 修改课程信息
export function updateCourses(courseId, data) { // 新增 courseId 参数
  return request({
    url: '/courses/courses/' + courseId, // 将 courseId 放入 URL
    method: 'put',
    data: data // 请求体不再包含 courseId
  })
}

// 删除课程信息
export function delCourses(courseId) {
  return request({
    url: '/courses/courses/' + courseId,
    method: 'delete'
  })
}
