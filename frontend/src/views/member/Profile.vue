<template>
  <div class="dashboard-page">
    <section class="member-hero">
      <div>
        <el-tag type="primary">{{ profile.cardType }}</el-tag>
        <h1>{{ profile.name }}，欢迎回来</h1>
        <p>会员卡有效期至 {{ profile.expireTime }}。</p>
      </div>
      <el-button type="primary" :icon="Edit" @click="resetForm">编辑资料</el-button>
    </section>

    <section class="content-grid">
      <div class="panel">
        <div class="section-head">
          <h2>个人资料</h2>
          <el-tag :type="profile.status === 1 ? 'success' : 'info'">{{ profile.status === 1 ? '正常' : '禁用' }}</el-tag>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ profile.name }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ profile.phone }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ profile.gender }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ profile.age }}</el-descriptions-item>
          <el-descriptions-item label="会员卡">{{ profile.cardType }}</el-descriptions-item>
          <el-descriptions-item label="到期时间">{{ profile.expireTime }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="panel">
        <div class="section-head">
          <h2>修改资料</h2>
          <el-button type="primary" plain @click="saveProfile">保存</el-button>
        </div>
        <el-form :model="form" label-width="108px">
          <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="年龄"><el-input-number v-model="form.age" :min="1" :max="100" /></el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="form.gender">
              <el-radio-button label="男" />
              <el-radio-button label="女" />
            </el-radio-group>
          </el-form-item>
          <el-divider />
          <el-form-item label="旧密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
          <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password /></el-form-item>
          <el-form-item>
            <el-button @click="savePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { useGymStore } from '@/stores/gym'

const gymStore = useGymStore()
const profile = computed(() => gymStore.currentMember || {})
const form = reactive({ phone: '', age: 20, gender: '男' })
const passwordForm = reactive({ oldPassword: '', newPassword: '' })

function resetForm() {
  Object.assign(form, {
    phone: profile.value.phone,
    age: profile.value.age,
    gender: profile.value.gender,
  })
}

async function saveProfile() {
  await gymStore.updateProfile({ ...form })
  ElMessage.success('资料已保存')
}

async function savePassword() {
  await gymStore.updatePassword({ ...passwordForm })
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  ElMessage.success('密码已修改')
}

resetForm()
onMounted(async () => {
  await gymStore.loadMemberData()
  resetForm()
})
</script>
