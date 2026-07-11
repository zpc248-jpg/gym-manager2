package com.yjx.gymmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yjx.gymmanager.common.PageResult;
import com.yjx.gymmanager.entity.Coach;
import com.yjx.gymmanager.mapper.CoachMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Service
@RequiredArgsConstructor
public class CoachService extends ServiceImpl<CoachMapper, Coach> {

    @Cacheable(value = "coach", key = "'list'")
    public List<Coach> getAllCoachesWithCache() {
        return this.list();
    }

    public PageResult<Coach> pageCoach(Long pageNum, Long pageSize, String keyword) {
        long current = pageNum == null || pageNum < 1 ? 1 : pageNum;
        long size = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        String searchText = keyword == null ? "" : keyword.trim();
        Page<Coach> page = this.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<Coach>()
                        .like(!searchText.isBlank(), Coach::getName, searchText)
                        .orderByDesc(Coach::getId)
        );
        return PageResult.of(page);
    }

    @CacheEvict(value = "coach", allEntries = true)
    public void clearCoachListCache() {
    }
}
