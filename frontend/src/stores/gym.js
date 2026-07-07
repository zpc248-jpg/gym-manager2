import { defineStore } from 'pinia'
import { adminApi, memberApi } from '@/api/gym'

const now = '2026-07-07 10:00'

export const useGymStore = defineStore('gym', {
  state: () => ({
    members: [
      {
        id: 1,
        name: '张三',
        phone: '13800000000',
        gender: '男',
        age: 22,
        cardType: '月卡',
        expireTime: '2026-12-31',
        status: 1,
      },
      {
        id: 2,
        name: '李娜',
        phone: '13900000001',
        gender: '女',
        age: 28,
        cardType: '季卡',
        expireTime: '2026-10-15',
        status: 1,
      },
      {
        id: 3,
        name: '赵明',
        phone: '13700000002',
        gender: '男',
        age: 35,
        cardType: '年卡',
        expireTime: '2027-03-20',
        status: 0,
      },
    ],
    coaches: [
      { id: 1, name: '李教练', phone: '13600000001', specialty: '燃脂塑形', entryDate: '2024-03-01', status: 1 },
      { id: 2, name: '王教练', phone: '13600000002', specialty: '普拉提', entryDate: '2023-09-12', status: 1 },
      { id: 3, name: '陈教练', phone: '13600000003', specialty: '力量训练', entryDate: '2025-01-08', status: 1 },
    ],
    courses: [
      {
        id: 1,
        name: '燃脂团课',
        type: '有氧',
        coachId: 1,
        startTime: '2026-07-07 09:00',
        endTime: '2026-07-07 10:00',
        capacity: 20,
        bookedCount: 16,
        status: 1,
      },
      {
        id: 2,
        name: '普拉提塑形',
        type: '塑形',
        coachId: 2,
        startTime: '2026-07-07 14:30',
        endTime: '2026-07-07 15:30',
        capacity: 16,
        bookedCount: 12,
        status: 1,
      },
      {
        id: 3,
        name: '力量训练入门',
        type: '力量',
        coachId: 3,
        startTime: '2026-07-07 18:30',
        endTime: '2026-07-07 19:30',
        capacity: 20,
        bookedCount: 20,
        status: 1,
      },
      {
        id: 4,
        name: '瑜伽拉伸',
        type: '恢复',
        coachId: 2,
        startTime: '2026-07-08 19:00',
        endTime: '2026-07-08 20:00',
        capacity: 18,
        bookedCount: 8,
        status: 0,
      },
    ],
    appointments: [
      { id: 1, memberId: 1, courseId: 1, status: 'reserved', createTime: now },
      { id: 2, memberId: 2, courseId: 2, status: 'reserved', createTime: '2026-07-07 09:18' },
      { id: 3, memberId: 3, courseId: 3, status: 'canceled', createTime: '2026-07-06 21:05' },
    ],
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
        coachName: state.coaches.find((coach) => coach.id === course.coachId)?.name || '-',
      })),
    appointmentRows: (state) =>
      state.appointments.map((appointment) => {
        const member = state.members.find((item) => item.id === appointment.memberId)
        const course = state.courses.find((item) => item.id === appointment.courseId)
        const coach = state.coaches.find((item) => item.id === course?.coachId)
        return {
          ...appointment,
          memberName: member?.name || '-',
          courseName: course?.name || '-',
          courseTime: course?.startTime || '-',
          coachName: coach?.name || '-',
        }
      }),
    currentMember: (state) => state.members.find((item) => item.id === state.currentMemberId),
    myAppointments() {
      return this.appointmentRows.filter((item) => item.memberId === this.currentMemberId)
    },
  },
  actions: {
    async loadAdminData() {
      try {
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
      } catch {
        // Keep local demo data when the backend or database is not ready.
      }
    },
    async loadMemberData() {
      try {
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
      } catch {
        // Keep local demo data when the backend or database is not ready.
      }
    },
    nextId(list) {
      return Math.max(0, ...list.map((item) => Number(item.id))) + 1
    },
    async saveMember(payload) {
      try {
        if (payload.id) await adminApi.updateMember(payload.id, payload)
        else await adminApi.createMember(payload)
        await this.loadAdminData()
        return
      } catch {
        // Fall back to local demo mutation.
      }
      if (payload.id) {
        const index = this.members.findIndex((item) => item.id === payload.id)
        this.members[index] = { ...this.members[index], ...payload }
      } else {
        this.members.unshift({ ...payload, id: this.nextId(this.members), status: 1 })
      }
    },
    async removeMember(id) {
      try {
        await adminApi.deleteMember(id)
        await this.loadAdminData()
        return
      } catch {
        // Fall back to local demo mutation.
      }
      this.members = this.members.filter((item) => item.id !== id)
      this.appointments = this.appointments.filter((item) => item.memberId !== id)
    },
    async saveCoach(payload) {
      try {
        if (payload.id) await adminApi.updateCoach(payload.id, payload)
        else await adminApi.createCoach(payload)
        await this.loadAdminData()
        return
      } catch {
        // Fall back to local demo mutation.
      }
      if (payload.id) {
        const index = this.coaches.findIndex((item) => item.id === payload.id)
        this.coaches[index] = { ...this.coaches[index], ...payload }
      } else {
        this.coaches.unshift({ ...payload, id: this.nextId(this.coaches), status: 1 })
      }
    },
    async removeCoach(id) {
      try {
        await adminApi.deleteCoach(id)
        await this.loadAdminData()
        return
      } catch {
        // Fall back to local demo mutation.
      }
      this.coaches = this.coaches.filter((item) => item.id !== id)
    },
    async saveCourse(payload) {
      try {
        if (payload.id) await adminApi.updateCourse(payload.id, payload)
        else await adminApi.createCourse(payload)
        await this.loadAdminData()
        return
      } catch {
        // Fall back to local demo mutation.
      }
      if (payload.id) {
        const index = this.courses.findIndex((item) => item.id === payload.id)
        this.courses[index] = { ...this.courses[index], ...payload }
      } else {
        this.courses.unshift({ ...payload, id: this.nextId(this.courses), bookedCount: 0, status: 1 })
      }
    },
    async removeCourse(id) {
      try {
        await adminApi.deleteCourse(id)
        await this.loadAdminData()
        return
      } catch {
        // Fall back to local demo mutation.
      }
      this.courses = this.courses.filter((item) => item.id !== id)
      this.appointments = this.appointments.filter((item) => item.courseId !== id)
    },
    async reserveCourse(courseId) {
      try {
        await memberApi.reserve(courseId)
        await this.loadMemberData()
        return true
      } catch {
        // Fall back to local demo mutation.
      }
      const course = this.courses.find((item) => item.id === courseId)
      if (!course || course.status !== 1 || course.bookedCount >= course.capacity) return false
      const existing = this.appointments.find(
        (item) => item.courseId === courseId && item.memberId === this.currentMemberId && item.status === 'reserved',
      )
      if (existing) return false
      this.appointments.unshift({
        id: this.nextId(this.appointments),
        memberId: this.currentMemberId,
        courseId,
        status: 'reserved',
        createTime: now,
      })
      course.bookedCount += 1
      return true
    },
    async cancelAppointment(id, admin = false) {
      try {
        if (admin) {
          await adminApi.cancelAppointment(id)
          await this.loadAdminData()
        } else {
          await memberApi.cancelAppointment(id)
          await this.loadMemberData()
        }
        return true
      } catch {
        // Fall back to local demo mutation.
      }
      const appointment = this.appointments.find((item) => item.id === id)
      if (!appointment || appointment.status !== 'reserved') return false
      appointment.status = 'canceled'
      const course = this.courses.find((item) => item.id === appointment.courseId)
      if (course && course.bookedCount > 0) course.bookedCount -= 1
      return true
    },
    async updateProfile(payload) {
      try {
        await memberApi.updateProfile(payload)
        await this.loadMemberData()
        return
      } catch {
        // Fall back to local demo mutation.
      }
      const index = this.members.findIndex((item) => item.id === this.currentMemberId)
      this.members[index] = { ...this.members[index], ...payload }
    },
  },
})
