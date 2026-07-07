package com.yjx.gymmanager.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Course {
    private Long id;
    private String name;
    private String type;
    private Long coachId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private Integer bookedCount;
    private Integer status;
}
