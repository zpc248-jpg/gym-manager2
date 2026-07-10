package com.yjx.gymmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.common.CurrentUser;
import com.yjx.gymmanager.common.UserContext;
import com.yjx.gymmanager.config.AiProperties;
import com.yjx.gymmanager.dto.AiChatMessage;
import com.yjx.gymmanager.dto.AiChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {
    private static final String SYSTEM_PROMPT = """
            你是健身房管理系统后台助手。
            你可以进行正常的中文日常聊天，也可以回答健身、课程、会员、预约和系统使用相关问题。
            日常聊天要自然、简洁、有礼貌，不要编造你不知道的个人信息。
            业务问题要回答具体、可执行。
            如果系统提供了真实业务数据，必须以真实业务数据为唯一依据，不能编造数据。
            """;

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private final AiBusinessContextService aiBusinessContextService;
    private final RestClient restClient = RestClient.builder().build();

    public String chat(AiChatRequest request) {
        CurrentUser currentUser = UserContext.get();
        String latestQuestion = findLatestQuestion(request);
        String directReply = aiBusinessContextService.directReply(latestQuestion, currentUser);
        if (!directReply.isBlank()) {
            return directReply;
        }

        List<Map<String, String>> messages = buildMessages(request);
        if (messages.size() <= 1) {
            throw new BusinessException("请输入问题");
        }
        if (aiProperties.getApiKey() == null || aiProperties.getApiKey().isBlank()) {
            throw new BusinessException("请先配置智谱 API Key");
        }
        if (aiProperties.getBaseUrl() == null || aiProperties.getBaseUrl().isBlank()) {
            throw new BusinessException("请先配置 AI 服务地址");
        }
        if (aiProperties.getModel() == null || aiProperties.getModel().isBlank()) {
            throw new BusinessException("请先配置 AI 模型");
        }

        Map<String, Object> requestBody = new java.util.LinkedHashMap<>();
        requestBody.put("model", aiProperties.getModel());
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2048);

        try {
            Map<?, ?> responseBody = restClient.post()
                    .uri(buildChatCompletionsUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + aiProperties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);
            return parseReply(responseBody);
        } catch (RestClientResponseException exception) {
            throw new BusinessException("AI 服务调用失败：" + parseErrorMessage(exception));
        } catch (RestClientException exception) {
            throw new BusinessException("AI 服务调用失败，请稍后重试");
        }
    }

    private List<Map<String, String>> buildMessages(AiChatRequest request) {
        List<Map<String, String>> messages = new java.util.ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        if (request == null) {
            return messages;
        }

        String latestQuestion = findLatestQuestion(request);
        CurrentUser currentUser = UserContext.get();
        String businessContext = aiBusinessContextService.buildContext(latestQuestion, currentUser);
        if (!businessContext.isBlank()) {
            messages.add(Map.of("role", "system", "content", businessContext));
        }

        List<AiChatMessage> requestMessages = request.getMessages();
        if (requestMessages != null && !requestMessages.isEmpty()) {
            int start = Math.max(0, requestMessages.size() - 10);
            for (int i = start; i < requestMessages.size(); i++) {
                AiChatMessage message = requestMessages.get(i);
                if (message == null || message.getContent() == null || message.getContent().isBlank()) {
                    continue;
                }
                String role = "assistant".equals(message.getRole()) ? "assistant" : "user";
                messages.add(Map.of("role", role, "content", message.getContent().trim()));
            }
            return messages;
        }

        if (request.getMessage() != null && !request.getMessage().isBlank()) {
            messages.add(Map.of("role", "user", "content", request.getMessage().trim()));
        }
        return messages;
    }

    private String findLatestQuestion(AiChatRequest request) {
        if (request.getMessages() != null && !request.getMessages().isEmpty()) {
            for (int i = request.getMessages().size() - 1; i >= 0; i--) {
                AiChatMessage message = request.getMessages().get(i);
                if (message != null
                        && "user".equals(message.getRole())
                        && message.getContent() != null
                        && !message.getContent().isBlank()) {
                    return message.getContent();
                }
            }
        }
        return request.getMessage();
    }

    private String buildChatCompletionsUrl() {
        String baseUrl = aiProperties.getBaseUrl().trim();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/chat/completions";
    }

    private String parseReply(Map<?, ?> responseBody) {
        if (responseBody == null) {
            throw new BusinessException("AI 服务没有返回内容");
        }
        JsonNode root = objectMapper.valueToTree(responseBody);
        JsonNode content = root.path("choices").path(0).path("message").path("content");
        if (content.isTextual() && !content.asText().isBlank()) {
            return content.asText();
        }
        JsonNode finishReason = root.path("choices").path(0).path("finish_reason");
        if (finishReason.isTextual() && "length".equals(finishReason.asText())) {
            throw new BusinessException("AI 输出长度不够，请稍后重试或缩短问题");
        }
        throw new BusinessException("AI 服务返回格式无法解析");
    }

    private String parseErrorMessage(RestClientResponseException exception) {
        String responseBody = exception.getResponseBodyAsString();
        if (responseBody == null || responseBody.isBlank()) {
            return "HTTP " + exception.getStatusCode().value();
        }
        JsonNode root;
        try {
            root = objectMapper.readTree(responseBody);
        } catch (Exception ignored) {
            return responseBody;
        }
        JsonNode errorMessage = root.path("error").path("message");
        if (errorMessage.isTextual() && !errorMessage.asText().isBlank()) {
            return errorMessage.asText();
        }
        JsonNode message = root.path("message");
        if (message.isTextual() && !message.asText().isBlank()) {
            return message.asText();
        }
        JsonNode msg = root.path("msg");
        if (msg.isTextual() && !msg.asText().isBlank()) {
            return msg.asText();
        }
        return responseBody;
    }
}
