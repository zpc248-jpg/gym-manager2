import { defineStore } from 'pinia'
import { adminApi, memberApi } from '@/api/gym'

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
    members: [],
    coaches: [],          // 存储当前页教练数据
    courses: [],
    appointments: [],
    currentMemberId: null,
  }),
  getters: {
    dashboard: (state) => ({
      memberCount: state.members.length,
      coachCount: state.coaches.filter((item) => item.status === 1).length, // 注意：coaches 现为当前页，统计可能不准，如需要全量统计可调整，此处保留原逻辑
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
    // 新增：分页加载教练列表
    async fetchCoaches(page = 1, size = 10) {
      const res = await adminApi.coaches(page, size)   // 需要 API 支持分页参数
      this.coaches = res.list || []
      return res   // 返回完整响应 { total, list }
    },

    async loadAdminData() {
      // 不再加载教练数据，仅加载其他模块
      const [members, courses, appointments] = await Promise.all([
        adminApi.members(),
        adminApi.courses(),
        adminApi.appointments(),
      ])
      this.members = members || []
      this.courses = courses || []
      this.appointments = appointments || []
      // coaches 由 fetchCoaches 单独加载，此处不再赋值
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
      // 只执行数据库操作，不在此处刷新列表
      if (payload.id) await adminApi.updateCoach(payload.id, payload)
      else await adminApi.createCoach(payload)
      // 移除 this.loadAdminData()，由组件控制刷新
    },
    async removeCoach(id) {
      await adminApi.deleteCoach(id)
      // 移除 this.loadAdminData()
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