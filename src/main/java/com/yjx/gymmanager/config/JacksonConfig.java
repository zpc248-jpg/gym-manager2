package com.yjx.gymmanager.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Configuration
public class JacksonConfig {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    // 纯日期格式
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
            module.addDeserializer(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());
            // ========== 新增 LocalDate 序列化与反序列化 ==========
            module.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
            module.addDeserializer(LocalDate.class, new FlexibleLocalDateTimeDeserializer.FlexibleLocalDateDeserializer());
            builder.modules(module);
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }

    private static class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        private static final List<DateTimeFormatter> FORMATTERS = List.of(
                DATE_TIME_FORMATTER,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );

        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String value = parser.getValueAsString();
            if (value == null || value.isBlank()) {
                return null;
            }
            String trimmedValue = value.trim();
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDateTime.parse(trimmedValue, formatter);
                } catch (DateTimeParseException ignored) {
                    // Try the next supported frontend format.
                }
            }
            throw context.weirdStringException(trimmedValue, LocalDateTime.class, "时间格式应为 yyyy-MM-dd HH:mm");
        }
        // 新增 LocalDate 专用反序列化器
        private static class FlexibleLocalDateDeserializer extends JsonDeserializer<LocalDate> {
            private static final List<DateTimeFormatter> FORMATTERS = List.of(
                    DATE_FORMATTER,
                    DateTimeFormatter.ISO_LOCAL_DATE
            );

            @Override
            public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                String value = parser.getValueAsString();
                if (value == null || value.isBlank()) {
                    return null;
                }
                String trimmedValue = value.trim();
                for (DateTimeFormatter formatter : FORMATTERS) {
                    try {
                        return LocalDate.parse(trimmedValue, formatter);
                    } catch (DateTimeParseException ignored) {
                    }
                }
                throw context.weirdStringException(trimmedValue, LocalDate.class, "日期格式应为 yyyy-MM-dd");
            }
        }
    }
}
