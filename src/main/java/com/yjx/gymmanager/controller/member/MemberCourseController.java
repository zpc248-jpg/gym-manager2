package com.yjx.gymmanager.controller.member;

import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.mapper.CourseMapper;
import com.yjx.gymmanager.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/member/courses")
@RequiredArgsConstructor
public class MemberCourseController {
    private final CourseMapper courseMapper;

    @GetMapping
    public Result<List<CourseVO>> list() {
        List<CourseVO> list = courseMapper.getAvailableCourseList();
        list.forEach(CourseVO::fillTimeStatus);
        return Result.ok(list);
    }
}
