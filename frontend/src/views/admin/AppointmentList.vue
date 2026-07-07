<template>
  <div class="panel page-panel">
    <div class="toolbar toolbar-wide">
      <el-input v-model="memberKeyword" clearable placeholder="按会员名查询" :prefix-icon="Search" />
      <el-input v-model="courseKeyword" clearable placeholder="按课程名查询" :prefix-icon="Search" />
    </div>

    <el-table :data="filteredRows" style="width: 100%">
      <el-table-column prop="memberName" label="会员" />
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
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useGymStore } from '@/stores/gym'

const gymStore = useGymStore()
const memberKeyword = ref('')
const courseKeyword = ref('')

const filteredRows = computed(() =>
  gymStore.appointmentRows.filter(
    (item) =>
      item.memberName.includes(memberKeyword.value.trim()) && item.courseName.includes(courseKeyword.value.trim()),
  ),
)

async function cancel(row) {
  await ElMessageBox.confirm(`确认取消 ${row.memberName} 的 ${row.courseName}？`, '取消预约', { type: 'warning' })
  await gymStore.cancelAppointment(row.id, true)
  ElMessage.success('已取消预约')
}

onMounted(() => gymStore.loadAdminData())
</script>
