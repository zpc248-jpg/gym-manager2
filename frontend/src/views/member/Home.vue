<template>
  <div class="dashboard-page">
    <section class="member-hero">
      <div>
        <el-tag type="primary">普通会员</el-tag>
        <h1>{{ userStore.username }}，欢迎回来</h1>
        <p>会员卡有效期至 2026-12-31，今天还有 3 节课程可预约。</p>
      </div>
      <el-button type="primary" size="large">查看可预约课程</el-button>
    </section>

    <section class="content-grid">
      <div class="panel">
        <div class="section-head">
          <h2>推荐课程</h2>
          <el-tag>可预约</el-tag>
        </div>
        <div class="course-list">
          <div v-for="course in courses" :key="course.name" class="course-item">
            <div>
              <strong>{{ course.name }}</strong>
              <span>{{ course.time }} · {{ course.coach }}</span>
            </div>
            <el-button type="primary" plain>预约</el-button>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="section-head">
          <h2>我的预约</h2>
          <el-button text type="primary">查看全部</el-button>
        </div>
        <el-timeline>
          <el-timeline-item timestamp="今天 18:30" type="success">力量训练入门</el-timeline-item>
          <el-timeline-item timestamp="明天 09:00" type="primary">燃脂团课</el-timeline-item>
          <el-timeline-item timestamp="周五 14:30">普拉提塑形</el-timeline-item>
        </el-timeline>
      </div>
    </section>

    <section class="metric-grid">
      <div class="metric-card">
        <div class="metric-icon blue">
          <el-icon><Calendar /></el-icon>
        </div>
        <div>
          <span>本月预约</span>
          <strong>8</strong>
          <small>已完成 5 次</small>
        </div>
      </div>
      <div class="metric-card">
        <div class="metric-icon green">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div>
          <span>剩余有效天数</span>
          <strong>177</strong>
          <small>月卡会员</small>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { Calendar, CircleCheck } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const courses = [
  { name: '力量训练入门', time: '今天 18:30', coach: '陈教练' },
  { name: '燃脂团课', time: '明天 09:00', coach: '李教练' },
  { name: '普拉提塑形', time: '周五 14:30', coach: '王教练' },
]
</script>
