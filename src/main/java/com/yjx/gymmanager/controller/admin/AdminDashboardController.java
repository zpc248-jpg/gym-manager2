package com.yjx.gymmanager.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.entity.Appointment;
import com.yjx.gymmanager.mapper.AppointmentMapper;
import com.yjx.gymmanager.mapper.CoachMapper;
import com.yjx.gymmanager.mapper.CourseMapper;
import com.yjx.gymmanager.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final MemberMapper memberMapper;
    private final CoachMapper coachMapper;
    private final CourseMapper courseMapper;
    private final AppointmentMapper appointmentMapper;

    @GetMapping
    public Result<Map<String, Long>> dashboard() {
        Map<String, Long> data = new HashMap<>();
        data.put("memberCount", memberMapper.selectCount(null));
        data.put("coachCount", coachMapper.selectCount(null));
        data.put("courseCount", courseMapper.selectCount(null));
        data.put("appointmentCount", appointmentMapper.selectCount(new LambdaQueryWrapper<Appointment>().eq(Appointment::getStatus, "reserved")));
        return Result.ok(data);
    }
}
