<template>
  <div class="panel page-panel">
    <div class="section-head">
      <h2>我的预约</h2>
      <el-tag>{{ rows.length }} 条记录</el-tag>
    </div>

    <el-table :data="rows" style="width: 100%">
      <el-table-column prop="courseName" label="课程" min-width="150" />
      <el-table-column prop="coachName" label="教练" />
      <el-table-column prop="courseTime" label="课程时间" min-width="160" />
      <el-table-column prop="createTime" label="预约时间" min-width="160" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="row.status === 'reserved' ? 'success' : 'info'">
            {{ row.status === 'reserved' ? '已预约' : '已取消' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button text type="danger" :disabled="row.status !== 'reserved'" @click="cancel(row)">取消预约</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useGymStore } from '@/stores/gym'

const gymStore = useGymStore()
const rows = computed(() => gymStore.myAppointments)

async function cancel(row) {
  await ElMessageBox.confirm(`确认取消 ${row.courseName}？`, '取消预约', { type: 'warning' })
  await gymStore.cancelAppointment(row.id)
  ElMessage.success('已取消预约')
}

onMounted(() => gymStore.loadMemberData())
</script>
