package com.yjx.gymmanager.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yjx.gymmanager.common.CurrentUser;
import com.yjx.gymmanager.entity.Appointment;
import com.yjx.gymmanager.mapper.AppointmentMapper;
import com.yjx.gymmanager.mapper.CourseMapper;
import com.yjx.gymmanager.vo.AppointmentVO;
import com.yjx.gymmanager.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiBusinessContextService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CourseMapper courseMapper;
    private final AppointmentMapper appointmentMapper;
    private final GymQueryService gymQueryService;

    public String directReply(String question, CurrentUser currentUser) {
        if (currentUser != null && "member".equals(currentUser.getRole()) && needMyAppointmentReply(question)) {
            return buildMyAppointmentReply(currentUser.getRelatedId());
        }
        if (needEndedCourseReply(question)) {
            return buildCourseListReply("已结束课程如下：", getEndedCourses());
        }
        if (needAllCourseReply(question)) {
            return buildCourseListReply("系统全部课程如下：", getAllCourses());
        }
        if (needAvailableCourseReply(question)) {
            return buildAvailableCourseReply();
        }
        return "";
    }

    public String buildContext(String question, CurrentUser currentUser) {
        if (currentUser == null || !"admin".equals(currentUser.getRole()) || !needTodayCourseAppointmentContext(question)) {
            return "";
        }
        return buildTodayCourseAppointmentContext();
    }

    private boolean needTodayCourseAppointmentContext(String question) {
        if (question == null || question.isBlank()) {
            return false;
        }
        boolean today = containsAny(question, "今天", "今日");
        boolean courseOrAppointment = containsAny(question, "课程", "预约");
        boolean courseAppointment = question.contains("课程预约");
        return (today && courseOrAppointment) || courseAppointment;
    }

    private boolean needAvailableCourseReply(String question) {
        if (question == null || question.isBlank()) {
            return false;
        }
        boolean askAvailable = containsAny(question, "哪些", "有什么", "可以", "可预约", "能预约");
        boolean courseOrAppointment = containsAny(question, "课程", "预约");
        return askAvailable && courseOrAppointment;
    }

    private boolean needEndedCourseReply(String question) {
        if (question == null || question.isBlank()) {
            return false;
        }
        return question.contains("课程") && containsAny(question, "已结束", "结束", "过期");
    }

    private boolean needAllCourseReply(String question) {
        if (question == null || question.isBlank()) {
            return false;
        }
        return question.contains("课程") && containsAny(question, "全部", "所有", "总共", "一共", "几门", "四门");
    }

    private boolean needMyAppointmentReply(String question) {
        if (question == null || question.isBlank()) {
            return false;
        }
        boolean mine = containsAny(question, "我", "我的");
        boolean appointment = containsAny(question, "预约", "报名");
        return mine && appointment;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private List<CourseVO> getAllCourses() {
        List<CourseVO> courses = courseMapper.getCourseList();
        courses.forEach(CourseVO::fillTimeStatus);
        return courses;
    }

    private List<CourseVO> getEndedCourses() {
        return getAllCourses().stream()
                .filter(course -> "已结束".equals(course.getTimeStatus()))
                .toList();
    }

    private String buildCourseListReply(String title, List<CourseVO> courses) {
        if (courses.isEmpty()) {
            return "没有查询到相关课程。";
        }

        StringBuilder builder = new StringBuilder(title).append("\n");
        for (CourseVO course : courses) {
            int capacity = course.getCapacity() == null ? 0 : course.getCapacity();
            int bookedCount = course.getBookedCount() == null ? 0 : course.getBookedCount();
            builder.append("- ").append(course.getName())
                    .append("，类型：").append(course.getType())
                    .append("，教练：").append(course.getCoachName())
                    .append("，时间：").append(formatDateTime(course.getStartTime()))
                    .append(" - ").append(formatDateTime(course.getEndTime()))
                    .append("，预约：").append(bookedCount)
                    .append("/")
                    .append(capacity)
                    .append("，状态：").append(course.getTimeStatus())
                    .append("\n");
        }
        return builder.toString();
    }

    private String buildAvailableCourseReply() {
        List<CourseVO> courses = courseMapper.getAvailableCourseList();
        if (courses.isEmpty()) {
            return "目前没有可预约课程。";
        }

        StringBuilder builder = new StringBuilder("目前可以预约的课程有：\n");
        for (CourseVO course : courses) {
            int capacity = course.getCapacity() == null ? 0 : course.getCapacity();
            int bookedCount = course.getBookedCount() == null ? 0 : course.getBookedCount();
            int remaining = Math.max(capacity - bookedCount, 0);
            builder.append("- ").append(course.getName())
                    .append("，类型：").append(course.getType())
                    .append("，教练：").append(course.getCoachName())
                    .append("，时间：").append(formatDateTime(course.getStartTime()))
                    .append(" - ").append(formatDateTime(course.getEndTime()))
                    .append("，剩余名额：").append(remaining)
                    .append("/")
                    .append(capacity)
                    .append("\n");
        }
        return builder.toString();
    }

    private String buildMyAppointmentReply(Long memberId) {
        if (memberId == null) {
            return "没有找到当前会员信息。";
        }
        List<Appointment> appointments = appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getMemberId, memberId));
        List<AppointmentVO> appointmentList = gymQueryService.toAppointmentVO(appointments);
        if (appointmentList.isEmpty()) {
            return "你目前没有预约记录。";
        }

        StringBuilder builder = new StringBuilder("你的预约记录如下：\n");
        for (AppointmentVO appointment : appointmentList) {
            builder.append("- ").append(appointment.getCourseName())
                    .append("，教练：").append(appointment.getCoachName())
                    .append("，上课时间：").append(formatDateTime(appointment.getCourseTime()))
                    .append("，状态：").append("reserved".equals(appointment.getStatus()) ? "已预约" : "已取消")
                    .append("\n");
        }
        return builder.toString();
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(DATE_TIME_FORMATTER);
    }

    private String buildTodayCourseAppointmentContext() {
        LocalDate today = LocalDate.now();
        List<CourseVO> todayCourses = courseMapper.getCourseList().stream()
                .filter(course -> course.getStartTime() != null && today.equals(course.getStartTime().toLocalDate()))
                .toList();
        List<Appointment> appointments = appointmentMapper.selectList(null);
        Set<Long> todayCourseIds = todayCourses.stream().map(CourseVO::getId).collect(Collectors.toSet());
        List<Appointment> todayAppointments = appointments.stream()
                .filter(appointment -> todayCourseIds.contains(appointment.getCourseId()))
                .toList();
        Map<Long, Long> reservedCountMap = todayAppointments.stream()
                .filter(appointment -> "reserved".equals(appointment.getStatus()))
                .collect(Collectors.groupingBy(Appointment::getCourseId, Collectors.counting()));
        Map<Long, Long> canceledCountMap = todayAppointments.stream()
                .filter(appointment -> "canceled".equals(appointment.getStatus()))
                .collect(Collectors.groupingBy(Appointment::getCourseId, Collectors.counting()));

        int totalCapacity = todayCourses.stream()
                .map(CourseVO::getCapacity)
                .filter(capacity -> capacity != null)
                .mapToInt(Integer::intValue)
                .sum();
        int reservedCount = todayAppointments.stream()
                .filter(appointment -> "reserved".equals(appointment.getStatus()))
                .mapToInt(appointment -> 1)
                .sum();
        int canceledCount = todayAppointments.stream()
                .filter(appointment -> "canceled".equals(appointment.getStatus()))
                .mapToInt(appointment -> 1)
                .sum();

        StringBuilder builder = new StringBuilder();
        builder.append("【系统真实业务数据】\n");
        builder.append("数据日期：").append(today).append("\n");
        builder.append("数据来源：course、appointment 表。\n");
        builder.append("回答要求：只能基于以下数据分析，不能编造不存在的课程、教练、会员人数、短信提醒、待确认状态或其他数据。\n");
        builder.append("今日课程数：").append(todayCourses.size()).append("\n");
        builder.append("今日已预约人次：").append(reservedCount).append("\n");
        builder.append("今日已取消人次：").append(canceledCount).append("\n");
        builder.append("今日总容量：").append(totalCapacity).append("\n");

        if (todayCourses.isEmpty()) {
            builder.append("今日没有课程数据。请直接说明系统中没有查询到今日课程和预约数据，不要生成虚假的分析报告。\n");
            return builder.toString();
        }

        builder.append("今日课程明细：\n");
        for (CourseVO course : todayCourses) {
            long courseReservedCount = reservedCountMap.getOrDefault(course.getId(), 0L);
            long courseCanceledCount = canceledCountMap.getOrDefault(course.getId(), 0L);
            int capacity = course.getCapacity() == null ? 0 : course.getCapacity();
            int remaining = Math.max(capacity - (int) courseReservedCount, 0);
            builder.append("- 课程ID：").append(course.getId())
                    .append("，课程名称：").append(course.getName())
                    .append("，类型：").append(course.getType())
                    .append("，教练：").append(course.getCoachName())
                    .append("，开始时间：").append(course.getStartTime())
                    .append("，结束时间：").append(course.getEndTime())
                    .append("，容量：").append(capacity)
                    .append("，已预约：").append(courseReservedCount)
                    .append("，已取消：").append(courseCanceledCount)
                    .append("，剩余名额：").append(remaining)
                    .append("，课程状态：").append(course.getStatus() != null && course.getStatus() == 1 ? "可预约" : "停课")
                    .append("\n");
        }
        return builder.toString();
    }
}
