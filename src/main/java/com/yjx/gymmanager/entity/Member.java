package com.yjx.gymmanager.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Member {
    private Long id;
    private String name;
    private String phone;
    private String gender;
    private Integer age;
    private String cardType;
    private LocalDate expireTime;
    private Integer status;
}
