package com.yjx.gymmanager.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Coach {
    private Long id;
    private String name;
    private String phone;
    private String specialty;
    private LocalDate entryDate;
    private Integer status;
}
