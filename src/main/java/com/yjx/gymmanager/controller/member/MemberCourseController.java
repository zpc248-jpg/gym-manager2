package com.yjx.gymmanager.controller.member;

import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.service.MemberCourseService;
import com.yjx.gymmanager.vo.CourseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/member/courses")
public class MemberCourseController {
    @Resource
  private MemberCourseService memberCourseService;

    @GetMapping
    public Result<List<CourseVO>> list() {
      List<CourseVO> list = memberCourseService.getAvailableCourseList();
        return Result.ok(list);
    }
}
