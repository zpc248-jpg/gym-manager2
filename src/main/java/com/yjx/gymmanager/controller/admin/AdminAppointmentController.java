package com.yjx.gymmanager.controller.admin;

import com.yjx.gymmanager.common.Result;
import com.yjx.gymmanager.mapper.AppointmentMapper;
import com.yjx.gymmanager.service.AppointmentService;
import com.yjx.gymmanager.service.GymQueryService;
import com.yjx.gymmanager.vo.AppointmentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {
    private final AppointmentMapper appointmentMapper;
    private final GymQueryService gymQueryService;
    private final AppointmentService appointmentService;

    @GetMapping
    public Result<List<AppointmentVO>> list() {
        return Result.ok(gymQueryService.toAppointmentVO(appointmentMapper.selectList(null)));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        appointmentService.cancel(id, null, true);
        return Result.ok();
    }
}
