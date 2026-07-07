<template>
  <div class="panel page-panel">
    <div class="toolbar">
      <el-input v-model="keyword" clearable placeholder="按用户名查询" :prefix-icon="Search" />
      <el-button type="primary" :icon="Plus" @click="openDialog()">新增用户</el-button>
    </div>

    <el-table :data="filteredRows" style="width: 100%">
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="role" label="角色" width="110">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'success' : 'primary'">
            {{ row.role === 'admin' ? '管理员' : '会员' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="relatedId" label="关联会员ID" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button text type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="520px">
      <el-form :model="form" label-width="96px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password :placeholder="form.id ? '留空则不修改' : '默认 123456'" />
        </el-form-item>
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio-button label="admin">管理员</el-radio-button>
            <el-radio-button label="member">会员</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="关联会员ID">
          <el-input-number v-model="form.relatedId" :min="1" :disabled="form.role !== 'member'" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="正常" inactive-text="禁用" />
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
  if (!value) return gymStore.users
  return gymStore.users.filter((item) => item.username.includes(value))
})

function openDialog(row) {
  Object.keys(form).forEach((key) => delete form[key])
  Object.assign(
    form,
    row ? { ...row, password: '' } : { username: '', password: '123456', role: 'member', relatedId: undefined, status: 1 },
  )
  dialogVisible.value = true
}

async function save() {
  const payload = { ...form }
  if (payload.role !== 'member') payload.relatedId = null
  await gymStore.saveUser(payload)
  dialogVisible.value = false
  ElMessage.success('保存成功')
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除用户 ${row.username}？`, '删除确认', { type: 'warning' })
  await gymStore.removeUser(row.id)
  ElMessage.success('删除成功')
}

onMounted(() => gymStore.loadAdminData())
</script>
