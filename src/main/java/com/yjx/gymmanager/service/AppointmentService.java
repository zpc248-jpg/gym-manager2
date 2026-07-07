package com.yjx.gymmanager.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.entity.Appointment;
import com.yjx.gymmanager.entity.Course;
import com.yjx.gymmanager.mapper.AppointmentMapper;
import com.yjx.gymmanager.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentMapper appointmentMapper;
    private final CourseMapper courseMapper;

    @Transactional
    public void reserve(Long memberId, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        if (course.getStatus() == null || course.getStatus() != 1) {
            throw new BusinessException("课程不可预约");
        }
        if (course.getBookedCount() >= course.getCapacity()) {
            throw new BusinessException("课程人数已满");
        }
        Appointment existing = appointmentMapper.selectOne(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getMemberId, memberId)
                .eq(Appointment::getCourseId, courseId));
        if (existing != null && "reserved".equals(existing.getStatus())) {
            throw new BusinessException("不能重复预约同一课程");
        }
        if (existing != null) {
            existing.setStatus("reserved");
            appointmentMapper.updateById(existing);
        } else {
            Appointment appointment = new Appointment();
            appointment.setMemberId(memberId);
            appointment.setCourseId(courseId);
            appointment.setStatus("reserved");
            appointmentMapper.insert(appointment);
        }
        course.setBookedCount(course.getBookedCount() + 1);
        courseMapper.updateById(course);
    }

    @Transactional
    public void cancel(Long appointmentId, Long memberId, boolean admin) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        if (!admin && !appointment.getMemberId().equals(memberId)) {
            throw new BusinessException("不能取消别人的预约");
        }
        if (!"reserved".equals(appointment.getStatus())) {
            throw new BusinessException("该预约已取消");
        }
        appointment.setStatus("canceled");
        appointmentMapper.updateById(appointment);

        Course course = courseMapper.selectById(appointment.getCourseId());
        if (course != null && course.getBookedCount() != null && course.getBookedCount() > 0) {
            course.setBookedCount(course.getBookedCount() - 1);
            courseMapper.updateById(course);
        }
    }
}
