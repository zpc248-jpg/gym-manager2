package com.yjx.gymmanager.controller.admin;

import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.entity.Course;
import com.yjx.gymmanager.service.AdminCourseService;
import com.yjx.gymmanager.vo.CourseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: zhoupc
 * @date: 2026/7/8 10:23
 * @version: 1.0
 * @description:
 */
@RequestMapping("/api/admin/courses")
@RestController
public class AdminCourseController {
	@Resource
	private AdminCourseService adminCourseService;

	@GetMapping
	public Result<List<CourseVO>> getCourseList() {
		return Result.ok(adminCourseService.getCourseList());
	}

	@PostMapping
	public Result<Void> addCourse(@RequestBody Course course) {
		adminCourseService.addCourse(course);
		return Result.ok();
	}

	@PutMapping("/{id}")
	public Result<Void> updateCourse(@PathVariable("id") Long id, @RequestBody Course course) {
		adminCourseService.updateCourse(id, course);
		return Result.ok();
	}

	@DeleteMapping("/{id}")
	public Result<Void> deleteCourse(@PathVariable("id") Long id) {
		adminCourseService.deleteCourse(id);
		return Result.ok();
	}
}
