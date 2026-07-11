package com.yjx.gymmanager.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yjx.gymmanager.common.PageResult;
import com.yjx.gymmanager.entity.Coach;
import com.yjx.gymmanager.mapper.CoachMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CoachService extends ServiceImpl<CoachMapper, Coach> {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String CACHE_KEY_COACH_LIST = "coach:list:all";
    private static final long CACHE_EXPIRE_SECONDS = 3600;

    /**
     * 获取全量教练列表（带缓存）
     */
    private List<Coach> getAllCoachesWithCache() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String cached = ops.get(CACHE_KEY_COACH_LIST);
        if (cached != null) {
            try {
                List<Coach> coachList = objectMapper.readValue(
                        cached,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Coach.class)
                );
                // ✅ 明确输出数据来源
                System.out.println("📌 数据来源: Redis 缓存 (命中)");
                return coachList;
            } catch (JsonProcessingException e) {
                redisTemplate.delete(CACHE_KEY_COACH_LIST);
                System.out.println("⚠️ 缓存反序列化失败，已删除，准备查 MySQL");
            }
        }

        // 缓存未命中，查 MySQL
        System.out.println("📌 数据来源: MySQL (缓存未命中)");
        List<Coach> coaches = this.list();
        try {
            String json = objectMapper.writeValueAsString(coaches);
            ops.set(CACHE_KEY_COACH_LIST, json, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            System.out.println("✅ 已写入 Redis 缓存");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return coaches;
    }

    /**
     * 分页获取教练列表（内存分页）
     */
    public PageResult<Coach> getCoachListPage(int page, int size) {
        List<Coach> all = getAllCoachesWithCache();
        long total = all.size();
        int from = (page - 1) * size;
        if (from >= total) {
            return new PageResult<>(total, Collections.emptyList());
        }
        int to = Math.min(from + size, (int) total);
        return new PageResult<>(total, all.subList(from, to));
    }

    /**
     * 清除缓存（增删改后调用）
     */
    public void clearCoachListCache() {
        redisTemplate.delete(CACHE_KEY_COACH_LIST);
    }
}