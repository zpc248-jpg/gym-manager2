<template>
  <el-dialog v-model="visible" title="AI助手" width="720px" class="ai-dialog">
    <div ref="aiChatBodyRef" class="ai-chat-box">
      <template v-if="aiMessages.length">
        <div
          v-for="message in aiMessages"
          :key="message.id"
          class="ai-message-row"
          :class="`ai-message-row-${message.role}`"
        >
          <div class="ai-message-bubble" :class="`ai-message-bubble-${message.role}`">
            {{ message.content }}
          </div>
        </div>
        <div v-if="aiLoading" class="ai-message-row ai-message-row-assistant">
          <div class="ai-message-bubble ai-message-bubble-assistant">正在思考...</div>
        </div>
      </template>
      <el-empty v-else description="输入问题后开始对话" />
    </div>
    <el-input
      v-model="aiMessage"
      type="textarea"
      :rows="4"
      maxlength="500"
      show-word-limit
      :placeholder="placeholder"
      :disabled="aiLoading"
      @keydown.enter.exact.prevent="sendAiMessage"
    />
    <template #footer>
      <el-button :disabled="aiLoading || !aiMessages.length" @click="clearAiMessages">清空对话</el-button>
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" :loading="aiLoading" :disabled="aiLoading" @click="sendAiMessage">
        发送
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { computed, nextTick, ref } from 'vue'
import { chatWithAi } from '@/api/ai'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
  placeholder: {
    type: String,
    default: '例如：帮我分析今天课程预约情况',
  },
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})
const aiMessage = ref('')
const aiMessages = ref([])
const aiLoading = ref(false)
const aiChatBodyRef = ref(null)

async function sendAiMessage() {
  if (aiLoading.value) {
    return
  }
  const message = aiMessage.value.trim()
  if (!message) {
    ElMessage.warning('请输入问题')
    return
  }
  aiMessages.value.push({
    id: Date.now(),
    role: 'user',
    content: message,
  })
  aiMessage.value = ''
  scrollAiChatToBottom()
  aiLoading.value = true
  try {
    const result = await chatWithAi({
      message,
      messages: aiMessages.value.map((item) => ({
        role: item.role,
        content: item.content,
      })),
    })
    aiMessages.value.push({
      id: Date.now() + 1,
      role: 'assistant',
      content: result?.reply || '没有返回内容',
    })
  } catch (error) {
    aiMessages.value.push({
      id: Date.now() + 1,
      role: 'assistant',
      content: error?.message || 'AI 服务暂时没有响应，请稍后再试',
    })
  } finally {
    aiLoading.value = false
    scrollAiChatToBottom()
  }
}

function clearAiMessages() {
  aiMessages.value = []
  aiMessage.value = ''
}

function scrollAiChatToBottom() {
  nextTick(() => {
    const element = aiChatBodyRef.value
    if (element) {
      element.scrollTop = element.scrollHeight
    }
  })
}
</script>
