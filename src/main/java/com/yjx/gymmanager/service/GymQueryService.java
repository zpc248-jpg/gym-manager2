package com.yjx.gymmanager.service;

import com.yjx.gymmanager.entity.Appointment;
import com.yjx.gymmanager.entity.Coach;
import com.yjx.gymmanager.entity.Course;
import com.yjx.gymmanager.entity.Member;
import com.yjx.gymmanager.mapper.CoachMapper;
import com.yjx.gymmanager.mapper.CourseMapper;
import com.yjx.gymmanager.mapper.MemberMapper;
import com.yjx.gymmanager.vo.AppointmentVO;
import com.yjx.gymmanager.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GymQueryService {
    private final MemberMapper memberMapper;
    private final CoachMapper coachMapper;
    private final CourseMapper courseMapper;

    public List<CourseVO> toCourseVO(List<Course> courses) {
        Map<Long, Coach> coachMap = coachMapper.selectList(null).stream()
                .collect(Collectors.toMap(Coach::getId, Function.identity()));
        return courses.stream()
                .map(course -> CourseVO.from(course, coachMap.get(course.getCoachId()) == null ? "-" : coachMap.get(course.getCoachId()).getName()))
                .toList();
    }

    public List<AppointmentVO> toAppointmentVO(List<Appointment> appointments) {
        Map<Long, Member> memberMap = memberMapper.selectList(null).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));
        Map<Long, Course> courseMap = courseMapper.selectList(null).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));
        Map<Long, Coach> coachMap = coachMapper.selectList(null).stream()
                .collect(Collectors.toMap(Coach::getId, Function.identity()));
        return appointments.stream().map(appointment -> {
            AppointmentVO vo = AppointmentVO.from(appointment);
            Member member = memberMap.get(appointment.getMemberId());
            Course course = courseMap.get(appointment.getCourseId());
            Coach coach = course == null ? null : coachMap.get(course.getCoachId());
            vo.setMemberName(member == null ? "-" : member.getName());
            vo.setCourseName(course == null ? "-" : course.getName());
            vo.setCourseTime(course == null ? null : course.getStartTime());
            vo.setCoachName(Objects.isNull(coach) ? "-" : coach.getName());
            return vo;
        }).toList();
    }
}
