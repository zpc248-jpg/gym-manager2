package com.yjx.gymmanager.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yjx.gymmanager.entity.Appointment;
import com.yjx.gymmanager.entity.Course;
import com.yjx.gymmanager.entity.SysUser;
import com.yjx.gymmanager.mapper.AppointmentMapper;
import com.yjx.gymmanager.mapper.CourseMapper;
import com.yjx.gymmanager.mapper.MemberMapper;
import com.yjx.gymmanager.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDeleteService {
    private final MemberMapper memberMapper;
    private final CourseMapper courseMapper;
    private final AppointmentMapper appointmentMapper;
    private final SysUserMapper sysUserMapper;

    @Transactional
    public void deleteMember(Long memberId) {
        List<Appointment> appointments = appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getMemberId, memberId)
                .eq(Appointment::getStatus, "reserved"));
        for (Appointment appointment : appointments) {
            Course course = courseMapper.selectById(appointment.getCourseId());
            if (course != null && course.getBookedCount() != null && course.getBookedCount() > 0) {
                course.setBookedCount(course.getBookedCount() - 1);
                courseMapper.updateById(course);
            }
        }
        appointmentMapper.delete(new LambdaQueryWrapper<Appointment>().eq(Appointment::getMemberId, memberId));
        sysUserMapper.delete(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "member")
                .eq(SysUser::getRelatedId, memberId));
        memberMapper.deleteById(memberId);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        appointmentMapper.delete(new LambdaQueryWrapper<Appointment>().eq(Appointment::getCourseId, courseId));
        courseMapper.deleteById(courseId);
    }
}
