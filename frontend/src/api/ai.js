import request from '@/utils/request'

export function chatWithAi(data) {
  return request.post('/ai/chat', data, {
    timeout: 60000,
    silent: true,
  })
}
