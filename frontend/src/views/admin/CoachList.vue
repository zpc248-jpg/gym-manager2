<template>
  <div class="panel page-panel">
    <div class="toolbar">
      <el-input v-model="keyword" clearable placeholder="按教练姓名查询" :prefix-icon="Search" />
      <el-button type="primary" :icon="Plus" @click="openDialog()">新增教练</el-button>
    </div>

    <el-table :data="filteredRows" style="width: 100%">
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="specialty" label="擅长项目" />
      <el-table-column prop="entryDate" label="入职日期" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '在职' : '离职' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button text type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 - 左对齐 -->
    <div style="margin-top: 16px; display: flex; justify-content: flex-start">
      <el-pagination
          background
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePageChange"
          @current-change="handlePageChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑教练' : '新增教练'" width="520px">
      <el-form :model="form" label-width="88px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="擅长项目"><el-input v-model="form.specialty" /></el-form-item>
        <el-form-item label="入职日期"><el-date-picker v-model="form.entryDate" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="在职" inactive-text="离职" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { useGymStore } from '@/stores/gym'

const gymStore = useGymStore()

// 搜索关键词
const keyword = ref('')
const dialogVisible = ref(false)
const form = reactive({})

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 当前页数据（来自 store）
const coaches = computed(() => gymStore.coaches)

// 前端过滤（基于当前页数据）
const filteredRows = computed(() => {
  const value = keyword.value.trim()
  if (!value) return coaches.value
  return coaches.value.filter((item) => item.name.includes(value))
})

// 加载分页数据
async function loadCoaches() {
  const res = await gymStore.fetchCoaches(currentPage.value, pageSize.value)
  total.value = res.total
  // gymStore.coaches 已在 fetchCoaches 中更新
}

// 分页变化事件
function handlePageChange() {
  loadCoaches()
}

// 打开对话框（新增/编辑）
function openDialog(row) {
  Object.assign(form, row || { name: '', phone: '', specialty: '', entryDate: '', status: 1 })
  if (!row) delete form.id
  dialogVisible.value = true
}

// 保存（新增或编辑）
async function save() {
  await gymStore.saveCoach({ ...form })
  dialogVisible.value = false
  ElMessage.success('保存成功')
  // 保存后重新加载当前页
  loadCoaches()
}

// 删除
async function remove(row) {
  await ElMessageBox.confirm(`确认删除教练 ${row.name}？`, '删除确认', { type: 'warning' })
  await gymStore.removeCoach(row.id)
  ElMessage.success('删除成功')
  // 删除后重新加载当前页
  loadCoaches()
}

// 组件挂载时加载第一页
onMounted(() => {
  loadCoaches()
})
</script>