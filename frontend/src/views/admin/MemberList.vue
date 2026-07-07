<template>
  <div class="panel page-panel">
    <div class="toolbar">
      <el-input v-model="keyword" clearable placeholder="按姓名或手机号查询" :prefix-icon="Search" />
      <el-button type="primary" :icon="Plus" @click="openDialog()">新增会员</el-button>
    </div>

    <el-table :data="filteredRows" style="width: 100%">
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="gender" label="性别" width="90" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="cardType" label="会员卡" />
      <el-table-column prop="expireTime" label="到期时间" />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑会员' : '新增会员'" width="560px">
      <el-form :model="form" label-width="104px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio-button label="男" />
            <el-radio-button label="女" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="1" :max="100" /></el-form-item>
        <el-form-item label="会员卡"><el-input v-model="form.cardType" /></el-form-item>
        <el-form-item label="到期时间"><el-date-picker v-model="form.expireTime" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="正常" inactive-text="禁用" />
        </el-form-item>
        <template v-if="!form.id">
          <el-form-item label="登录账号"><el-input v-model="form.username" placeholder="会员登录用户名" /></el-form-item>
          <el-form-item label="登录密码">
            <el-input v-model="form.password" type="password" show-password placeholder="默认 123456" />
          </el-form-item>
        </template>
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
  if (!value) return gymStore.members
  return gymStore.members.filter((item) => item.name.includes(value) || item.phone.includes(value))
})

function openDialog(row) {
  Object.keys(form).forEach((key) => delete form[key])
  Object.assign(
    form,
    row || {
      name: '',
      phone: '',
      gender: '男',
      age: 20,
      cardType: '月卡',
      expireTime: '',
      status: 1,
      username: '',
      password: '123456',
    },
  )
  dialogVisible.value = true
}

async function save() {
  await gymStore.saveMember({ ...form })
  dialogVisible.value = false
  ElMessage.success('保存成功')
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除会员 ${row.name}？`, '删除确认', { type: 'warning' })
  await gymStore.removeMember(row.id)
  ElMessage.success('删除成功')
}

onMounted(() => gymStore.loadAdminData())
</script>
