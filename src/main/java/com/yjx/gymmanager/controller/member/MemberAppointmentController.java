package com.yjx.gymmanager.controller.member;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yjx.gymmanager.common.CurrentUser;
import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.common.UserContext;
import com.yjx.gymmanager.dto.ReserveRequest;
import com.yjx.gymmanager.entity.Appointment;
import com.yjx.gymmanager.mapper.AppointmentMapper;
import com.yjx.gymmanager.service.AppointmentService;
import com.yjx.gymmanager.service.GymQueryService;
import com.yjx.gymmanager.vo.AppointmentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/member/appointments")
@RequiredArgsConstructor
public class MemberAppointmentController {
    private final AppointmentMapper appointmentMapper;
    private final GymQueryService gymQueryService;
    private final AppointmentService appointmentService;

    @GetMapping
    public Result<List<AppointmentVO>> list() {
        CurrentUser user = UserContext.get();
        List<Appointment> appointments = appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getMemberId, user.getRelatedId()));
        return Result.ok(gymQueryService.toAppointmentVO(appointments));
    }

    @PostMapping
    public Result<Void> reserve(@RequestBody ReserveRequest request) {
        CurrentUser user = UserContext.get();
        appointmentService.reserve(user.getRelatedId(), request.getCourseId());
        return Result.ok();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        CurrentUser user = UserContext.get();
        appointmentService.cancel(id, user.getRelatedId(), false);
        return Result.ok();
    }
}
