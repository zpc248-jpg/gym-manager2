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
const keyword = ref('')
const dialogVisible = ref(false)
const form = reactive({})

const filteredRows = computed(() => {
  const value = keyword.value.trim()
  if (!value) return gymStore.coaches
  return gymStore.coaches.filter((item) => item.name.includes(value))
})

function openDialog(row) {
  Object.assign(form, row || { name: '', phone: '', specialty: '', entryDate: '', status: 1 })
  if (!row) delete form.id
  dialogVisible.value = true
}

async function save() {
  await gymStore.saveCoach({ ...form })
  dialogVisible.value = false
  ElMessage.success('保存成功')
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除教练 ${row.name}？`, '删除确认', { type: 'warning' })
  await gymStore.removeCoach(row.id)
  ElMessage.success('删除成功')
}

onMounted(() => gymStore.loadAdminData())
</script>
