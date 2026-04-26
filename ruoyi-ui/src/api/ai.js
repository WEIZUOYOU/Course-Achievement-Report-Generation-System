import request from '@/utils/request'

export function chatWithAi(message) {
  return request({
    url: '/ai/chat',
    method: 'post',
    data: { message },
    headers: { repeatSubmit: false }
  })
}
