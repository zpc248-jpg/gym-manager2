package com.yjx.gymmanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.zhipu")
public class AiProperties {
    private String apiKey;
    private String baseUrl = "https://open.bigmodel.cn/api/paas/v4";
    private String model = "glm-5.2";
}
