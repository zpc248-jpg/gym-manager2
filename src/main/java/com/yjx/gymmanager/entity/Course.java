package com.yjx.gymmanager.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Course {
    private Long id;
    private String name;
    private String type;
    private Long coachId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;
    private Integer capacity;
    private Integer bookedCount;
    private Integer status;
}
