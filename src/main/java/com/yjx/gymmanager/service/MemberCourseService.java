package com.yjx.gymmanager.service;

import com.yjx.gymmanager.mapper.CourseMapper;
import com.yjx.gymmanager.vo.CourseVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberCourseService {
	@Resource
	private CourseMapper courseMapper;

	public List<CourseVO> getAvailableCourseList() {
		return courseMapper.getAvailableCourseList();
	}
}
