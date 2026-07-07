package com.yjx.gymmanager.vo;

import com.yjx.gymmanager.entity.Appointment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class AppointmentVO {
    private Long id;
    private Long memberId;
    private String memberName;
    private Long courseId;
    private String courseName;
    private String coachName;
    private LocalDateTime courseTime;
    private String status;
    private LocalDateTime createTime;

    public static AppointmentVO from(Appointment appointment) {
        AppointmentVO vo = new AppointmentVO();
        BeanUtils.copyProperties(appointment, vo);
        return vo;
    }
}
