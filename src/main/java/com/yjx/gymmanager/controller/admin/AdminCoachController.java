package com.yjx.gymmanager.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.common.PageResult;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.entity.Coach;
import com.yjx.gymmanager.entity.Course;
import com.yjx.gymmanager.mapper.CourseMapper;
import com.yjx.gymmanager.service.CoachService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coaches")
@RequiredArgsConstructor
public class AdminCoachController {

    private final CoachService coachService;
    private final CourseMapper courseMapper;

    @GetMapping
    public Result<PageResult<Coach>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Coach> pageResult = coachService.getCoachListPage(page, size);
        return Result.ok(pageResult);
    }

    @PostMapping
    public Result<Void> create(@RequestBody Coach coach) {
        coachService.save(coach);
        coachService.clearCoachListCache();  // 新增后清除缓存
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Coach coach) {
        coach.setId(id);
        coachService.updateById(coach);
        coachService.clearCoachListCache();  // 更新后清除缓存
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long courseCount = courseMapper.selectCount(
                new LambdaQueryWrapper<Course>().eq(Course::getCoachId, id)
        );
        if (courseCount > 0) {
            throw new BusinessException("该教练已有课程关联，不能删除");
        }
        coachService.removeById(id);
        coachService.clearCoachListCache();  // 删除后清除缓存
        return Result.ok();
    }
}