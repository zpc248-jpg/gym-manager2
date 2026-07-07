package com.yjx.gymmanager.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.entity.Coach;
import com.yjx.gymmanager.entity.Course;
import com.yjx.gymmanager.mapper.CoachMapper;
import com.yjx.gymmanager.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coaches")
@RequiredArgsConstructor
public class AdminCoachController {
    private final CoachMapper coachMapper;
    private final CourseMapper courseMapper;

    @GetMapping
    public Result<List<Coach>> list() {
        return Result.ok(coachMapper.selectList(null));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Coach coach) {
        coachMapper.insert(coach);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Coach coach) {
        coach.setId(id);
        coachMapper.updateById(coach);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long courseCount = courseMapper.selectCount(new LambdaQueryWrapper<Course>().eq(Course::getCoachId, id));
        if (courseCount > 0) {
            throw new BusinessException("该教练已有课程关联，不能删除");
        }
        coachMapper.deleteById(id);
        return Result.ok();
    }
}
