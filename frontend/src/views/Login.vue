<template>
  <main class="login-page">
    <section class="login-visual">
      <div class="login-hero">
        <el-tag effect="dark" type="success">Gym Manager</el-tag>
        <h1>健身房管理系统</h1>
        <p>轻量化管理会员、教练、课程与预约，支持管理员和会员分角色进入系统。</p>
      </div>
      <div class="login-preview">
        <div class="preview-header">
          <span>今日运营</span>
          <strong>18:30 热门课程</strong>
        </div>
        <div class="preview-grid">
          <div>
            <strong>128</strong>
            <span>活跃会员</span>
          </div>
          <div>
            <strong>12</strong>
            <span>在职教练</span>
          </div>
          <div>
            <strong>36</strong>
            <span>本周预约</span>
          </div>
        </div>
      </div>
    </section>

    <section class="login-panel">
      <div class="panel-title">
        <h2>登录系统</h2>
        <p>使用开发文档中的测试账号快速进入</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            placeholder="密码"
            type="password"
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">
          登录
        </el-button>
      </el-form>

      <div class="quick-login">
        <el-button @click="fillAccount('admin')">管理员 admin / 123456</el-button>
        <el-button @click="fillAccount('member')">会员 zhangsan / 123456</el-button>
      </div>
      <p class="demo-tip">后端登录接口未就绪时，上面两个账号会自动走本地演示登录。</p>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: '123456',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const demoUsers = {
  admin: { token: 'demo-admin-token', userId: 1, username: 'admin', role: 'admin' },
  zhangsan: { token: 'demo-member-token', userId: 2, username: 'zhangsan', role: 'member' },
}

function fillAccount(type) {
  if (type === 'admin') {
    form.username = 'admin'
  } else {
    form.username = 'zhangsan'
  }
  form.password = '123456'
}

function goHome(role) {
  router.replace(role === 'admin' ? '/admin/dashboard' : '/member/profile')
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const user = await login({ username: form.username, password: form.password })
    userStore.setUser(user)
    ElMessage.success('登录成功')
    goHome(user.role)
  } catch (error) {
    const demoUser = demoUsers[form.username]
    if (demoUser && form.password === '123456') {
      userStore.setUser(demoUser)
      ElMessage.success('已进入本地演示模式')
      goHome(demoUser.role)
    }
  } finally {
    loading.value = false
  }
}
</script>
