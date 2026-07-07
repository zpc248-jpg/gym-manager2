package com.yjx.gymmanager.controller.admin;

import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.entity.Course;
import com.yjx.gymmanager.mapper.CourseMapper;
import com.yjx.gymmanager.service.AdminDeleteService;
import com.yjx.gymmanager.service.GymQueryService;
import com.yjx.gymmanager.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {
    private final CourseMapper courseMapper;
    private final GymQueryService gymQueryService;
    private final AdminDeleteService adminDeleteService;

    @GetMapping
    public Result<List<CourseVO>> list() {
        return Result.ok(gymQueryService.toCourseVO(courseMapper.selectList(null)));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Course course) {
        validateCourseCapacity(course);
        courseMapper.insert(course);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Course course) {
        validateCourseCapacity(course);
        course.setId(id);
        courseMapper.updateById(course);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminDeleteService.deleteCourse(id);
        return Result.ok();
    }

    private void validateCourseCapacity(Course course) {
        if (course.getCapacity() != null && course.getBookedCount() != null && course.getBookedCount() > course.getCapacity()) {
            throw new BusinessException("已预约人数不能超过人数上限");
        }
    }
}
