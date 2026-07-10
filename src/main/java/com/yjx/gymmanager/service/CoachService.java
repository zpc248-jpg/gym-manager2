package com.yjx.gymmanager.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yjx.gymmanager.entity.Coach;
import com.yjx.gymmanager.mapper.CoachMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

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
     * 获取教练列表（带 Redis 缓存）
     */
    public List<Coach> getCoachListWithCache() {
        System.out.println("================== CoachService 获取教练列表 ==================");

        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String cached = ops.get(CACHE_KEY_COACH_LIST);

        System.out.println("从 Redis 读取到的缓存值: " + (cached != null ? "存在 (长度=" + cached.length() + ")" : "null"));

        if (cached != null) {
            System.out.println("✅✅✅ 缓存命中！尝试从 Redis 反序列化数据...");
            try {
                List<Coach> coachList = objectMapper.readValue(
                        cached,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Coach.class)
                );
                System.out.println("✅ 反序列化成功，返回缓存数据，教练数量: " + coachList.size());
                return coachList;
            } catch (JsonProcessingException e) {
                System.out.println("❌ 反序列化失败，删除错误缓存，走 MySQL 查询。异常信息: " + e.getMessage());
                redisTemplate.delete(CACHE_KEY_COACH_LIST);
                // 继续往下执行，走 MySQL
            }
        } else {
            System.out.println("❌❌❌ 缓存未命中，将查询 MySQL");
        }

        // 缓存未命中或反序列化失败，查 MySQL
        System.out.println("⏳ 正在查询 MySQL 数据库...");
        List<Coach> coachList = this.list();  // 等价于 coachMapper.selectList(null)
        System.out.println("从 MySQL 查询到 " + coachList.size() + " 条教练记录");

        // 写入 Redis
        try {
            String json = objectMapper.writeValueAsString(coachList);
            ops.set(CACHE_KEY_COACH_LIST, json, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            System.out.println("✅ 已将教练列表写入 Redis，过期时间 " + CACHE_EXPIRE_SECONDS + " 秒");
        } catch (JsonProcessingException e) {
            System.err.println("❌ 序列化教练列表到 Redis 失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("返回教练列表，数据来源: MySQL");
        return coachList;
    }

    /**
     * 清除教练列表缓存（增删改操作后调用）
     */
    public void clearCoachListCache() {
        redisTemplate.delete(CACHE_KEY_COACH_LIST);
        System.out.println("🔄 已清除教练列表缓存");
    }
}