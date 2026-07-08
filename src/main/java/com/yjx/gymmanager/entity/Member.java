package com.yjx.gymmanager.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireTime;
    private Integer status;
}
