<template>
  <div class="ai-chat-container" :class="{ 'is-open': isOpen }">
    <!-- 触发按钮 -->
    <div class="chat-trigger" @click="toggleChat" v-if="!isOpen">
      <i class="el-icon-chat-dot-round"></i>
      <span>AI 助手</span>
    </div>

    <!-- 聊天窗口 -->
    <div class="chat-window" v-show="isOpen">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-left">
          <i class="el-icon-chat-dot-round"></i>
          <span>AI 智能助手</span>
        </div>
        <i class="el-icon-close" @click="toggleChat"></i>
      </div>

      <!-- 消息列表 -->
      <div class="chat-messages" ref="messageList">
        <div v-if="messages.length === 0" class="empty-state">
          <i class="el-icon-chat-line-round"></i>
          <p>你好！我是课程达成度分析系统的 AI 助手</p>
          <p class="sub">有什么可以帮助你的吗？</p>
        </div>

        <div
          v-for="(msg, index) in messages"
          :key="index"
          class="message-item"
          :class="msg.role"
        >
          <div class="message-avatar">
            <i :class="msg.role === 'user' ? 'el-icon-user' : 'el-icon-cpu'"></i>
          </div>
          <div class="message-content">
            <div
              v-if="msg.role === 'user'"
              class="message-text"
            >{{ msg.content }}</div>
            <div
              v-else
              class="message-text message-text--markdown"
              v-html="renderMarkdown(msg.content)"
            ></div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>

        <div v-if="isLoading" class="message-item assistant">
          <div class="message-avatar">
            <i class="el-icon-cpu"></i>
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <div class="chat-input">
        <el-input
          v-model="userInput"
          placeholder="输入你的问题..."
          @keyup.enter.native="sendMessage"
          :disabled="isLoading"
        >
          <el-button
            slot="append"
            icon="el-icon-s-promotion"
            @click="sendMessage"
            :loading="isLoading"
          ></el-button>
        </el-input>
      </div>
    </div>
  </div>
</template>

<script>
import { chatWithAi } from '@/api/ai'
import { marked } from 'marked'

export default {
  name: 'AiChat',
  data() {
    return {
      isOpen: false,
      userInput: '',
      messages: [],
      isLoading: false
    };
  },
  methods: {
    /** AI 返回支持 Markdown；用户侧仍为纯文本，避免 XSS */
    renderMarkdown(text) {
      if (text == null || text === '') {
        return ''
      }
      const s = typeof text === 'string' ? text : String(text)
      return marked(s, { breaks: true, gfm: true })
    },
    toggleChat() {
      this.isOpen = !this.isOpen;
    },
    
    async sendMessage() {
      if (!this.userInput.trim() || this.isLoading) return;

      const userMessage = {
        role: 'user',
        content: this.userInput,
        time: this.getCurrentTime()
      };

      this.messages.push(userMessage);
      const question = this.userInput;
      this.userInput = '';
      this.isLoading = true;

      this.$nextTick(() => {
        this.scrollToBottom();
      });

      try {
        const response = await chatWithAi(question);
        
        const raw = response.data != null ? response.data : response.msg
        const aiMessage = {
          role: 'assistant',
          content: typeof raw === 'string' ? raw : String(raw == null ? '' : raw),
          time: this.getCurrentTime()
        };
        
        this.messages.push(aiMessage);
      } catch (error) {
        this.$message.error('AI 服务异常，请稍后重试');
        
        const errorMessage = {
          role: 'assistant',
          content: '抱歉，我暂时无法回答。请稍后再试。',
          time: this.getCurrentTime()
        };
        
        this.messages.push(errorMessage);
      } finally {
        this.isLoading = false;
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      }
    },
    
    scrollToBottom() {
      const container = this.$refs.messageList;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    },
    
    getCurrentTime() {
      const now = new Date();
      return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
    }
  }
};
</script>

<style scoped lang="scss">
.ai-chat-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 2000;
}

.chat-trigger {
  width: 120px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
  }

  i {
    font-size: 20px;
  }
}

.chat-window {
  width: 380px;
  height: 600px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-left {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 16px;
    font-weight: 500;

    i {
      font-size: 20px;
    }
  }

  .el-icon-close {
    font-size: 18px;
    cursor: pointer;
    transition: transform 0.2s;

    &:hover {
      transform: scale(1.1);
    }
  }
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f5f7fa;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #dcdfe6;
    border-radius: 3px;
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;

  i {
    font-size: 48px;
    margin-bottom: 16px;
    color: #c0c4cc;
  }

  p {
    margin: 8px 0;
    font-size: 14px;
  }

  .sub {
    font-size: 12px;
    color: #c0c4cc;
  }
}

.message-item {
  display: flex;
  margin-bottom: 20px;
  animation: fadeIn 0.3s;

  &.user {
    flex-direction: row-reverse;

    .message-content {
      align-items: flex-end;
    }

    .message-text {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
    }
  }

  .message-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #e4e7ed;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    margin: 0 12px;

    i {
      font-size: 18px;
      color: #909399;
    }
  }

  &.user .message-avatar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

    i {
      color: white;
    }
  }

  .message-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .message-text {
    background: white;
    padding: 12px 16px;
    border-radius: 12px;
    font-size: 14px;
    line-height: 1.6;
    color: #303133;
    word-wrap: break-word;
    max-width: 80%;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .message-text--markdown {
    min-width: 0;

    ::v-deep p {
      margin: 8px 0;

      &:first-child {
        margin-top: 0;
      }

      &:last-child {
        margin-bottom: 0;
      }
    }

    ::v-deep strong {
      font-weight: 600;
      color: #667eea;
    }

    ::v-deep em {
      font-style: italic;
      color: #909399;
    }

    ::v-deep ul,
    ::v-deep ol {
      margin: 8px 0;
      padding-left: 20px;
    }

    ::v-deep li {
      margin: 4px 0;
    }

    ::v-deep code {
      background: #f5f7fa;
      padding: 2px 6px;
      border-radius: 4px;
      font-family: 'Consolas', 'Monaco', monospace;
      font-size: 13px;
      color: #e83e8c;
    }

    ::v-deep pre {
      background: #f5f7fa;
      padding: 12px;
      border-radius: 8px;
      overflow-x: auto;
      margin: 8px 0;
    }

    ::v-deep pre code {
      background: transparent;
      padding: 0;
      color: #303133;
    }

    ::v-deep blockquote {
      border-left: 3px solid #667eea;
      padding-left: 12px;
      margin: 8px 0;
      color: #606266;
    }

    ::v-deep h1,
    ::v-deep h2,
    ::v-deep h3,
    ::v-deep h4,
    ::v-deep h5,
    ::v-deep h6 {
      margin: 12px 0 8px;
      font-weight: 600;
      color: #303133;
    }

    ::v-deep h1 {
      font-size: 20px;
    }
    ::v-deep h2 {
      font-size: 18px;
    }
    ::v-deep h3 {
      font-size: 16px;
    }
    ::v-deep h4 {
      font-size: 15px;
    }
  }

  .message-time {
    font-size: 12px;
    color: #c0c4cc;
    padding: 0 4px;
  }
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: white;
  border-radius: 12px;

  span {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #909399;
    animation: typing 1.4s infinite;

    &:nth-child(2) {
      animation-delay: 0.2s;
    }

    &:nth-child(3) {
      animation-delay: 0.4s;
    }
  }
}

.chat-input {
  padding: 16px 20px;
  background: white;
  border-top: 1px solid #ebeef5;

  ::v-deep .el-input-group__append {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;

    .el-button {
      background: transparent;
      color: white;
      border: none;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}
</style>