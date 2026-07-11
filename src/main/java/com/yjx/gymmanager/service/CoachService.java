package com.yjx.gymmanager.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yjx.gymmanager.common.PageResult;
import com.yjx.gymmanager.entity.Coach;
import com.yjx.gymmanager.mapper.CoachMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachService extends ServiceImpl<CoachMapper, Coach> {

    /**
     * 分页获取教练列表（直接缓存分页结果）
     * 用 page 和 size 作为缓存 key，不同分页参数会生成不同缓存
     */
    @Cacheable(value = "coach", key = "'page:' + #page + ':' + #size")
    public PageResult<Coach> getCoachListPage(int page, int size) {
        List<Coach> all = this.list();
        long total = all.size();
        int from = (page - 1) * size;
        if (from >= total) {
            return new PageResult<>(total, Collections.emptyList());
        }
        int to = Math.min(from + size, (int) total);
        List<Coach> pageList = new ArrayList<>(all.subList(from, to));
        return new PageResult<>(total, pageList);
    }

    /**
     * 清除所有教练缓存（增删改后调用）
     * 因为现在缓存 key 包含页码，所以用 allEntries = true 清除整个缓存区域
     */
    @CacheEvict(value = "coach", allEntries = true)
    public void clearCoachListCache() {
        // 方法体为空，由注解驱动清除缓存
    }
}