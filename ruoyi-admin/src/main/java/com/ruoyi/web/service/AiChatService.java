package com.ruoyi.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiChatService
{
    @Value("${ai.deepseek.api-key:}")
    private String apiKey;

    @Value("${ai.deepseek.api-url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.deepseek.model:deepseek-chat}")
    private String model;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public String chat(String userMessage) throws Exception
    {
        if (!StringUtils.hasText(userMessage) || !StringUtils.hasText(userMessage.trim()))
        {
            throw new IllegalArgumentException("消息内容不能为空");
        }
        if (!StringUtils.hasText(apiKey))
        {
            throw new IllegalStateException("未配置 AI 密钥，请在环境变量设置 DEEPSEEK_API_KEY 或 application 配置中设置 ai.deepseek.api-key");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是课程达成度分析系统的AI助手。你的任务是帮助用户了解系统功能、解答使用问题。系统主要功能包括：1) 考核方式配置（平时、上机、期末）2) 课程目标管理 3) 达成度报告生成。请用简洁、专业的语言回答问题。");
        messages.add(systemMessage);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage.trim());
        messages.add(userMsg);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        String bodyJson = objectMapper.writeValueAsString(requestBody);
        HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);

        try
        {
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );
            String responseBody = response.getBody();
            if (responseBody == null)
            {
                throw new RestClientException("AI 响应为空");
            }
            Map<String, Object> root = objectMapper.readValue(responseBody, Map.class);
            if (root.containsKey("error"))
            {
                Object err = root.get("error");
                return "AI 接口返回错误：" + (err == null ? "unknown" : err.toString());
            }
            List<Map<String, Object>> choices = (List<Map<String, Object>>) root.get("choices");
            if (choices == null || choices.isEmpty())
            {
                throw new RestClientException("AI 响应格式异常：无 choices");
            }
            Map<String, Object> first = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) first.get("message");
            if (message == null)
            {
                throw new RestClientException("AI 响应格式异常：无 message");
            }
            Object content = message.get("content");
            return content == null ? "" : String.valueOf(content);
        }
        catch (RestClientException e)
        {
            throw new Exception("调用 AI 失败：" + e.getMessage(), e);
        }
    }
}