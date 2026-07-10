package com.yjx.gymmanager.service;

import com.yjx.gymmanager.common.BusinessException;
import com.yjx.gymmanager.entity.Appointment;
import com.yjx.gymmanager.entity.Course;
import com.yjx.gymmanager.mapper.AppointmentMapper;
import com.yjx.gymmanager.mapper.CourseMapper;
import com.yjx.gymmanager.mapper.MemberAppointmentMapper;
import com.yjx.gymmanager.mapper.SysUserMapper;
import com.yjx.gymmanager.vo.AppointmentVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberAppointmentService {
    @Resource
    private MemberAppointmentMapper memberAppointmentMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private AppointmentMapper appointmentMapper;
    public List<AppointmentVO> selectAppointments(long userId) {
        if (userId == 0){
             throw new RuntimeException("该会员不存在");
        }
        if (sysUserMapper.selectUserByUserId(userId) == null) {
            throw new RuntimeException("该会员不存在");
        }
        //通过userId查询sys_user表，获取relatedId，即会员ID
        long memberId = sysUserMapper.selectUserByUserId(userId).getRelatedId();
        if (memberAppointmentMapper.selectCourseByMemberId(memberId) == null){
            throw new RuntimeException("该会员暂无预约课程");
        }
         return memberAppointmentMapper.selectCourseByMemberId(memberId);
    }
    @Transactional
    public void reserve(long userId, Long courseId) {
        Appointment appointment = new Appointment();
        long memberId = sysUserMapper.selectUserByUserId(userId).getRelatedId();
        if (courseMapper.getCourseById(courseId) ==  null){
            throw new BusinessException("课程不存在");
        }
        Course course = courseMapper.getCourseById(courseId);
        if (course.getStatus() == 0){
            throw new BusinessException("课程已停课");
        }
        if (course.getBookedCount() >= course.getCapacity()){
            throw new BusinessException("课程已满");
        }
        Appointment appoint = memberAppointmentMapper.courseCount(courseId, memberId);
        if (appoint != null) {
            if ("reserved".equals(appoint.getStatus())) {
                throw new BusinessException("该会员已预约过该课程");
            } else if ("canceled".equals(appoint.getStatus())) {
                // 已取消：更新旧预约状态
                memberAppointmentMapper.updateAppointment(memberId,courseId);
                course.setBookedCount(course.getBookedCount() + 1);
                courseMapper.updateCourse(courseId, course);
                return;
            }
        }
// 完全无预约记录，执行新增逻辑
        course.setBookedCount(course.getBookedCount() + 1);
        courseMapper.updateCourse(courseId, course);
        appointment.setMemberId(memberId);
        appointment.setCourseId(courseId);
        appointment.setStatus("reserved");
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        if (memberAppointmentMapper.insert(appointment) < 1){
            throw new BusinessException("预约失败");
        }

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
