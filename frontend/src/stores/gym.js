import { defineStore } from 'pinia'
import { adminApi, memberApi } from '@/api/gym'

const now = '2026-07-07 10:00'

const demoMembers = [
  { id: 1, name: '张三', phone: '13800000000', gender: '男', age: 22, cardType: '月卡', expireTime: '2026-12-31', status: 1 },
  { id: 2, name: '李娜', phone: '13900000001', gender: '女', age: 28, cardType: '季卡', expireTime: '2026-10-15', status: 1 },
  { id: 3, name: '赵明', phone: '13700000002', gender: '男', age: 35, cardType: '年卡', expireTime: '2027-03-20', status: 0 },
]

const demoCoaches = [
  { id: 1, name: '李教练', phone: '13600000001', specialty: '燃脂塑形', entryDate: '2024-03-01', status: 1 },
  { id: 2, name: '王教练', phone: '13600000002', specialty: '普拉提', entryDate: '2023-09-12', status: 1 },
  { id: 3, name: '陈教练', phone: '13600000003', specialty: '力量训练', entryDate: '2025-01-08', status: 1 },
]

const demoCourses = [
  { id: 1, name: '燃脂团课', type: '有氧', coachId: 1, startTime: '2026-07-07 09:00', endTime: '2026-07-07 10:00', capacity: 20, bookedCount: 16, status: 1 },
  { id: 2, name: '普拉提塑形', type: '塑形', coachId: 2, startTime: '2026-07-07 14:30', endTime: '2026-07-07 15:30', capacity: 16, bookedCount: 12, status: 1 },
  { id: 3, name: '力量训练入门', type: '力量', coachId: 3, startTime: '2026-07-07 18:30', endTime: '2026-07-07 19:30', capacity: 20, bookedCount: 20, status: 1 },
  { id: 4, name: '瑜伽拉伸', type: '恢复', coachId: 2, startTime: '2026-07-08 19:00', endTime: '2026-07-08 20:00', capacity: 18, bookedCount: 8, status: 0 },
]

const demoAppointments = [
  { id: 1, memberId: 1, courseId: 1, status: 'reserved', createTime: now },
  { id: 2, memberId: 2, courseId: 2, status: 'reserved', createTime: '2026-07-07 09:18' },
  { id: 3, memberId: 3, courseId: 3, status: 'canceled', createTime: '2026-07-06 21:05' },
]

function pad(value) {
  return String(value).padStart(2, '0')
}

function formatDateTime(date) {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

function normalizeDateTime(value) {
  if (!value) return value
  if (value instanceof Date) return formatDateTime(value)
  if (typeof value === 'string') {
    const trimmedValue = value.trim()
    if (/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$/.test(trimmedValue)) return trimmedValue
    const isoMatch = trimmedValue.match(/^(\d{4}-\d{2}-\d{2})T(\d{2}:\d{2})/)
    if (isoMatch) return `${isoMatch[1]} ${isoMatch[2]}`
    const date = new Date(trimmedValue)
    if (!Number.isNaN(date.getTime())) return formatDateTime(date)
  }
  return value
}

export const useGymStore = defineStore('gym', {
  state: () => ({
    members: [...demoMembers],
    coaches: [...demoCoaches],
    courses: [...demoCourses],
    appointments: [...demoAppointments],
    currentMemberId: 1,
  }),
  getters: {
    dashboard: (state) => ({
      memberCount: state.members.length,
      coachCount: state.coaches.filter((item) => item.status === 1).length,
      courseCount: state.courses.length,
      appointmentCount: state.appointments.filter((item) => item.status === 'reserved').length,
    }),
    courseRows: (state) =>
      state.courses.map((course) => ({
        ...course,
        coachName: course.coachName || state.coaches.find((coach) => coach.id === course.coachId)?.name || '-',
      })),
    appointmentRows: (state) =>
      state.appointments.map((appointment) => {
        const member = state.members.find((item) => item.id === appointment.memberId)
        const course = state.courses.find((item) => item.id === appointment.courseId)
        const coach = state.coaches.find((item) => item.id === course?.coachId)
        return {
          ...appointment,
          memberName: appointment.memberName || member?.name || '-',
          courseName: appointment.courseName || course?.name || '-',
          courseTime: appointment.courseTime || course?.startTime || '-',
          coachName: appointment.coachName || coach?.name || '-',
        }
      }),
    currentMember: (state) => state.members.find((item) => item.id === state.currentMemberId),
    myAppointments() {
      return this.appointmentRows.filter((item) => item.memberId === this.currentMemberId)
    },
  },
  actions: {
    async loadAdminData() {
        const [members, coaches, courses, appointments] = await Promise.all([
          adminApi.members(),
          adminApi.coaches(),
          adminApi.courses(),
          adminApi.appointments(),
        ])
        this.members = members || []
      this.coaches = coaches || []
      this.courses = courses || []
      this.appointments = appointments || []
    },
    async loadMemberData() {
      const [profile, courses, appointments] = await Promise.all([
        memberApi.profile(),
        memberApi.courses(),
        memberApi.appointments(),
      ])
      if (profile) {
        this.currentMemberId = profile.id
        const index = this.members.findIndex((item) => item.id === profile.id)
        if (index >= 0) this.members[index] = profile
        else this.members = [profile, ...this.members]
      }
      this.courses = courses || []
      this.appointments = appointments || []
    },
    async saveMember(payload) {
      const { username, password, ...member } = payload
      const request = { member, username, password }
      if (payload.id) await adminApi.updateMember(payload.id, request)
      else await adminApi.createMember(request)
      await this.loadAdminData()
    },
    async removeMember(id) {
      await adminApi.deleteMember(id)
      await this.loadAdminData()
    },
    async saveCoach(payload) {
      if (payload.id) await adminApi.updateCoach(payload.id, payload)
      else await adminApi.createCoach(payload)
      await this.loadAdminData()
    },
    async removeCoach(id) {
      await adminApi.deleteCoach(id)
      await this.loadAdminData()
    },
    async saveCourse(payload) {
      const course = {
        ...payload,
        startTime: normalizeDateTime(payload.startTime),
        endTime: normalizeDateTime(payload.endTime),
      }
      if (payload.id) await adminApi.updateCourse(payload.id, course)
      else await adminApi.createCourse(course)
      await this.loadAdminData()
    },
    async removeCourse(id) {
      await adminApi.deleteCourse(id)
      await this.loadAdminData()
    },
    async reserveCourse(courseId) {
      await memberApi.reserve(courseId)
      await this.loadMemberData()
      return true
    },
    async cancelAppointment(id, admin = false) {
      if (admin) {
        await adminApi.cancelAppointment(id)
        await this.loadAdminData()
      } else {
        await memberApi.cancelAppointment(id)
        await this.loadMemberData()
      }
      return true
    },
    async updateProfile(payload) {
      await memberApi.updateProfile(payload)
      await this.loadMemberData()
    },
    async updatePassword(payload) {
      await memberApi.updatePassword(payload)
    },
  },
})
