import request from '@/utils/request'

export const adminApi = {
  dashboard: () => request.get('/admin/dashboard'),
  members: () => request.get('/admin/members'),
  createMember: (data) => request.post('/admin/members', data),
  updateMember: (id, data) => request.put(`/admin/members/${id}`, data),
  deleteMember: (id) => request.delete(`/admin/members/${id}`),
  // ★ 修改这里：支持分页参数
  coaches: () => request.get('/admin/coaches'),
  createCoach: (data) => request.post('/admin/coaches', data),
  updateCoach: (id, data) => request.put(`/admin/coaches/${id}`, data),
  deleteCoach: (id) => request.delete(`/admin/coaches/${id}`),
  courses: () => request.get('/admin/courses'),
  createCourse: (data) => request.post('/admin/courses', data),
  updateCourse: (id, data) => request.put(`/admin/courses/${id}`, data),
  deleteCourse: (id) => request.delete(`/admin/courses/${id}`),
  appointments: () => request.get('/admin/appointments'),
  cancelAppointment: (id) => request.put(`/admin/appointments/${id}/cancel`),
}

export const memberApi = {
  profile: () => request.get('/member/profile'),
  updateProfile: (data) => request.put('/member/profile', data),
  updatePassword: (data) => request.put('/member/profile/password', data),
  courses: () => request.get('/member/courses'),
  appointments: () => request.get('/member/appointments'),
  reserve: (courseId) => request.post('/member/appointments', { courseId }),
  cancelAppointment: (id) => request.put(`/member/appointments/${id}/cancel`),
}