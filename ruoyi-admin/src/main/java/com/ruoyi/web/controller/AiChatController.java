package com.ruoyi.web.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiChatController extends BaseController
{
    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody ChatRequest request)
    {
        try
        {
            String response = aiChatService.chat(request.getMessage());
            return AjaxResult.success("操作成功", response);
        }
        catch (Exception e)
        {
            logger.error("AI 聊天异常", e);
            return AjaxResult.error("AI 服务异常：" + e.getMessage());
        }
    }

    public static class ChatRequest
    {
        private String message;

        public String getMessage()
        {
            return message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }
    }
}