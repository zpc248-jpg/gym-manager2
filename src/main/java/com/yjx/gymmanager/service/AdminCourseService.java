package com.yjx.gymmanager.service;

import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.entity.Course;
import com.yjx.gymmanager.mapper.AppointmentMapper;
import com.yjx.gymmanager.mapper.CourseMapper;
import com.yjx.gymmanager.vo.CourseVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: zhoupc
 * @date: 2026/7/8 10:39
 * @version: 1.0
 * @description:
 */
@Service
public class AdminCourseService {
	@Resource
	private CourseMapper courseMapper;

	@Resource
	private AppointmentMapper appointmentMapper;
	public List<CourseVO> getCourseList() {
		return courseMapper.getCourseList();
	}

	public void addCourse(Course course) {
		checkCourse(course);
		if (course.getBookedCount() == null) {
			course.setBookedCount(0);
		}
		if (course.getStatus() == null) {
			course.setStatus(1);
		}
		courseMapper.addCourse(course);
	}

	private void checkCourse(Course course) {
		if (course.getName() == null || course.getName().isBlank()) {
			throw new BusinessException("请输入课程名称");
		}
		if (course.getCoachId() == null) {
			throw new BusinessException("请选择授课教练");
		}
		if (course.getStartTime() == null || course.getEndTime() == null) {
			throw new BusinessException("请选择课程时间");
		}
		if (!course.getStartTime().isBefore(course.getEndTime())) {
			throw new BusinessException("开始时间必须早于结束时间");
		}
		if (course.getCapacity() == null || course.getCapacity() <= 0) {
			throw new BusinessException("人数上限必须大于0");
		}
		if (course.getBookedCount() != null && course.getBookedCount() < 0) {
			throw new BusinessException("已预约人数不能小于0");
		}
		if (course.getBookedCount() != null && course.getBookedCount() > course.getCapacity()) {
			throw new BusinessException("已预约人数不能超过人数上限");
		}
	}

	public void updateCourse(Long id, Course course) {
		if (id == null){
			throw new BusinessException("课程ID不能为空");
		}
		checkCourse(course);
		courseMapper.updateCourse(id, course);
	}

	public void deleteCourse(Long id) {
		if(id == null || courseMapper.getCourseById(id) == null){
			throw new BusinessException("课程不存在");
		}
		if(!appointmentMapper.selectByCourseId(id).isEmpty()){
			throw new BusinessException("课程已有预约，不能删除");
		}
		courseMapper.deleteCourse(id);
	}
}
